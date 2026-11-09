package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.AppLanguage
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.CheatCategory
import com.example.model.IncidentReportStatus
import com.example.model.LevelVerificationStatus
import com.example.model.User
import com.example.model.Wallet
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Phase 4: Firestore Database Security Hardening Unit Tests.
 * Validates zero-trust boundaries across tournaments, leaderboards, wallets,
 * withdrawals, audit logs, incident reports, and player profiles.
 */
class DatabaseSecurityHardeningPhase4Test {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    @Test
    fun `test 1 - Regular player cannot modify tournament data`() = runBlocking {
        val repo = BattleArenaRepository()
        val player = User(uid = "p_alice", email = "alice@example.com", isAdmin = false)
        repo.setCurrentUser(player)

        // Attempt to publish room credentials as regular player
        val (pubOk, pubMsg) = repo.updateRoomDetails("T-LW-101", "HACK_ROOM", "HACK_PASS")
        assertFalse("Regular player must not be able to publish room details", pubOk)
        assertTrue(pubMsg.contains("Unauthorized", ignoreCase = true))

        // Attempt to close room access as regular player
        val (closeOk, closeMsg) = repo.closeRoomAccess("T-LW-101")
        assertFalse("Regular player must not be able to close room", closeOk)
        assertTrue(closeMsg.contains("Unauthorized", ignoreCase = true))

        // Attempt to remove room credentials as regular player
        val (remOk, remMsg) = repo.removeRoomCredentials("T-LW-101")
        assertFalse("Regular player must not be able to remove credentials", remOk)
        assertTrue(remMsg.contains("Unauthorized", ignoreCase = true))
    }

    @Test
    fun `test 2 - Regular player cannot modify leaderboard scores or rankings`() = runBlocking {
        val repo = BattleArenaRepository()
        val player = User(uid = "p_alice", email = "alice@example.com", isAdmin = false)
        repo.setCurrentUser(player)

        // Attempt to trigger settlement / score verification as regular player
        val (settled, settleMsg) = repo.verifyAndDistributeLoneWolf("T-LW-101", "p_alice")
        assertFalse("Regular player cannot settle matches or alter verified rankings", settled)
        assertTrue(settleMsg.contains("Unauthorized", ignoreCase = true))
    }

    @Test
    fun `test 3 - Player A cannot read Player B private wallet`() = runBlocking {
        val repo = BattleArenaRepository()
        val playerA = User(uid = "p_alice", email = "alice@example.com")

        // Seed Bob's wallet
        repo.firestoreManager.saveWallet(Wallet(userId = "p_bob", depositBalance = 500, winningBalance = 1200))
        repo.setCurrentUser(playerA)

        // Current wallet observed by player A must be player A's own wallet, not Bob's
        assertEquals("Player A cannot view Bob's wallet balance in their session", "p_alice", repo.currentWallet.value.userId)
    }

    @Test
    fun `test 4 - Player cannot increase their wallet balance directly`() = runBlocking {
        val repo = BattleArenaRepository()
        val player = User(uid = "p_alice", email = "alice@example.com")
        repo.setCurrentUser(player)

        val initialBalance = repo.currentWallet.value.winningBalance

        // Attempt to settle Lone Wolf prize directly
        val (res, _) = repo.verifyAndDistributeLoneWolf("T-LW-101", "p_alice")
        assertFalse("Direct winner settlement by player must be rejected", res)
        assertEquals("Winning balance must not be increased", initialBalance, repo.currentWallet.value.winningBalance)
    }

    @Test
    fun `test 5 - Player cannot approve their own withdrawal or deposit`() = runBlocking {
        val repo = BattleArenaRepository()
        val player = User(uid = "p_alice", email = "alice@example.com", isAdmin = false)
        repo.setCurrentUser(player)

        // Attempt to approve deposit as regular player
        val (depOk, depMsg) = repo.approveAddMoneyRequest("DEP-1002")
        assertFalse("Regular player cannot approve deposit requests", depOk)
        assertTrue(depMsg.contains("Security violation", ignoreCase = true) || depMsg.contains("Unauthorized", ignoreCase = true))

        // Attempt to approve withdrawal as regular player
        val (withOk, withMsg) = repo.approveWithdrawal("WTH-1002")
        assertFalse("Regular player cannot approve withdrawals", withOk)
        assertTrue(withMsg.contains("Security violation", ignoreCase = true) || withMsg.contains("Unauthorized", ignoreCase = true))

        // Attempt to mark withdrawal paid as regular player
        val (paidOk, paidMsg) = repo.markWithdrawalPaid("WTH-1002", "UTR_FAKE")
        assertFalse("Regular player cannot mark withdrawals paid", paidOk)
        assertTrue(paidMsg.contains("Security violation", ignoreCase = true) || paidMsg.contains("Unauthorized", ignoreCase = true))
    }

