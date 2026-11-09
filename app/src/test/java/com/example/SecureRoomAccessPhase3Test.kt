package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.AccountStatus
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.GameMode
import com.example.model.LevelVerificationStatus
import com.example.model.PlayerParticipant
import com.example.model.PlayerVerificationStatus
import com.example.model.RoomPublicationStatus
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.model.User
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Phase 3: Secure Room ID and Password Access System Unit Tests.
 * Verifies strict server-side validation, cross-match isolation, Level 10+ eligibility,
 * suspension enforcement, role-based access control, secure subcollection storage,
 * and zero leakage in audit logs or notifications.
 */
class SecureRoomAccessPhase3Test {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    private fun createTestTournament(
        id: String,
        title: String,
        roomId: String? = null,
        roomPass: String? = null,
        roomStatus: RoomPublicationStatus = RoomPublicationStatus.PUBLISHED,
        status: TournamentStatus = TournamentStatus.LIVE,
        participants: List<PlayerParticipant> = emptyList()
    ): Tournament {
        return Tournament(
            id = id,
            titleEn = title,
            titleBn = title,
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            totalPrizePool = 40,
            totalSlots = 2,
            mapName = "Iron Cage",
            status = status,
            roomId = roomId,
            roomPassword = roomPass,
            roomStatus = roomStatus,
            participants = participants
        )
    }

    @Test
    fun `test A - Unregistered player cannot access match credentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-SEC-101"

        // Set up match with published credentials
        val match = createTestTournament(
            id = tournamentId,
            title = "Test Match 101",
            roomId = "ROOM_888999",
            roomPass = "PASS_1234",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.LIVE,
            participants = emptyList()
        )
        repo.addTournament(match)

        // Player not registered
        val unregisteredUser = User(
            uid = "user_outsider",
            displayName = "Outside Player",
            email = "outsider@example.com",
            freeFireLevel = 25,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED,
            isAdmin = false
        )
        repo.setCurrentUser(unregisteredUser)

        val (rId, rPass) = repo.getSecureRoomCredentials(tournamentId, unregisteredUser.uid)
        assertNull("Unregistered user must receive null roomId", rId)
        assertNull("Unregistered user must receive null roomPassword", rPass)
    }

    @Test
    fun `test B - Registered but unpaid or unconfirmed player cannot access credentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-SEC-102"
        val playerUid = "user_unpaid_123"

        val unpaidParticipant = PlayerParticipant(
            userId = playerUid,
            name = "Unpaid Player",
            freeFireName = "UNPAID_FF",
            freeFireUid = "99887766",
            isPaid = false,
            verificationStatus = PlayerVerificationStatus.PENDING_CHECK
        )

        val match = createTestTournament(
            id = tournamentId,
            title = "Test Match 102",
            roomId = "ROOM_555",
            roomPass = "PASS_555",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.LIVE,
            participants = listOf(unpaidParticipant)
        )
        repo.addTournament(match)

        val user = User(
            uid = playerUid,
            displayName = "Unpaid Player",
            email = "unpaid@example.com",
            freeFireLevel = 30,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED
        )
        repo.setCurrentUser(user)

