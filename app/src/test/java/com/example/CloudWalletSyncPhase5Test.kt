package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.GameMode
import com.example.model.LevelVerificationStatus
import com.example.model.PaymentApprovalStatus
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.model.TransactionStatus
import com.example.model.TransactionType
import com.example.model.User
import com.example.model.Wallet
import com.example.model.WithdrawalStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

class CloudWalletSyncPhase5Test {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    private fun createTestUser(
        id: String = "P-USER-1",
        email: String = "p1@freefire.in",
        name: String = "Pro Gamer",
        ffUid: String = "2948100001",
        level: Int = 25,
        isAdmin: Boolean = false
    ): User {
        return User(
            uid = id,
            displayName = name,
            email = email,
            freeFireName = "⚡$name⚡",
            freeFireUID = ffUid,
            freeFireLevel = level,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED,
            isAdmin = isAdmin
        )
    }

    // -------------------------------------------------------------
    // SCENARIO 1: Successful match entry fee deduction
    // -------------------------------------------------------------
    @Test
    fun `scenario 1 - test successful match entry fee deduction using 20 percent bonus hierarchy`() = runBlocking {
        val repo = BattleArenaRepository()
        val user = createTestUser("USER-001", ffUid = "2948101111")
        
        // Initial wallet: deposit 50, bonus 10, winning 0 (total = 60)
        val initialWallet = Wallet(
            userId = user.id,
            depositBalance = 50,
            winningBalance = 0,
            bonusBalance = 10
        )
        repo.firestoreManager.saveUser(user)
        repo.firestoreManager.saveWallet(initialWallet)
        repo.setCurrentUser(user)

        val tourney = repo.createTournament(
            titleEn = "Lone Wolf Cash Duel",
            titleBn = "লোন উলফ ডুয়েল",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "10:00 PM"
        )

        val (joinSuccess, msg) = repo.joinTournament(tourney.id)
        assertTrue("Expected join to succeed: $msg", joinSuccess)

        // Fee = 25: 20% of 25 = 5 max bonus. Bonus used: 5 -> remaining bonus = 5.
        // Remaining fee = 20 -> deducted from deposit -> remaining deposit = 30.
        val localWallet = repo.currentWallet.value
        assertEquals(30, localWallet.depositBalance)
        assertEquals(5, localWallet.bonusBalance)
        assertEquals(0, localWallet.winningBalance)
        assertEquals(35, localWallet.totalBalance)
    }

    // -------------------------------------------------------------
    // SCENARIO 2: Firestore wallet balance updated correctly
    // -------------------------------------------------------------
    @Test
    fun `scenario 2 - test firestore cloud wallet balance updated correctly`() = runBlocking {
        val repo = BattleArenaRepository()
        val user = createTestUser("USER-002", ffUid = "2948102222")

        val initialWallet = Wallet(
            userId = user.id,
            depositBalance = 60,
            winningBalance = 20,
            bonusBalance = 10
        )
        repo.firestoreManager.saveUser(user)
        repo.firestoreManager.saveWallet(initialWallet)
        repo.setCurrentUser(user)

        val tourney = repo.createTournament(
            titleEn = "Clash Squad 4v4",
            titleBn = "ক্ল্যাশ স্কোয়াড",
            mode = GameMode.CLASH_SQUAD,
            entryFee = 30,
            startTimeFormatted = "11:00 PM"
        )

        val (joinSuccess, _) = repo.joinTournament(tourney.id)
        assertTrue(joinSuccess)

        // Fee = 30: 20% of 30 = 6 max bonus. Bonus used: 6 -> remaining bonus = 4.
        // Remaining fee = 24 -> deducted from deposit (60 - 24 = 36).
        // Total = 36 + 20 + 4 = 60.
        val cloudWallet = FirestoreManager.getCloudWalletForTesting(user.id)
        assertNotNull("Cloud wallet must be saved", cloudWallet)
        assertEquals(36, cloudWallet!!.depositBalance)
        assertEquals(20, cloudWallet.winningBalance)
        assertEquals(4, cloudWallet.bonusBalance)
        assertEquals(60, cloudWallet.totalBalance)

        val cloudUser = FirestoreManager.getCloudUserForTesting(user.id)
        assertNotNull("Cloud user must exist", cloudUser)
        assertEquals(60, cloudUser!!.walletBalance)
    }

