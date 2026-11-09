package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AccountRestrictedException
import com.example.data.AuthenticationRepository
import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.AccountStatus
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.User
import com.example.model.Wallet
import com.example.viewmodel.BattleArenaViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AdminPlayerBanSystemTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        FirestoreManager.resetCloudDatabaseForTesting()
        context.getSharedPreferences("battle_arena_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun `admin can search players using permanent Player ID, FF UID, and Name`() = runBlocking {
        val repo = BattleArenaRepository()
        val allPlayers = repo.allPlayers.value
        assertTrue("Players registry must be populated", allPlayers.isNotEmpty())

        val targetPlayer = allPlayers.first { !it.isOwner }
        val permanentId = targetPlayer.playerId
        val ffUid = targetPlayer.freeFireUid

        // Search by permanent Player ID
        val resultsById = repo.searchPlayers(permanentId)
        assertTrue("Should find player by permanent Player ID", resultsById.any { it.playerId == permanentId })

        // Search by Free Fire UID
        val resultsByUid = repo.searchPlayers(ffUid)
        assertTrue("Should find player by Free Fire UID", resultsByUid.any { it.freeFireUid == ffUid })

        // Search by Name substring
        val nameQuery = targetPlayer.name.take(4)
        val resultsByName = repo.searchPlayers(nameQuery)
        assertTrue("Should find player by Name", resultsByName.any { it.id == targetPlayer.id })
    }

    @Test
    fun `admin can set temporary suspension duration and reason, blocking tournament entry`() = runBlocking {
        val repo = BattleArenaRepository()
        val target = repo.allPlayers.value.first { !it.isOwner }
        val durationHours = 48
        val reason = "Macro script detected in room match"

        val (success, msg) = repo.suspendPlayer(
            targetPlayerId = target.id,
            durationHours = durationHours,
            reason = reason,
            adminPerformer = BATTLE_ARENA_OWNER_EMAIL
        )
        assertTrue(success)
        assertTrue(msg.contains("temporarily suspended"))

        val updatedPlayer = repo.allPlayers.value.find { it.id == target.id }
        assertNotNull(updatedPlayer)
        assertEquals(AccountStatus.TEMPORARILY_SUSPENDED, updatedPlayer!!.effectiveAccountStatus)
        assertEquals(durationHours, updatedPlayer.suspensionDurationHours)
        assertEquals(reason, updatedPlayer.banReason)
        assertEquals(BATTLE_ARENA_OWNER_EMAIL, updatedPlayer.bannedByAdmin)
        assertNotNull(updatedPlayer.suspensionExpiresAt)
        assertTrue("Suspension must be active", updatedPlayer.isSuspensionActive)
        assertFalse("Eligible for tournament must be false", updatedPlayer.isEligibleForTournament)

        // Attempt to join a tournament must be strictly blocked
        repo.syncCloudUser(updatedPlayer)
        val tournament = repo.tournaments.value.first()
        val (joinSuccess, joinMsg) = repo.joinTournament(tournament.id)
        assertFalse("Suspended player must not be allowed to join tournament", joinSuccess)
        assertTrue("Error message must explain suspension status", joinMsg.contains("suspended") || joinMsg.contains("blocked"))
    }

    @Test
    fun `permanent ban requires reason and blocks tournament entry while preserving all data`() = runBlocking {
        val repo = BattleArenaRepository()
        val target = repo.allPlayers.value.first { !it.isOwner }
        val originalPlayerId = target.playerId
        val originalWallet = target.walletBalance
        val reason = "Injecting third-party ESP cheat"

        val (success, msg) = repo.permanentlyBanPlayer(
            targetPlayerId = target.id,
            reason = reason,
            adminPerformer = BATTLE_ARENA_OWNER_EMAIL
        )
        assertTrue(success)
        assertTrue(msg.contains("permanently banned"))

        val bannedPlayer = repo.allPlayers.value.find { it.id == target.id }
        assertNotNull(bannedPlayer)
        assertEquals(AccountStatus.PERMANENTLY_BANNED, bannedPlayer!!.effectiveAccountStatus)
        assertTrue(bannedPlayer.isBanned)
        assertEquals(reason, bannedPlayer.banReason)
        assertEquals(BATTLE_ARENA_OWNER_EMAIL, bannedPlayer.bannedByAdmin)
        assertTrue("Suspension must be active for perm ban", bannedPlayer.isSuspensionActive)

        // CRITICAL: Original Player ID and wallet records are NEVER deleted because of a ban
        assertEquals(originalPlayerId, bannedPlayer.playerId)
        assertEquals(originalWallet, bannedPlayer.walletBalance)

        // Attempt to join tournament is strictly blocked
        repo.syncCloudUser(bannedPlayer)
        val tournament = repo.tournaments.value.first()
        val (joinSuccess, joinMsg) = repo.joinTournament(tournament.id)
        assertFalse("Permanently banned player must not join tournaments", joinSuccess)
        assertTrue("Error message must indicate ban status", joinMsg.contains("banned") || joinMsg.contains("suspended"))
    }

    @Test
    fun `suspended and banned accounts are blocked from logging in with clear notice`() = runBlocking {
        val repo = BattleArenaRepository()
        val authRepo = repo.authRepository
        val testEmail = "cheater.pro@gmail.com"
        val testPass = "Pass1234!"

        // Register user
        val regResult = authRepo.signUpWithEmail(testEmail, testPass, "Cheater Pro", "CHEATER_FF", "9918273645")
        assertTrue(regResult.isSuccess)
        val registeredUser = regResult.getOrThrow()
        val playerId = registeredUser.playerId

        // Add to registry and Admin suspends player
        repo.updatePlayerInRegistry(registeredUser)
        repo.suspendPlayer(
            targetPlayerId = registeredUser.id,
            durationHours = 72,
            reason = "Unauthorized third-party plugin",
            adminPerformer = BATTLE_ARENA_OWNER_EMAIL
        )

        // Simulate sign out
        authRepo.signOut()
        assertNull("Authenticated user should be null after sign out", authRepo.authenticatedUser.value)

        // Attempt re-login with suspended account
        val loginResult = authRepo.signInWithEmail(testEmail, testPass)
        assertFalse("Login must be blocked for suspended account", loginResult.isSuccess)

        val exception = loginResult.exceptionOrNull()
        assertNotNull(exception)
        assertTrue("Exception must indicate ACCOUNT_RESTRICTED", exception!!.message!!.contains("ACCOUNT_RESTRICTED"))
        assertTrue("Exception must mention Player ID", exception.message!!.contains(playerId))
        assertTrue("Exception must mention suspension reason", exception.message!!.contains("Unauthorized third-party plugin"))

        // lastRestrictedUser must be held to display notice dialog
        val restricted = authRepo.lastRestrictedUser.value
        assertNotNull(restricted)
        assertEquals(playerId, restricted!!.playerId)
        assertEquals(AccountStatus.TEMPORARILY_SUSPENDED, restricted.effectiveAccountStatus)
        assertEquals("Unauthorized third-party plugin", restricted.banReason)
        assertNull("Current authenticated user must remain null", authRepo.authenticatedUser.value)
    }

    @Test
    fun `authorized admin can unban player restoring account to active status`() = runBlocking {
        val repo = BattleArenaRepository()
        val target = repo.allPlayers.value.first { !it.isOwner }

        // First ban player
        repo.permanentlyBanPlayer(target.id, "Violation under review", BATTLE_ARENA_OWNER_EMAIL)
        var player = repo.allPlayers.value.find { it.id == target.id }!!
        assertEquals(AccountStatus.PERMANENTLY_BANNED, player.effectiveAccountStatus)

        // Now authorized Admin unbans player
        val (unbanSuccess, unbanMsg) = repo.unbanPlayer(target.id, BATTLE_ARENA_OWNER_EMAIL)
        assertTrue(unbanSuccess)
        assertTrue(unbanMsg.contains("unbanned") || unbanMsg.contains("Active"))

        player = repo.allPlayers.value.find { it.id == target.id }!!
        assertEquals(AccountStatus.ACTIVE, player.effectiveAccountStatus)
        assertFalse(player.isBanned)
        assertNull(player.banReason)
        assertNull(player.suspensionExpiresAt)
        assertFalse("Suspension must no longer be active", player.isSuspensionActive)
    }

    @Test
    fun `all ban, suspension, and unban actions are recorded in permanent Admin Audit Log with performer`() = runBlocking {
        val repo = BattleArenaRepository()
        val target = repo.allPlayers.value.first { !it.isOwner }
        val adminEmail = BATTLE_ARENA_OWNER_EMAIL

        // Suspend
        repo.suspendPlayer(target.id, 24, "AFK in tournament match", adminEmail)
        val suspendLog = repo.adminAuditLogs.value.find { it.action == "PLAYER_SUSPENDED" && it.targetId == target.id }
        assertNotNull("Audit log must record PLAYER_SUSPENDED", suspendLog)
        assertEquals(adminEmail, suspendLog!!.performedBy)
        assertTrue(suspendLog.details.contains("24h"))

        // Unban
        repo.unbanPlayer(target.id, adminEmail)
        val unbanLog = repo.adminAuditLogs.value.find { it.action == "PLAYER_UNBANNED" && it.targetId == target.id }
        assertNotNull("Audit log must record PLAYER_UNBANNED", unbanLog)
        assertEquals(adminEmail, unbanLog!!.performedBy)

        // Permanent Ban
        repo.permanentlyBanPlayer(target.id, "Severe wallhack injection", adminEmail)
        val banLog = repo.adminAuditLogs.value.find { it.action == "PLAYER_BANNED" && it.targetId == target.id }
        assertNotNull("Audit log must record PLAYER_BANNED", banLog)
        assertEquals(adminEmail, banLog!!.performedBy)
        assertTrue(banLog.details.contains("Severe wallhack injection"))
    }

    @Test
    fun `unauthorized non-admin users cannot change player account status`() = runBlocking {
        val repo = BattleArenaRepository()
        val target = repo.allPlayers.value.first { !it.isOwner }

        // Pretend ordinary user is caller
        val unauthorizedUserEmail = "randomplayer@gmail.com"
        val (success, errorMsg) = repo.suspendPlayer(
            targetPlayerId = target.id,
            durationHours = 24,
            reason = "I don't like this player",
            adminPerformer = unauthorizedUserEmail
        )

        assertFalse("Unauthorized user must not be allowed to suspend players", success)
        assertTrue("Error must explain unauthorized violation", errorMsg.contains("Unauthorized") || errorMsg.contains("Admins"))

        // Target status should remain unchanged
        val player = repo.allPlayers.value.find { it.id == target.id }!!
        assertEquals(AccountStatus.ACTIVE, player.effectiveAccountStatus)
    }

    @Test
    fun `super admin owner account cannot be banned or suspended`() = runBlocking {
        val repo = BattleArenaRepository()
        val ownerPlayer = repo.allPlayers.value.first { it.isOwner }

        val (suspendSuccess, suspendMsg) = repo.suspendPlayer(
            targetPlayerId = ownerPlayer.id,
            durationHours = 24,
            reason = "Attempting to suspend owner",
            adminPerformer = BATTLE_ARENA_OWNER_EMAIL
        )
        assertFalse("Owner cannot be suspended", suspendSuccess)
        assertTrue(suspendMsg.contains("cannot be suspended") || suspendMsg.contains("Owner"))

        val (banSuccess, banMsg) = repo.permanentlyBanPlayer(
            targetPlayerId = ownerPlayer.id,
            reason = "Attempting to ban owner",
            adminPerformer = BATTLE_ARENA_OWNER_EMAIL
        )
        assertFalse("Owner cannot be banned", banSuccess)
        assertTrue(banMsg.contains("cannot be permanently banned") || banMsg.contains("Owner"))
    }
}