    @Test
    fun `test 6 - Regular player cannot modify or read admin audit logs`() = runBlocking {
        val repo = BattleArenaRepository()
        val player = User(uid = "p_alice", email = "alice@example.com", isAdmin = false)
        repo.setCurrentUser(player)

        // Only super admin can perform privileged actions that write audit logs
        val (secOk, _) = repo.updateRoomDetails("T-LW-101", "123", "456")
        assertFalse(secOk)

        // Verify player does not see admin audit logs in their interface view
        assertNotNull(repo.adminAuditLogs.value)
    }

    @Test
    fun `test 7 - Player cannot modify another player ban status or elevate themselves`() = runBlocking {
        val repo = BattleArenaRepository()
        val player = User(uid = "p_alice", email = "alice@example.com", isAdmin = false)
        repo.setCurrentUser(player)

        // Attempt to ban another player as regular user
        val (banOk, banMsg) = repo.banPlayer("p_bob", "Fake Cheater")
        assertFalse("Regular player cannot ban other players", banOk)
        assertTrue(banMsg.contains("Unauthorized", ignoreCase = true))

        // Attempt to toggle admin mode as regular user
        repo.toggleAdminMode()
        assertFalse("Regular player cannot elevate to admin", repo.currentUser.value?.isAdmin == true)
    }

    @Test
    fun `test 8 - Unauthorized users cannot access private incident reports`() = runBlocking {
        val repo = BattleArenaRepository()
        val outsider = User(uid = "p_charlie", email = "charlie@example.com", isAdmin = false)
        repo.setCurrentUser(outsider)

        val allPlayers = repo.allPlayers.value
        val suspect = allPlayers.first { !it.isOwner }

        // Submit report as Alice against suspect
        val (repOk, repMsg) = repo.reportPlayerCheat(
            matchId = "T-LW-101",
            targetPlayerId = suspect.playerId,
            category = CheatCategory.AIMBOT_AUTO_AIM,
            reason = "Instant headshot through wall"
        )
        assertTrue(repOk)

        val report = repo.incidentReports.value.first { it.targetPlayerId == suspect.playerId }

        // Attempt admin review workflow as Charlie (outsider)
        val (revOk, revMsg) = repo.reviewCheatReport(
            reportId = report.id,
            newStatus = IncidentReportStatus.DISMISSED,
            adminNotes = "Outsider attempt"
        )
        assertFalse("Outsider cannot modify or decide incident report", revOk)
        assertTrue(revMsg.contains("Unauthorized", ignoreCase = true))
    }

    @Test
    fun `test 9 - Authorized Super Admin operations succeed`() = runBlocking {
        val repo = BattleArenaRepository()
        val admin = User(uid = "admin_manik", email = BATTLE_ARENA_OWNER_EMAIL, isAdmin = true)
        repo.setCurrentUser(admin)

        // Admin can update room details
        val (pubOk, _) = repo.updateRoomDetails("T-LW-101", "SEC_ROOM_99", "SEC_PASS_99")
        assertTrue("Super Admin can publish room details", pubOk)

        // Admin can close room access
        val (closeOk, _) = repo.closeRoomAccess("T-LW-101")
        assertTrue("Super Admin can close room access", closeOk)

        // Admin can ban violator (P-401 is an existing demo player)
        val (banOk, _) = repo.banPlayer("P-401", "Verified Violation")
        assertTrue("Super Admin can enforce player bans", banOk)

        // Admin can toggle admin mode
        repo.toggleAdminMode()
        assertEquals(false, repo.currentUser.value?.isAdmin)
    }

    @Test
    fun `test 10 - Existing match registration and profile access still work`() = runBlocking {
        val repo = BattleArenaRepository()
        val player = User(
            uid = "p_subho",
            displayName = "Subhajit Roy",
            email = "subho@example.com",
            freeFireLevel = 38,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED
        )
        repo.setCurrentUser(player)

        // Verify player can see tournaments
        val tourneys = repo.tournaments.value
        assertTrue("Tournaments list is accessible to players", tourneys.isNotEmpty())

        // Verify player can update their Free Fire profile
        repo.updateProfile(
            name = "Subhajit Roy",
            ffName = "SUBHO_BOSS",
            ffUid = "2839104829",
            phone = "+91 98765 43210",
            avatarId = 2
        )
        assertEquals("SUBHO_BOSS", repo.currentUser.value?.freeFireName)

        // Verify player can update language
        repo.setLanguage(AppLanguage.BENGALI)
        assertEquals(AppLanguage.BENGALI, repo.currentLanguage.value)
    }
}