    // -------------------------------------------------------------
    // SCENARIO 3: Transaction history updated correctly
    // -------------------------------------------------------------
    @Test
    fun `scenario 3 - test transaction history recorded with entry fee details`() = runBlocking {
        val repo = BattleArenaRepository()
        val user = createTestUser("USER-003", ffUid = "2948103333")

        val initialWallet = Wallet(userId = user.id, depositBalance = 80, winningBalance = 0, bonusBalance = 10)
        repo.firestoreManager.saveUser(user)
        repo.firestoreManager.saveWallet(initialWallet)
        repo.setCurrentUser(user)

        val tourney = repo.createTournament(
            titleEn = "Battle Royale Squad",
            titleBn = "ব্যাটল রয়্যাল",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 40,
            startTimeFormatted = "08:00 PM"
        )

        val (joinSuccess, _) = repo.joinTournament(tourney.id)
        assertTrue(joinSuccess)

        // Verify local transactions
        val localTx = repo.transactions.value.find { it.referenceId == tourney.id }
        assertNotNull("Local transaction must exist for tournament", localTx)
        assertEquals(TransactionType.ENTRY_FEE, localTx!!.type)
        assertEquals(40, localTx.amount)
        assertEquals(TransactionStatus.SUCCESS, localTx.status)

        // Verify cloud transactions
        val cloudTxs = FirestoreManager.getCloudTransactionsForTesting(user.id)
        val cloudTx = cloudTxs.find { it.referenceId == tourney.id }
        assertNotNull("Cloud transaction must exist in cloud store", cloudTx)
        assertEquals(TransactionType.ENTRY_FEE, cloudTx!!.type)
        assertEquals(40, cloudTx.amount)
        assertEquals(TransactionStatus.SUCCESS, cloudTx.status)
    }

    // -------------------------------------------------------------
    // SCENARIO 4: App reinstall wallet restoration
    // -------------------------------------------------------------
    @Test
    fun `scenario 4 - test app reinstall restores authoritative cloud wallet balance`() = runBlocking {
        val repoDevice1 = BattleArenaRepository()
        val user = createTestUser("USER-004", ffUid = "2948104444")

        // User starts with 50 + 10 = 60, joins match (fee 20), balance becomes 44
        val initialWallet = Wallet(userId = user.id, depositBalance = 50, winningBalance = 0, bonusBalance = 10)
        repoDevice1.firestoreManager.saveUser(user)
        repoDevice1.firestoreManager.saveWallet(initialWallet)
        repoDevice1.setCurrentUser(user)

        val tourney = repoDevice1.createTournament(
            titleEn = "Lone Wolf Match",
            titleBn = "লোন উলফ",
            mode = GameMode.LONE_WOLF,
            entryFee = 20,
            startTimeFormatted = "07:00 PM"
        )
        repoDevice1.joinTournament(tourney.id)
        assertEquals(40, repoDevice1.currentWallet.value.totalBalance)

        // Simulate app reinstall by instantiating a completely new Repository
        val repoDeviceReinstalled = BattleArenaRepository()
        // Stale local default wallet before cloud restore
        assertFalse(repoDeviceReinstalled.currentWallet.value.userId == user.id)

        // Restore player session from cloud
        val restored = repoDeviceReinstalled.restoreUserFromCloud(user.id)
        assertTrue("Expected cloud user restore to succeed", restored)

        // Authoritative cloud wallet must be restored accurately
        assertEquals(user.id, repoDeviceReinstalled.currentUser.value?.id)
        assertEquals(40, repoDeviceReinstalled.currentWallet.value.totalBalance)
        assertEquals(34, repoDeviceReinstalled.currentWallet.value.depositBalance)
        assertEquals(6, repoDeviceReinstalled.currentWallet.value.bonusBalance)
        assertTrue(repoDeviceReinstalled.transactions.value.any { it.referenceId == tourney.id })
    }

