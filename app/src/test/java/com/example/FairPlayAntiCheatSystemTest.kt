package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.AccountStatus
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.CheatCategory
import com.example.model.IncidentReportStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FairPlayAntiCheatSystemTest {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    @Test
    fun `test player report system requires valid Match ID, target Player ID and reason`() = runBlocking {
        val repo = BattleArenaRepository()
        val allPlayers = repo.allPlayers.value
        val suspect = allPlayers.first { !it.isOwner }

        // Test missing match ID
        val res1 = repo.reportPlayerCheat(
            matchId = "",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.AIMBOT_AUTO_AIM,
            reason = "Instant headshot through wall"
        )
        assertFalse("Should fail without match ID", res1.first)

        // Test missing target player ID
        val res2 = repo.reportPlayerCheat(
            matchId = "T-BR-101",
            targetPlayerId = "",
            category = CheatCategory.AIMBOT_AUTO_AIM,
            reason = "Instant headshot through wall"
        )
        assertFalse("Should fail without target player ID", res2.first)

        // Test short reason (< 5 chars)
        val res3 = repo.reportPlayerCheat(
            matchId = "T-BR-101",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.AIMBOT_AUTO_AIM,
            reason = "hack"
        )
        assertFalse("Should fail with too short reason", res3.first)

        // Test successful report
        val res4 = repo.reportPlayerCheat(
            matchId = "T-BR-101",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.AIMBOT_AUTO_AIM,
            reason = "Spectated player locking onto heads through obstacles",
            evidenceUrl = "https://youtube.com/watch?v=sample123"
        )
        assertTrue("Valid report should succeed: ${res4.second}", res4.first)

        val reports = repo.incidentReports.value
        val createdReport = reports.find { it.targetPlayerId == suspect.playerId }
        assertNotNull("Report must be added to repository", createdReport)
        assertEquals("Match ID should match", "T-BR-101", createdReport?.tournamentId)
        assertEquals("Category should match", CheatCategory.AIMBOT_AUTO_AIM, createdReport?.category)
        assertEquals("Status should initially be PENDING", IncidentReportStatus.PENDING, createdReport?.reportStatus)
    }

    @Test
    fun `test anti-spam cooldown prevents duplicate spam reports within 5 minutes`() = runBlocking {
        val repo = BattleArenaRepository()
        val suspect = repo.allPlayers.value.first { !it.isOwner }

        val res1 = repo.reportPlayerCheat(
            matchId = "T-CS-201",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.WALLHACK_ESP,
            reason = "Pre-firing corners before anyone peeks"
        )
        assertTrue("First report must succeed", res1.first)

        // Second report immediately afterwards for same match & suspect
        val res2 = repo.reportPlayerCheat(
            matchId = "T-CS-201",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.WALLHACK_ESP,
            reason = "Repeated report immediately submitted"
        )
        assertFalse("Spam report within cooldown must be blocked", res2.first)
        assertTrue("Feedback should mention spam prevention: ${res2.second}", res2.second.contains("Spam", ignoreCase = true) || res2.second.contains("already", ignoreCase = true))
    }

    @Test
    fun `test fair enforcement ensures player is NOT auto-banned on report alone`() = runBlocking {
        val repo = BattleArenaRepository()
        val suspect = repo.allPlayers.value.first { !it.isOwner }

        repo.reportPlayerCheat(
            matchId = "T-LW-301",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.SPEED_HACK,
            reason = "Moving across Lone Wolf map in 1 second"
        )

        // Verify player account status remains ACTIVE (no auto-ban on suspicion alone)
        val refreshedSuspect = repo.allPlayers.value.find { it.id == suspect.id }
        assertNotNull(refreshedSuspect)
        assertEquals("Player must remain ACTIVE until admin verifies violation", AccountStatus.ACTIVE, refreshedSuspect?.accountStatus)
    }

    @Test
    fun `test admin review workflow allows setting under review, verified violation, or dismissed`() = runBlocking {
        val repo = BattleArenaRepository()
        val suspect = repo.allPlayers.value.first { !it.isOwner }

        repo.reportPlayerCheat(
            matchId = "T-BR-102",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.NO_RECOIL_DAMAGE_MOD,
            reason = "M4A1 zero recoil laser from 200m away"
        )

        val report = repo.incidentReports.value.first { it.targetPlayerId == suspect.playerId }

        // 1. Move to UNDER_REVIEW
        val reviewRes1 = repo.reviewCheatReport(
            reportId = report.id,
            newStatus = IncidentReportStatus.UNDER_REVIEW,
            adminNotes = "Spectator video footage requested from custom room host"
        )
        assertTrue("Admin can put report under review", reviewRes1.first)
        assertEquals(IncidentReportStatus.UNDER_REVIEW, repo.incidentReports.value.first { it.id == report.id }.reportStatus)

        // 2. Verified violation with suspension
        val reviewRes2 = repo.reviewCheatReport(
            reportId = report.id,
            newStatus = IncidentReportStatus.VERIFIED_VIOLATION,
            adminNotes = "Cross-referenced bullet spread logs. Verified third-party modification script.",
            actionType = "SUSPEND",
            suspensionHours = 72
        )
        assertTrue("Admin can verify violation and apply penalty", reviewRes2.first)

        val updatedSuspect = repo.allPlayers.value.first { it.id == suspect.id }
        assertEquals("Player should now be TEMPORARILY_SUSPENDED", AccountStatus.TEMPORARILY_SUSPENDED, updatedSuspect.accountStatus)

        // Verify action is recorded in permanent Admin Audit Log
        val auditLogs = repo.adminAuditLogs.value
        val hasAuditEntry = auditLogs.any { it.action == "CHEAT_REPORT_REVIEWED" && it.targetId == suspect.id }
        assertTrue("Admin review decision must be recorded in permanent audit logs", hasAuditEntry)
    }

    @Test
    fun `test match monitoring detects duplicate inconsistent match results`() = runBlocking {
        val repo = BattleArenaRepository()
        val suspect = repo.allPlayers.value.first { !it.isOwner }

        // Flag inconsistent result
        repo.flagInconsistentResult(
            matchId = "T-BR-888",
            userId = suspect.id,
            reason = "Duplicate Rank 1 claim",
            details = "Two different squads claimed Rank 1 in the same custom room"
        )

        val incident = repo.incidentReports.value.find { it.tournamentId == "T-BR-888" }
        assertNotNull("System must generate an incident report for duplicate result", incident)
        assertEquals("Category must be FAKE_RESULT_SUBMISSION", CheatCategory.FAKE_RESULT_SUBMISSION, incident?.category)
    }

    @Test
    fun `test player appeal workflow allows submitting appeal and admin reversal`() = runBlocking {
        val repo = BattleArenaRepository()
        val suspect = repo.allPlayers.value.first { !it.isOwner }

        // 1. File and verify a violation report
        repo.reportPlayerCheat(
            matchId = "T-BR-999",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.TEAMING,
            reason = "Two players didn't shoot each other in solo final circle"
        )
        val report = repo.incidentReports.value.first { it.targetPlayerId == suspect.playerId }

        repo.reviewCheatReport(
            reportId = report.id,
            newStatus = IncidentReportStatus.VERIFIED_VIOLATION,
            adminNotes = "Teaming reported in Lone Wolf",
            actionType = "SUSPEND",
            suspensionHours = 24
        )

        // 2. Player logs in as suspect to submit appeal
        repo.setCurrentUser(suspect)

        val appealRes = repo.submitPlayerAppeal(
            reportId = report.id,
            explanation = "I was reloading behind gloo wall and ran out of ammo; I did not team with the opponent. Here is my screen recording.",
            proofUrl = "https://drive.google.com/sample_appeal_proof"
        )
        assertTrue("Player appeal should be accepted: ${appealRes.second}", appealRes.first)

        val reportWithAppeal = repo.incidentReports.value.first { it.id == report.id }
        assertNotNull("Report must now have an attached appeal", reportWithAppeal.appeal)
        assertEquals("Appeal status must be PENDING", "PENDING", reportWithAppeal.appeal?.status)

        // 3. Admin logs back in to review and accept the appeal
        val owner = repo.allPlayers.value.first { it.isOwner }
        repo.setCurrentUser(owner)

        val reviewAppealRes = repo.reviewPlayerAppeal(
            reportId = report.id,
            acceptAppeal = true,
            reviewNotes = "Screen recording reviewed; legitimate ammo depletion confirmed. Penalty lifted."
        )
        assertTrue("Admin can accept appeal: ${reviewAppealRes.second}", reviewAppealRes.first)

        // 4. Verify player is restored to ACTIVE and report status is DISMISSED
        val restoredSuspect = repo.allPlayers.value.first { it.id == suspect.id }
        assertEquals("Player should be restored to ACTIVE upon accepted appeal", AccountStatus.ACTIVE, restoredSuspect.accountStatus)

        val resolvedReport = repo.incidentReports.value.first { it.id == report.id }
        assertEquals("Report status should be DISMISSED", IncidentReportStatus.DISMISSED, resolvedReport.reportStatus)
        assertEquals("Appeal status should be ACCEPTED", "ACCEPTED", resolvedReport.appeal?.status)
    }
}