        val (rId, rPass) = repo.getSecureRoomCredentials(tournamentId, playerUid)
        assertNull("Unpaid participant must not receive room ID", rId)
        assertNull("Unpaid participant must not receive room Password", rPass)
    }

    @Test
    fun `test C - Player registered in Match A cannot access Match B credentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val matchAId = "T-MATCH-A"
        val matchBId = "T-MATCH-B"
        val playerUid = "user_player_a"

        val playerAParticipant = PlayerParticipant(
            userId = playerUid,
            name = "Player A",
            freeFireName = "PLAYER_A_FF",
            freeFireUid = "11223344",
            isPaid = true,
            verificationStatus = PlayerVerificationStatus.VERIFIED
        )

        val matchA = createTestTournament(
            id = matchAId,
            title = "Match A (Registered)",
            roomId = "ROOM_AAA",
            roomPass = "PASS_AAA",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.LIVE,
            participants = listOf(playerAParticipant)
        )
        val matchB = createTestTournament(
            id = matchBId,
            title = "Match B (Unregistered)",
            roomId = "ROOM_BBB",
            roomPass = "PASS_BBB",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.LIVE,
            participants = emptyList()
        )

        repo.addTournament(matchA)
        repo.addTournament(matchB)

        val user = User(
            uid = playerUid,
            displayName = "Player A",
            email = "playera@example.com",
            freeFireLevel = 35,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED
        )
        repo.setCurrentUser(user)

        // Eligible for Match A
        val (aRoomId, aRoomPass) = repo.getSecureRoomCredentials(matchAId, playerUid)
        assertEquals("ROOM_AAA", aRoomId)
        assertEquals("PASS_AAA", aRoomPass)

        // Forbidden for Match B
        val (bRoomId, bRoomPass) = repo.getSecureRoomCredentials(matchBId, playerUid)
        assertNull("Cross-match access to Match B must be strictly blocked", bRoomId)
        assertNull("Cross-match access to Match B password must be strictly blocked", bRoomPass)
    }

    @Test
    fun `test D - Unauthorized regular player cannot publish, update, or remove credentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-SEC-104"

        val match = createTestTournament(
            id = tournamentId,
            title = "Admin Protected Match",
            roomId = null,
            roomPass = null,
            roomStatus = RoomPublicationStatus.PENDING,
            status = TournamentStatus.UPCOMING
        )
        repo.addTournament(match)

        // Standard player without owner privileges
        val normalPlayer = User(
            uid = "hacker_wannabe",
            displayName = "Regular Gamer",
            email = "regular@example.com",
            isAdmin = false
        )
        repo.setCurrentUser(normalPlayer)

        val (pubSuccess, pubMsg) = repo.updateRoomDetails(tournamentId, "HACKED_ROOM", "HACKED_PASS")
        assertFalse("Regular player must NOT be able to publish room credentials", pubSuccess)
        assertTrue("Error message must indicate owner authorization required", pubMsg.contains("Unauthorized"))

        val (remSuccess, remMsg) = repo.removeRoomCredentials(tournamentId)
        assertFalse("Regular player must NOT be able to remove credentials", remSuccess)
        assertTrue("Error message must indicate owner authorization required", remMsg.contains("Unauthorized"))
    }

    @Test
    fun `test E - Confirmed eligible participant in unlocked match can access credentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-SEC-105"
        val playerUid = "user_champion_55"

        val confirmedParticipant = PlayerParticipant(
            userId = playerUid,
            name = "Champion Player",
            freeFireName = "CHAMP_FF",
            freeFireUid = "55555555",
            isPaid = true,
            verificationStatus = PlayerVerificationStatus.VERIFIED
        )

        val match = createTestTournament(
            id = tournamentId,
            title = "Championship Match",
            roomId = "ROOM_SECURE_777",
            roomPass = "PASS_SECURE_888",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.ROOM_PUBLISHED,
            participants = listOf(confirmedParticipant)
        )
        repo.addTournament(match)

        val user = User(
            uid = playerUid,
            displayName = "Champion Player",
            email = "champ@example.com",
            freeFireLevel = 45,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED
        )
        repo.setCurrentUser(user)

        val (rId, rPass) = repo.getSecureRoomCredentials(tournamentId, playerUid)
        assertEquals("ROOM_SECURE_777", rId)
        assertEquals("PASS_SECURE_888", rPass)
    }

    @Test
    fun `test F - Suspended or banned player cannot access credentials even if registered`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-SEC-106"
        val playerUid = "user_banned_cheater"

        val participant = PlayerParticipant(
            userId = playerUid,
            name = "Banned Player",
            freeFireName = "BANNED_FF",
            freeFireUid = "66666666",
            isPaid = true,
            verificationStatus = PlayerVerificationStatus.VERIFIED
        )

        val match = createTestTournament(
            id = tournamentId,
            title = "Fair Play Match",
            roomId = "ROOM_CLEAN_100",
            roomPass = "PASS_CLEAN_100",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.LIVE,
            participants = listOf(participant)
        )
        repo.addTournament(match)

        // Temporarily suspended player
        val suspendedUser = User(
            uid = playerUid,
            displayName = "Suspended Gamer",
            email = "suspended@example.com",
            freeFireLevel = 25,
            accountStatus = AccountStatus.TEMPORARILY_SUSPENDED,
            suspensionExpiresAt = System.currentTimeMillis() + 86400000L
        )
        repo.setCurrentUser(suspendedUser)

        val (rId, rPass) = repo.getSecureRoomCredentials(tournamentId, playerUid)
        assertNull("Suspended player must not receive credentials", rId)
        assertNull("Suspended player must not receive password", rPass)
    }

    @Test
    fun `test G - Player with Level under 10 or rejected level verification cannot access credentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-SEC-107"
        val lowLevelUid = "user_noob_level5"

        val participant = PlayerParticipant(
            userId = lowLevelUid,
            name = "Low Level Player",
            freeFireName = "NOOB_FF",
            freeFireUid = "77777777",
            isPaid = true,
            verificationStatus = PlayerVerificationStatus.VERIFIED
        )

        val match = createTestTournament(
            id = tournamentId,
            title = "Level 10 Plus Required Match",
            roomId = "ROOM_PRO_200",
            roomPass = "PASS_PRO_200",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.LIVE,
            participants = listOf(participant)
        )
        repo.addTournament(match)

        // Level 5 player (< Level 10)
        val lowLevelUser = User(
            uid = lowLevelUid,
            displayName = "Low Level Gamer",
            email = "level5@example.com",
            freeFireLevel = 5,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED
        )
        repo.setCurrentUser(lowLevelUser)

        val (rId, rPass) = repo.getSecureRoomCredentials(tournamentId, lowLevelUid)
        assertNull("Player with Level < 10 must be blocked from room credentials", rId)
        assertNull("Player with Level < 10 must be blocked from room password", rPass)

        // Player with rejected level verification
        val rejectedUser = User(
            uid = lowLevelUid,
            displayName = "Fake Level Gamer",
            email = "rejected@example.com",
            freeFireLevel = 20,
            levelVerificationStatus = LevelVerificationStatus.REJECTED
        )
        repo.setCurrentUser(rejectedUser)

        val (rejId, rejPass) = repo.getSecureRoomCredentials(tournamentId, lowLevelUid)
        assertNull("Player with rejected level verification must be blocked", rejId)
        assertNull("Player with rejected level verification must be blocked", rejPass)
    }

    @Test
    fun `test H - Migration preserves credentials in secure storage and makes them accessible`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-LEGACY-MIGRATION"
        val playerUid = "user_migrated_player"

        val participant = PlayerParticipant(
            userId = playerUid,
            name = "Migrated Player",
            freeFireName = "MIGRATED_FF",
            freeFireUid = "88889999",
            isPaid = true,
            verificationStatus = PlayerVerificationStatus.VERIFIED
        )

        val legacyMatch = createTestTournament(
            id = tournamentId,
            title = "Legacy Match to Migrate",
            roomId = "ROOM_LEGACY_900",
            roomPass = "PASS_LEGACY_900",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.LIVE,
            participants = listOf(participant)
        )
        repo.addTournament(legacyMatch)

        // Run migration
        val migratedCount = repo.migrateAllRoomCredentials()
        assertTrue("At least one legacy tournament must be migrated", migratedCount >= 1)

        // Verify credentials in secure storage
        val storedInCloud = FirestoreManager.getCloudRoomForTesting(tournamentId)
        assertNotNull("Credentials must exist in secure cloud private_room store", storedInCloud)
        assertEquals("ROOM_LEGACY_900", storedInCloud?.first)
        assertEquals("PASS_LEGACY_900", storedInCloud?.second)

        // Verify eligible player can fetch them
        val user = User(
            uid = playerUid,
            displayName = "Migrated Gamer",
            email = "migrated@example.com",
            freeFireLevel = 28,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED
        )
        repo.setCurrentUser(user)

        val (rId, rPass) = repo.getSecureRoomCredentials(tournamentId, playerUid)
        assertEquals("ROOM_LEGACY_900", rId)
        assertEquals("PASS_LEGACY_900", rPass)
    }

    @Test
    fun `test I - Admin publishes credentials and logs audit without exposing plaintext password`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-SEC-AUDIT-109"

        val match = createTestTournament(
            id = tournamentId,
            title = "Audit Test Match",
            roomId = null,
            roomPass = null,
            roomStatus = RoomPublicationStatus.PENDING,
            status = TournamentStatus.UPCOMING
        )
        repo.addTournament(match)

        // Setup authorized Super Admin
        val superAdmin = User(
            uid = "admin_super",
            displayName = "Admin Manik",
            email = BATTLE_ARENA_OWNER_EMAIL,
            isAdmin = true
        )
        repo.setCurrentUser(superAdmin)

        val (pubSuccess, pubMsg) = repo.updateRoomDetails(
            tournamentId = tournamentId,
            roomId = "ROOM_AUDIT_300",
            roomPassword = "SUPER_SECRET_PASSWORD_XYZ",
            roomStatus = RoomPublicationStatus.PUBLISHED
        )
        assertTrue("Super Admin must be able to publish credentials", pubSuccess)

        // Verify Admin Audit Log does NOT contain the plaintext password
        val auditLogs = repo.adminAuditLogs.value
        val publishedLog = auditLogs.find { it.targetId == tournamentId && it.action == "ROOM_PUBLISHED" }
        assertNotNull("Admin audit log must be recorded", publishedLog)
        assertFalse(
            "Plaintext password must NEVER appear in admin audit log",
            publishedLog!!.details.contains("SUPER_SECRET_PASSWORD_XYZ")
        )

        // Verify notifications do NOT leak the password
        val notifs = repo.notifications.value
        val matchNotif = notifs.find { it.messageEn.contains("Room ID: ROOM_AUDIT_300") }
        assertNotNull("Notification for published room must be generated", matchNotif)
        assertFalse(
            "Notification message must NEVER contain plaintext password",
            matchNotif!!.messageEn.contains("SUPER_SECRET_PASSWORD_XYZ")
        )
    }

    @Test
    fun `test J - Room removal clears credentials from secure store and denies access`() = runBlocking {
        val repo = BattleArenaRepository()
        val tournamentId = "T-SEC-REMOVE-110"
        val playerUid = "user_active_player"

        val participant = PlayerParticipant(
            userId = playerUid,
            name = "Active Player",
            freeFireName = "ACTIVE_FF",
            freeFireUid = "12121212",
            isPaid = true,
            verificationStatus = PlayerVerificationStatus.VERIFIED
        )

        val match = createTestTournament(
            id = tournamentId,
            title = "Removal Test Match",
            roomId = "ROOM_WILL_BE_REMOVED",
            roomPass = "PASS_WILL_BE_REMOVED",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            status = TournamentStatus.LIVE,
            participants = listOf(participant)
        )
        repo.addTournament(match)

        val superAdmin = User(
            uid = "admin_super",
            displayName = "Admin Manik",
            email = BATTLE_ARENA_OWNER_EMAIL,
            isAdmin = true
        )
        repo.setCurrentUser(superAdmin)

        // Remove credentials
        val (remSuccess, remMsg) = repo.removeRoomCredentials(tournamentId)
        assertTrue("Super Admin must successfully remove credentials", remSuccess)

        // Now player checks credentials
        val playerUser = User(
            uid = playerUid,
            displayName = "Active Gamer",
            email = "active@example.com",
            freeFireLevel = 30,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED
        )
        repo.setCurrentUser(playerUser)

        val (rId, rPass) = repo.getSecureRoomCredentials(tournamentId, playerUid)
        assertNull("Removed room ID must return null", rId)
        assertNull("Removed room password must return null", rPass)
    }
}