    // -------------------------------------------------------------
    // SCENARIO 5: Login from another device
    // -------------------------------------------------------------
    @Test
    fun `scenario 5 - test login from another device supersedes stale local cache`() = runBlocking {
        val repo1 = BattleArenaRepository()
        val user = createTestUser("USER-005", ffUid = "2948105555")

        val initialWallet = Wallet(userId = user.id, depositBalance = 100, winningBalance = 50, bonusBalance = 10)
        repo1.firestoreManager.saveUser(user)
        repo1.firestoreManager.saveWallet(initialWallet)
        repo1.setCurrentUser(user)

        // User wins a prize on Device 1: winning balance increased by 80
        val prizeTx = com.example.model.Transaction(
            id = "TX-PRIZE-101",
            userId = user.id,
            type = TransactionType.MATCH_WINNING,
            amount = 80,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis()
        )
        repo1.firestoreManager.creditWinnerPrize(user.id, 80, prizeTx)

        // Verify Device 1 cloud wallet has 100 deposit + 130 winning + 10 bonus = 240
        val cloudW = FirestoreManager.getCloudWalletForTesting(user.id)
        assertEquals(240, cloudW!!.totalBalance)

        // Now user logs in from Device 2
        val repoDevice2 = BattleArenaRepository()
        val (restoredUser, restoredWallet, restoredTxs) = repoDevice2.firestoreManager.restoreOrRegisterUser(
            authUid = user.id,
            email = user.email,
            name = user.name
        )
        repoDevice2.restorePlayerSession(restoredUser, restoredWallet, restoredTxs)

        assertEquals(240, repoDevice2.currentWallet.value.totalBalance)
        assertEquals(130, repoDevice2.currentWallet.value.winningBalance)
        assertEquals(100, repoDevice2.currentWallet.value.depositBalance)
        assertTrue(repoDevice2.transactions.value.any { it.id == "TX-PRIZE-101" })
    }

    // -------------------------------------------------------------
    // SCENARIO 6: Duplicate Join request blocked without double deduction
    // -------------------------------------------------------------
    @Test
    fun `scenario 6 - test duplicate join request blocked without double deduction`() = runBlocking {
        val repo = BattleArenaRepository()
        val user = createTestUser("USER-006", ffUid = "2948106666")

        val initialWallet = Wallet(userId = user.id, depositBalance = 100, winningBalance = 0, bonusBalance = 10)
        repo.firestoreManager.saveUser(user)
        repo.firestoreManager.saveWallet(initialWallet)
        repo.setCurrentUser(user)

        val tourney = repo.createTournament(
            titleEn = "Lone Wolf Duel #6",
            titleBn = "লোন উলফ #৬",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "06:00 PM"
        )

        // First join succeeds
        val (join1, msg1) = repo.joinTournament(tourney.id)
        assertTrue(join1)
        val balanceAfterFirst = repo.currentWallet.value.totalBalance

        // Second join attempt must be blocked
        val (join2, msg2) = repo.joinTournament(tourney.id)
        assertFalse("Second join must be rejected", join2)
        assertTrue(msg2.contains("Already joined"))

        // Wallet balance must NOT be deducted again
        assertEquals("Balance must remain unchanged after duplicate attempt", balanceAfterFirst, repo.currentWallet.value.totalBalance)
        assertEquals(balanceAfterFirst, FirestoreManager.getCloudWalletForTesting(user.id)!!.totalBalance)
    }

