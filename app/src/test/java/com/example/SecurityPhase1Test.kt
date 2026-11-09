package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.User
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SecurityPhase1Test {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    @Test
    fun `test regular players cannot toggle or become admin`() = runBlocking {
        val repo = BattleArenaRepository()
        val regularPlayer = User(
            uid = "player_123",
            displayName = "Regular Gamer",
            email = "gamer@gmail.com",
            freeFireUID = "1234567890",
            freeFireName = "GamerOne",
            isAdmin = false
        )
        repo.setCurrentUser(regularPlayer)
        assertFalse("Player must not be owner", repo.currentUser.value?.isOwner == true)
        assertFalse("Player must not be admin initially", repo.currentUser.value?.isAdmin == true)

        // Attempt to toggle admin mode
        repo.toggleAdminMode()

        assertFalse("Regular player must NOT be able to toggle admin mode", repo.currentUser.value?.isAdmin == true)
    }

    @Test
    fun `test only super admin can toggle admin mode`() = runBlocking {
        val repo = BattleArenaRepository()
        val ownerUser = User(
            uid = "owner_uid",
            displayName = "Battle Arena Owner",
            email = BATTLE_ARENA_OWNER_EMAIL,
            freeFireUID = "9999999999",
            freeFireName = "OwnerBoss",
            isAdmin = false
        )
        repo.setCurrentUser(ownerUser)
        assertTrue("User must be identified as Owner", repo.currentUser.value?.isOwner == true)

        // Toggle admin mode as Owner
        repo.toggleAdminMode()
        assertTrue("Owner must be able to toggle admin mode to active", repo.currentUser.value?.isAdmin == true)

        repo.toggleAdminMode()
        assertFalse("Owner must be able to toggle admin mode back to inactive", repo.currentUser.value?.isAdmin == true)
    }

    @Test
    fun `test regular players cannot approve or reject add-money deposit requests`() = runBlocking {
        val repo = BattleArenaRepository()
        val regularPlayer = User(
            uid = "player_123",
            displayName = "Regular Gamer",
            email = "gamer@gmail.com",
            freeFireUID = "1234567890",
            freeFireName = "GamerOne",
            isAdmin = false
        )
        repo.setCurrentUser(regularPlayer)

        // Submit add money request
        val (submitted, _) = repo.submitAddMoneyRequest(100, "UPI", "UTR123456789012", "Test Deposit")
        assertTrue("Deposit request should be submitted", submitted)

        val pendingReq = repo.addMoneyRequests.value.first()

        // Attempt approval by regular player
        val (approveRes, approveMsg) = repo.approveAddMoneyRequest(pendingReq.id)
        assertFalse("Regular player must NOT be allowed to approve deposits", approveRes)
        assertTrue("Error message should mention security/owner", approveMsg.contains("Security violation", ignoreCase = true) || approveMsg.contains("Owner", ignoreCase = true))

        // Attempt rejection by regular player
        val (rejectRes, rejectMsg) = repo.rejectAddMoneyRequest(pendingReq.id, "Malicious attempt")
        assertFalse("Regular player must NOT be allowed to reject deposits", rejectRes)
        assertTrue("Error message should mention security/owner", rejectMsg.contains("Security violation", ignoreCase = true) || rejectMsg.contains("Owner", ignoreCase = true))
    }

    @Test
    fun `test regular players cannot approve or pay or reject withdrawals`() = runBlocking {
        val repo = BattleArenaRepository()
        val regularPlayer = User(
            uid = "player_123",
            displayName = "Regular Gamer",
            email = "gamer@gmail.com",
            freeFireUID = "1234567890",
            freeFireName = "GamerOne",
            isAdmin = false
        )
        repo.setCurrentUser(regularPlayer)

        // Attempt approval on withdrawal
        val (approveRes, _) = repo.approveWithdrawal("WTH-1001")
        assertFalse("Regular player cannot approve withdrawal", approveRes)

        val (paidRes, _) = repo.markWithdrawalPaid("WTH-1001", "UTR999999")
        assertFalse("Regular player cannot mark withdrawal as paid", paidRes)

        val (rejectRes, _) = repo.rejectWithdrawal("WTH-1001", "Declined")
        assertFalse("Regular player cannot reject withdrawal", rejectRes)
    }

    @Test
    fun `test regular players cannot publish or hide room credentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val regularPlayer = User(
            uid = "player_123",
            displayName = "Regular Gamer",
            email = "gamer@gmail.com",
            freeFireUID = "1234567890",
            freeFireName = "GamerOne",
            isAdmin = false
        )
        repo.setCurrentUser(regularPlayer)

        val tourneys = repo.tournaments.value
        val tourneyId = tourneys.first().id

        val (pubRes, pubMsg) = repo.updateRoomDetails(tourneyId, "999888", "1234")
        assertFalse("Regular player must NOT be allowed to update room details", pubRes)
        assertTrue("Message must indicate authorization failure", pubMsg.contains("Unauthorized", ignoreCase = true))

        val (hideRes, hideMsg) = repo.hideRoomDetails(tourneyId)
        assertFalse("Regular player must NOT be allowed to hide room details", hideRes)
        assertTrue("Message must indicate authorization failure", hideMsg.contains("Unauthorized", ignoreCase = true))
    }

    @Test
    fun `test unauthorized users cannot read room credentials via getSecureRoomCredentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val regularPlayer = User(
            uid = "unauthorized_player",
            displayName = "Spectator",
            email = "spectator@gmail.com",
            freeFireUID = "5555555555",
            freeFireName = "SpectatorFF",
            isAdmin = false
        )
        repo.setCurrentUser(regularPlayer)

        val tourneys = repo.tournaments.value
        val tourney = tourneys.first()

        // User has not joined this tournament
        val (roomId, roomPass) = repo.getSecureRoomCredentials(tourney.id, regularPlayer.uid)
        assertNull("Unauthorized user must receive null room ID", roomId)
        assertNull("Unauthorized user must receive null room password", roomPass)
    }

    @Test
    fun `test super admin can access room credentials via getSecureRoomCredentials`() = runBlocking {
        val repo = BattleArenaRepository()
        val ownerUser = User(
            uid = "owner_uid",
            displayName = "Battle Arena Owner",
            email = BATTLE_ARENA_OWNER_EMAIL,
            freeFireUID = "9999999999",
            freeFireName = "OwnerBoss",
            isAdmin = true
        )
        repo.setCurrentUser(ownerUser)

        val tourneys = repo.tournaments.value
        val tourney = tourneys.first()

        val (roomId, roomPass) = repo.getSecureRoomCredentials(tourney.id, ownerUser.uid)
        assertEquals("Super admin should retrieve actual room ID", tourney.roomId, roomId)
        assertEquals("Super admin should retrieve actual room password", tourney.roomPassword, roomPass)
    }
}