    // -------------------------------------------------------------
    // SCENARIO 7: Network failure during payment handled gracefully
    // -------------------------------------------------------------
    @Test
    fun `scenario 7 - test network failure during payment aborts registration safely`() = runBlocking {
        val repo = BattleArenaRepository()
        val user = createTestUser("USER-007", ffUid = "2948107777")

        val initialWallet = Wallet(userId = user.id, depositBalance = 50, winningBalance = 0, bonusBalance = 10)
        repo.firestoreManager.saveUser(user)
        repo.firestoreManager.saveWallet(initialWallet)
        repo.setCurrentUser(user)

        val tourney = repo.createTournament(
            titleEn = "Network Test Match",
            titleBn = "নেটওয়ার্ক টেস্ট",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "05:00 PM"
        )

        // Simulate network timeout to Firestore backend
        FirestoreManager.simulateNetworkFailure = true

        val (joinSuccess, errorMsg) = repo.joinTournament(tourney.id)
        assertFalse("Join must fail when network fails", joinSuccess)
        assertTrue("Error must mention payment/network failure: $errorMsg", errorMsg.contains("Payment transaction failed"))

        // Reset network simulation
        FirestoreManager.simulateNetworkFailure = false

        // Wallet balance must NOT have been deducted
        assertEquals(60, repo.currentWallet.value.totalBalance)
        assertEquals(60, FirestoreManager.getCloudWalletForTesting(user.id)!!.totalBalance)

        // Player must NOT be registered in the tournament
        val tourneyDoc = repo.tournaments.value.find { it.id == tourney.id }!!
        assertFalse("Player must NOT be registered in tournament", tourneyDoc.isUserJoined(user.id))

        // No ENTRY_FEE transaction must exist
        assertFalse(repo.transactions.value.any { it.referenceId == tourney.id })
    }

    // -------------------------------------------------------------
    // SCENARIO 8: Concurrent Join attempts safely handled
    // -------------------------------------------------------------
    @Test
    fun `scenario 8 - test concurrent join attempts safely handled without overbooking or double deduction`() = runBlocking {
        val repo = BattleArenaRepository()

        // Create match with only 1 slot remaining
        val tourney = repo.createTournament(
            titleEn = "Single Slot Showdown",
            titleBn = "একক স্লট ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "04:00 PM"
        )

        val userA = createTestUser("USER-8A", ffUid = "2948108881")
        val userB = createTestUser("USER-8B", ffUid = "2948108882")

        val walletA = Wallet(userId = userA.id, depositBalance = 50, winningBalance = 0, bonusBalance = 10)
        val walletB = Wallet(userId = userB.id, depositBalance = 50, winningBalance = 0, bonusBalance = 10)

        repo.firestoreManager.saveUser(userA)
        repo.firestoreManager.saveWallet(walletA)
        repo.firestoreManager.saveUser(userB)
        repo.firestoreManager.saveWallet(walletB)

        // Register userA first so 1 slot remains (totalSlots = 2)
        repo.setCurrentUser(userA)
        val (firstJoin, _) = repo.joinTournament(tourney.id)
        assertTrue(firstJoin)

        // Now, two concurrent threads attempt to join the remaining slot
        val executor = Executors.newFixedThreadPool(2)
        val deferred1 = async(Dispatchers.IO) {
            repo.setCurrentUser(userB)
            repo.joinTournament(tourney.id)
        }
        val deferred2 = async(Dispatchers.IO) {
            // User A trying to join again concurrently
            repo.setCurrentUser(userA)
            repo.joinTournament(tourney.id)
        }

        val results = awaitAll(deferred1, deferred2)
        executor.shutdown()

        // User A's second join is rejected (already joined)
        // User B fills the last slot
        val updatedTourney = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(2, updatedTourney.participants.size)
        assertTrue(updatedTourney.isFull)

        // User A paid once (35 remaining), User B paid once (35 remaining)
        assertEquals(35, FirestoreManager.getCloudWalletForTesting(userA.id)!!.totalBalance)
        assertEquals(35, FirestoreManager.getCloudWalletForTesting(userB.id)!!.totalBalance)
    }

    // -------------------------------------------------------------
    // SCENARIO 9: Insufficient wallet balance rejected
    // -------------------------------------------------------------
    @Test
    fun `scenario 9 - test insufficient wallet balance rejected`() = runBlocking {
        val repo = BattleArenaRepository()
        val user = createTestUser("USER-009", ffUid = "2948109999")

        // Total balance = 10, but fee = 25
        val lowWallet = Wallet(userId = user.id, depositBalance = 0, winningBalance = 0, bonusBalance = 10)
        repo.firestoreManager.saveUser(user)
        repo.firestoreManager.saveWallet(lowWallet)
        repo.setCurrentUser(user)

        val tourney = repo.createTournament(
            titleEn = "High Stakes Match",
            titleBn = "হাই স্টেক্স ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "03:00 PM"
        )

        val (joinSuccess, msg) = repo.joinTournament(tourney.id)
        assertFalse("Join must fail due to insufficient funds", joinSuccess)
        assertTrue("Message must mention insufficient balance: $msg", msg.contains("Insufficient balance"))

        // Wallet must not be modified
        assertEquals(10, repo.currentWallet.value.totalBalance)
        assertEquals(10, FirestoreManager.getCloudWalletForTesting(user.id)!!.totalBalance)

        // No participant added
        val tourneyDoc = repo.tournaments.value.find { it.id == tourney.id }!!
        assertFalse(tourneyDoc.isUserJoined(user.id))
    }

    // -------------------------------------------------------------
    // SCENARIO 10: Existing deposit and withdrawal workflows preserved
    // -------------------------------------------------------------
    @Test
    fun `scenario 10 - test existing deposit and withdrawal workflows preserved and synced`() = runBlocking {
        val repo = BattleArenaRepository()
        val ownerUser = User(
            uid = "OWNER-01",
            displayName = "App Owner",
            email = BATTLE_ARENA_OWNER_EMAIL,
            isAdmin = true
        )
        val playerUser = createTestUser("PLAYER-10", email = "p10@test.com", ffUid = "2948110000")

        val initialPlayerWallet = Wallet(userId = playerUser.id, depositBalance = 20, winningBalance = 150, bonusBalance = 10)
        repo.firestoreManager.saveUser(playerUser)
        repo.firestoreManager.saveWallet(initialPlayerWallet)

        // 1. Player submits Add Money request
        repo.setCurrentUser(playerUser)
        val (addSuccess, _) = repo.submitAddMoneyRequest(
            amount = 100,
            paymentMethod = "UPI",
            utrNumber = "123456789012",
            screenshotNote = "Paid via PhonePe"
        )
        assertTrue(addSuccess)

        // 2. Owner approves Add Money request
        repo.setCurrentUser(ownerUser)
        val pendingReq = repo.addMoneyRequests.value.first()
        val (approveSuccess, _) = repo.approveAddMoneyRequest(pendingReq.id)
        assertTrue(approveSuccess)

        // Switch back to player, verify deposit credited in local and cloud
        repo.setCurrentUser(playerUser)
        kotlinx.coroutines.delay(50)
        assertEquals(120, repo.currentWallet.value.depositBalance)
        assertEquals(120, FirestoreManager.getCloudWalletForTesting(playerUser.id)!!.depositBalance)

        // 3. Player requests Withdrawal of winning balance (₹70 to UPI)
        val (wthSuccess, _) = repo.requestWithdrawal(
            amount = 70,
            method = "UPI",
            destination = "player@upi"
        )
        assertTrue(wthSuccess)
        kotlinx.coroutines.delay(50)

        // Reserved balance should be 70, winning balance decreased from 150 to 80
        assertEquals(80, repo.currentWallet.value.winningBalance)
        assertEquals(70, repo.currentWallet.value.reservedBalance)
        assertEquals(80, FirestoreManager.getCloudWalletForTesting(playerUser.id)!!.winningBalance)
        assertEquals(70, FirestoreManager.getCloudWalletForTesting(playerUser.id)!!.reservedBalance)

        // 4. Owner approves withdrawal and marks paid with Bank UTR
        repo.setCurrentUser(ownerUser)
        kotlinx.coroutines.delay(50)
        val wthReq = repo.withdrawals.value.first()
        repo.approveWithdrawal(wthReq.id)
        val (paidSuccess, _) = repo.markWithdrawalPaid(wthReq.id, "BANKUTR998877")
        assertTrue(paidSuccess)

        // Switch back to player: reserved balance cleared to 0
        repo.setCurrentUser(playerUser)
        kotlinx.coroutines.delay(50)
        assertEquals(0, repo.currentWallet.value.reservedBalance)
        assertEquals(0, FirestoreManager.getCloudWalletForTesting(playerUser.id)!!.reservedBalance)
        val paidTx = repo.transactions.value.find { it.referenceId == "BANKUTR998877" }
        assertNotNull("Payout transaction must be recorded as SUCCESS", paidTx)
        assertEquals(TransactionStatus.SUCCESS, paidTx!!.status)
    }
}
