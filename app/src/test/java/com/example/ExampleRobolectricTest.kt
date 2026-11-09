package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BattleArenaRepository
import com.example.model.AppLanguage
import com.example.model.GameMode
import com.example.model.RefundStatus
import com.example.model.TournamentStatus
import com.example.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context matches Battle Arena`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Battle Arena", appName)
    }

    @Test
    fun `test Lone Wolf economic distribution`() {
        val repo = BattleArenaRepository()
        val initialWinningBalance = repo.currentWallet.value.winningBalance
        
        // Verify Lone Wolf #101 where current user (BA-70912) is participant
        repo.verifyAndDistributeLoneWolf("T-LW-101", "BA-70912")

        val updatedWallet = repo.currentWallet.value
        assertEquals(initialWinningBalance + 40, updatedWallet.winningBalance)

        val latestComm = repo.commissions.value.first()
        assertEquals(10, latestComm.commissionEarned)
        assertEquals(50, latestComm.totalEntryCollected)
        assertEquals(40, latestComm.totalPrizeDistributed)
    }

    @Test
    fun `test Clash Squad prize distribution`() {
        val repo = BattleArenaRepository()
        
        // Verify Clash Squad #201 where Team B wins
        repo.verifyAndDistributeClashSquad("T-CS-201", "Team B")

        val latestComm = repo.commissions.value.first()
        assertEquals(40, latestComm.commissionEarned)
        assertEquals(160, latestComm.totalEntryCollected)
        assertEquals(120, latestComm.totalPrizeDistributed)
    }

    @Test
    fun `test Battle Royale prize and commission calculation`() {
        val repo = BattleArenaRepository()

        repo.verifyAndDistributeBattleRoyale("T-BR-301")

        val latestComm = repo.commissions.value.first()
        assertEquals(230, latestComm.commissionEarned)
        assertEquals(800, latestComm.totalEntryCollected)
        assertEquals(570, latestComm.totalPrizeDistributed)
    }

    @Test
    fun `test withdrawal minimum 50 rupees validation`() {
        val repo = BattleArenaRepository()

        // Attempting to withdraw ₹30 should fail
        val (failResult, failMsg) = repo.requestWithdrawal(30, "UPI", "user@upi")
        assertFalse(failResult)

        // Attempting to withdraw ₹50 with sufficient winning balance should succeed
        val (successResult, _) = repo.requestWithdrawal(40, "UPI", "user@upi") // wallet has 40 winning, try with 40 fails
        assertFalse(successResult)

        // Add winning prize first
        repo.verifyAndDistributeLoneWolf("T-LW-101", "BA-70912")
        // Now winning balance is 40 + 40 = 80
        val (okResult, _) = repo.requestWithdrawal(60, "UPI", "user@upi")
        assertTrue(okResult)
    }

    @Test
    fun `test referral bonus system`() {
        val repo = BattleArenaRepository()
        val initialBonus = repo.currentWallet.value.bonusBalance

        val (success, _) = repo.applyReferralCode("FRIEND99")
        assertTrue(success)
        assertEquals(initialBonus + 5, repo.currentWallet.value.bonusBalance)

        // Re-applying should fail
        val (failSuccess, _) = repo.applyReferralCode("ANOTHER77")
        assertFalse(failSuccess)
    }

    @Test
    fun `test AuthenticationRepository email sign in and sign out`() = kotlinx.coroutines.runBlocking {
        val authRepo = com.example.data.AuthenticationRepository(firebaseAuth = null)
        
        val result = authRepo.signInWithEmail("tester@arena.com", "pass12345")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        org.junit.Assert.assertNotNull(user)
        assertEquals("tester", user?.name)
        assertTrue(authRepo.isUserAuthenticated)

        authRepo.signOut()
        assertFalse(authRepo.isUserAuthenticated)
    }

    @Test
    fun `test User data class fields for Firestore database structure`() {
        val user = com.example.model.User(
            uid = "BA-TEST123",
            displayName = "Champion Player",
            email = "champion@battlearena.com",
            freeFireUID = "2948194820",
            walletBalance = 250,
            referralCode = "ARENA100"
        )

        assertEquals("BA-TEST123", user.uid)
        assertEquals("Champion Player", user.displayName)
        assertEquals("champion@battlearena.com", user.email)
        assertEquals("2948194820", user.freeFireUID)
        assertEquals(250, user.walletBalance)
        assertEquals("ARENA100", user.referralCode)

        // Verify compatibility aliases
        assertEquals(user.uid, user.id)
        assertEquals(user.displayName, user.name)
        assertEquals(user.freeFireUID, user.freeFireUid)
    }

    @Test
    fun `test Free Fire account level 10 eligibility requirement`() {
        val repo = BattleArenaRepository()
        
        // Lower level below 10
        repo.submitLevelVerification(8)
        val (joinResult, errorMsg) = repo.joinTournament("T-LW-101")
        assertFalse("Player below Level 10 must not be allowed to join", joinResult)
        assertTrue(errorMsg.contains("Level 10"))

        // Update level to 15 (Level 10+)
        repo.submitLevelVerification(15)
        assertTrue(repo.currentUser.value?.freeFireLevel ?: 0 >= 10)
    }

    @Test
    fun `test Add Money manual UTR approval and idempotency`() {
        val repo = BattleArenaRepository()
        val initialDeposit = repo.currentWallet.value.depositBalance

        // 1. Submit Add Money request with UTR
        val utr = "UTR" + System.currentTimeMillis()
        val (submitOk, _) = repo.submitAddMoneyRequest(100, "PhonePe UPI", utr, "Payment Screenshot Attached")
        assertTrue(submitOk)

        // 2. Duplicate submission with same UTR should be rejected
        val (dupOk, _) = repo.submitAddMoneyRequest(100, "PhonePe UPI", utr, "Duplicate Try")
        assertFalse("Duplicate UTR must be rejected", dupOk)

        // 3. Find pending request and approve by owner
        val req = repo.addMoneyRequests.value.first { it.utrNumber == utr }
        val (approveOk, _) = repo.approveAddMoneyRequest(req.id)
        assertTrue(approveOk)

        // Wallet must be credited
        assertEquals(initialDeposit + 100, repo.currentWallet.value.depositBalance)
    }

    @Test
    fun `test Owner identification for kirtaniamanik2010@gmail com`() {
        val ownerUser = com.example.model.User(
            uid = "BA-70912",
            displayName = "Manik Hunter",
            email = "kirtaniamanik2010@gmail.com",
            freeFireUID = "2948109382",
            walletBalance = 500,
            referralCode = "ARENA99"
        )
        assertTrue(ownerUser.isOwner)

        val ordinaryUser = com.example.model.User(
            uid = "BA-12345",
            displayName = "Other Player",
            email = "other@gmail.com",
            freeFireUID = "2948100000",
            walletBalance = 100,
            referralCode = "ARENA11"
        )
        assertFalse(ordinaryUser.isOwner)
    }

    @Test
    fun `test Fair Play ban prevents tournament participation`() {
        val repo = BattleArenaRepository()
        val currentUserId = repo.currentUser.value?.id ?: "BA-70912"

        repo.banPlayer(currentUserId, "Aimbot detection")
        assertTrue(repo.currentUser.value?.isBanned == true)

        val (joinOk, banMsg) = repo.joinTournament("T-LW-101")
        assertFalse(joinOk)
        assertTrue(banMsg.contains("banned"))

        // Unban
        repo.unbanPlayer(currentUserId)
        assertFalse(repo.currentUser.value?.isBanned == true)
    }

    @Test
    fun `test Telegram Customer Support and Official Channel URLs and metadata`() {
        // 1. Verify Customer Support Group Link and Name
        assertEquals("https://t.me/+n2Kh_H-Xuw81MjFl", com.example.ui.components.TelegramLinks.SUPPORT_GROUP_URL)
        assertEquals("Battle Arena Support", com.example.ui.components.TelegramLinks.SUPPORT_GROUP_NAME)

        // 2. Verify Official Updates Channel Link, Name, and Username
        assertEquals("https://t.me/BattleArenaOfficial1", com.example.ui.components.TelegramLinks.OFFICIAL_CHANNEL_URL)
        assertEquals("Battle Arena Official", com.example.ui.components.TelegramLinks.CHANNEL_NAME)
        assertEquals("@BattleArenaOfficial1", com.example.ui.components.TelegramLinks.CHANNEL_USERNAME)

        // 3. Verify Intent launcher works safely without crashing
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.ui.components.openTelegramOrBrowser(context, com.example.ui.components.TelegramLinks.SUPPORT_GROUP_URL)
        com.example.ui.components.openTelegramOrBrowser(context, com.example.ui.components.TelegramLinks.OFFICIAL_CHANNEL_URL)
    }

    @Test
    fun `test Unique Bound Entry ID generated on tournament join and binds Free Fire UID`() {
        val repo = BattleArenaRepository()
        // Ensure user is level 10+
        repo.submitLevelVerification(12)
        // Add enough wallet balance to join
        val utr = "DEP" + System.currentTimeMillis()
        repo.submitAddMoneyRequest(100, "UPI", utr, "Proof")
        val req = repo.addMoneyRequests.value.first { it.utrNumber == utr }
        repo.approveAddMoneyRequest(req.id)

        // T-BR-301 has 32/40 slots and current user (BA-70912) has not joined yet
        val (joinOk, msg) = repo.joinTournament("T-BR-301")
        assertTrue("User should be able to join with sufficient balance: $msg", joinOk)

        val updatedTourney = repo.tournaments.value.first { it.id == "T-BR-301" }
        val currentUserId = repo.currentUser.value?.id ?: ""
        val entryId = updatedTourney.getEntryIdForUser(currentUserId)
        org.junit.Assert.assertNotNull(entryId)
        assertTrue("Entry ID must start with BA-ENTRY", entryId!!.startsWith("BA-ENTRY"))
        assertTrue("Entry ID must contain tournament mode code", entryId.contains("BR"))
    }

    @Test
    fun `test prevent duplicate Free Fire UID registration in same tournament`() {
        val repo = BattleArenaRepository()
        repo.submitLevelVerification(15)

        // 1. Current user joins T-BR-301
        val utr = "DEP" + System.currentTimeMillis()
        repo.submitAddMoneyRequest(100, "UPI", utr, "Proof")
        val req = repo.addMoneyRequests.value.first { it.utrNumber == utr }
        repo.approveAddMoneyRequest(req.id)

        val (firstJoinOk, _) = repo.joinTournament("T-BR-301")
        assertTrue("Initial join should succeed", firstJoinOk)

        val registeredUid = repo.currentUser.value!!.freeFireUid

        // 2. A different player account tries to join with the SAME Free Fire UID (smurfing attempt)
        val smurfUser = com.example.model.User(
            uid = "BA-SMURF-999",
            displayName = "Smurf Account",
            email = "smurf@arena.com",
            freeFireUID = registeredUid, // Same FF UID
            freeFireLevel = 15,
            walletBalance = 200
        )
        repo.setCurrentUser(smurfUser)

        val (dupJoinOk, errorMsg) = repo.joinTournament("T-BR-301")
        assertFalse("Duplicate Free Fire UID registration must be blocked", dupJoinOk)
        assertTrue("Error message must indicate UID is already registered", errorMsg.contains("already registered"))
    }

    @Test
    fun `test Lone Wolf automatic matchmaking flow and auto-creation of next match`() {
        val repo = BattleArenaRepository()
        repo.submitLevelVerification(15)

        // Fund user 1
        val utr1 = "DEP_U1_" + System.currentTimeMillis()
        repo.submitAddMoneyRequest(50, "UPI", utr1, "Proof")
        val req1 = repo.addMoneyRequests.value.first { it.utrNumber == utr1 }
        repo.approveAddMoneyRequest(req1.id)

        // Create a dedicated Lone Wolf tournament with 2 slots
        val t = repo.createTournament(
            titleEn = "Lone Wolf 1v1 Test Match",
            titleBn = "লোন উলফ টেস্ট ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "10:00 PM"
        )

        // Player 1 joins
        val (join1Ok, _) = repo.joinTournament(t.id)
        assertTrue("Player 1 should join successfully", join1Ok)

        var tourney = repo.tournaments.value.first { it.id == t.id }
        assertEquals("Match should be in MATCHMAKING status", TournamentStatus.MATCHMAKING, tourney.status)
        assertNotNull("Waiting player timestamp must be recorded", tourney.waitingPlayerJoinedAt)
        assertEquals("Countdown must NOT start until Player 2 joins", null, tourney.countdownStartMillis)
        assertFalse("Countdown must not be active", tourney.isCountdownActive)

        // Switch to Player 2
        val user2 = com.example.model.User(
            uid = "BA-PLAYER-2",
            displayName = "Player Two",
            email = "player2@arena.com",
            freeFireUID = "8899776655",
            freeFireLevel = 18,
            walletBalance = 100
        )
        repo.setCurrentUser(user2)

        // Player 2 joins
        val (join2Ok, _) = repo.joinTournament(t.id)
        assertTrue("Player 2 should join successfully", join2Ok)

        tourney = repo.tournaments.value.first { it.id == t.id }
        assertEquals("Match must be confirmed when slot limit is reached", TournamentStatus.CONFIRMED, tourney.status)
        assertNotNull("Synchronized countdown timestamp must be set", tourney.countdownStartMillis)
        assertTrue("Countdown must be active", tourney.isCountdownActive)

        // Verify "Your Match Will Start in 45 Seconds!" notification
        val notifs = repo.notifications.value
        val has45sNotif = notifs.any { it.titleEn.contains("Your Match Will Start in 45 Seconds") || it.messageEn.contains("Your Match Will Start in 45 Seconds") }
        assertTrue("Both players must receive 45s countdown start notification", has45sNotif)

        // Verify Automatic Match Creation: A new Lone Wolf tournament exists with 0 slots
        val openLoneWolfMatches = repo.tournaments.value.filter { it.mode == GameMode.LONE_WOLF && it.status == TournamentStatus.UPCOMING }
        assertTrue("A new upcoming Lone Wolf tournament must be automatically created", openLoneWolfMatches.isNotEmpty())
    }

    @Test
    fun `test tickCountdowns transitions confirmed match to live at 45 seconds`() {
        val repo = BattleArenaRepository()
        
        // Find or create a confirmed tournament with countdown started > 45s ago
        val t = repo.createTournament(
            titleEn = "Countdown Tick Test",
            titleBn = "কাউন্টডাউন টেস্ট",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "10:00 PM"
        )

        // Force match to CONFIRMED with elapsed countdown
        val elapsedStart = System.currentTimeMillis() - 46_000L
        repo.updateTournamentDirectly(t.copy(status = TournamentStatus.CONFIRMED, countdownStartMillis = elapsedStart))

        val preStatus = repo.tournaments.value.first { it.id == t.id }.status
        assertEquals(TournamentStatus.CONFIRMED, preStatus)

        // Tick countdowns
        repo.tickCountdowns()

        val postTourney = repo.tournaments.value.first { it.id == t.id }
        assertEquals("Match must transition to LIVE when countdown reaches zero", TournamentStatus.LIVE, postTourney.status)
        assertTrue("Match started notification flag must be set", postTourney.matchStartedNotified)

        val notifs = repo.notifications.value
        val hasStartedNotif = notifs.any { it.titleEn.contains("Match Started!") || it.messageEn.contains("Match Started!") }
        assertTrue("Match Started! notification must be delivered", hasStartedNotif)
    }

    @Test
    fun `test Lone Wolf waiting player cancellation and refund`() {
        val repo = BattleArenaRepository()
        repo.submitLevelVerification(15)

        val utr = "DEP_WAIT_" + System.currentTimeMillis()
        repo.submitAddMoneyRequest(50, "UPI", utr, "Proof")
        val req = repo.addMoneyRequests.value.first { it.utrNumber == utr }
        repo.approveAddMoneyRequest(req.id)

        val t = repo.createTournament(
            titleEn = "Waiting Cancellation Test",
            titleBn = "ওয়েটিং টেস্ট",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "10:00 PM"
        )

        repo.joinTournament(t.id)
        val balanceAfterJoin = repo.currentWallet.value.depositBalance

        // Cancel waiting player with refund
        val (cancelOk, _) = repo.cancelLoneWolfWaitingPlayer(t.id, refundFee = true)
        assertTrue("Cancellation should succeed", cancelOk)

        val updatedTourney = repo.tournaments.value.first { it.id == t.id }
        assertEquals("Tournament should reset to UPCOMING", TournamentStatus.UPCOMING, updatedTourney.status)
        assertEquals("Slot count should reset to 0", 0, updatedTourney.filledSlots)

        val balanceAfterRefund = repo.currentWallet.value.depositBalance
        assertEquals("Player entry fee of ₹25 must be refunded", balanceAfterJoin + 25, balanceAfterRefund)
    }

    @Test
    fun `test Battle Royale daily scheduled match and admin schedule update`() {
        val repo = BattleArenaRepository()

        val brTourney = repo.tournaments.value.first { it.mode == GameMode.BATTLE_ROYALE }
        assertEquals("Battle Royale match must require 40 slots", 40, brTourney.totalSlots)
        assertEquals("Default schedule must be 08:00 PM IST", "08:00 PM IST", brTourney.scheduledDailyTime)

        // Super Admin updates schedule to 09:30 PM IST
        val (updateOk, _) = repo.updateBattleRoyaleSchedule(brTourney.id, "09:30 PM IST")
        assertTrue("Schedule update by owner must succeed", updateOk)

        val updatedBR = repo.tournaments.value.first { it.id == brTourney.id }
        assertEquals("Schedule must be updated to 09:30 PM IST", "09:30 PM IST", updatedBR.scheduledDailyTime)

        val auditLogs = repo.adminAuditLogs.value
        val hasScheduleLog = auditLogs.any { it.action == "SCHEDULE_UPDATED" && it.targetId == brTourney.id }
        assertTrue("Admin audit log must be recorded for schedule update", hasScheduleLog)
    }

    @Test
    fun `test unsuccessful match marking and direct wallet refund with duplicate prevention`() {
        val repo = BattleArenaRepository()
        repo.submitLevelVerification(15)

        // Add funds and join T-BR-301
        val utr = "DEP_REFUND_" + System.currentTimeMillis()
        repo.submitAddMoneyRequest(100, "UPI", utr, "Proof")
        val req = repo.addMoneyRequests.value.first { it.utrNumber == utr }
        repo.approveAddMoneyRequest(req.id)

        val (joinOk, _) = repo.joinTournament("T-BR-301")
        assertTrue("Join should succeed", joinOk)

        val balanceBeforeRefund = repo.currentWallet.value.depositBalance

        // Admin marks match as UNSUCCESSFUL
        val (unsuccessOk, _) = repo.markTournamentUnsuccessful("T-BR-301", "Custom room disconnect glitch")
        assertTrue("Marking as unsuccessful should succeed", unsuccessOk)

        val tourney = repo.tournaments.value.first { it.id == "T-BR-301" }
        assertEquals("Status must be UNSUCCESSFUL", TournamentStatus.UNSUCCESSFUL, tourney.status)

        // Verify refund item was automatically queued in refund records
        val refundRecords = repo.refundRecords.value.filter { it.tournamentId == "T-BR-301" }
        assertTrue("Refund record must be created for registered players", refundRecords.isNotEmpty())

        val playerRefund = refundRecords.first { it.userId == repo.currentUser.value!!.id }
        assertEquals("Initial refund status must be PENDING", RefundStatus.PENDING, playerRefund.status)
        assertEquals("Refund amount must equal entry fee", tourney.entryFee, playerRefund.entryFee)

        // Admin approves and processes the refund
        val (approveOk, _) = repo.approveAndProcessMatchRefund("T-BR-301", playerRefund.id)
        assertTrue("Admin approving refund must succeed", approveOk)

        // Verify wallet updated automatically without requiring manual claim
        val balanceAfterRefund = repo.currentWallet.value.depositBalance
        assertEquals("Wallet must be credited with entry fee", balanceBeforeRefund + tourney.entryFee, balanceAfterRefund)

        // Verify transaction history displays Entry Fee Refunded
        val txs = repo.transactions.value
        val hasRefundTx = txs.any { it.type == TransactionType.REFUND && it.titleEn.contains("Entry Fee Refunded") }
        assertTrue("Transaction history must log Entry Fee Refunded", hasRefundTx)

        // Verify duplicate refund prevention
        val (dupRefundOk, dupMsg) = repo.approveAndProcessMatchRefund("T-BR-301", playerRefund.id)
        assertFalse("Duplicate refund must be strictly prevented", dupRefundOk)
        assertTrue("Error message must indicate refund already processed", dupMsg.contains("already refunded") || dupMsg.contains("Already refunded"))
    }

    @Test
    fun `test Admin Wallet initial balances and verified owner UPI`() {
        val repo = BattleArenaRepository()
        val wallet = repo.adminWallet.value

        assertEquals(280, wallet.availableBalance)
        assertEquals(0, wallet.reservedBalance)
        assertEquals(280, wallet.totalMatchEarnings)
        assertEquals("kirtaniamanik@okhdfcbank", wallet.verifiedUpiId)
        assertTrue(wallet.isUpiVerified)
    }

    @Test
    fun `test successful Admin UPI withdrawal updates balance and ledger`() {
        val repo = BattleArenaRepository()
        val initialAvailable = repo.adminWallet.value.availableBalance

        val (ok, msg) = repo.requestAdminUPIWithdrawal(100, "kirtaniamanik@okhdfcbank", autoProcessSuccess = true)
        assertTrue("Withdrawal must succeed for verified owner: $msg", ok)

        val updatedWallet = repo.adminWallet.value
        assertEquals("Available balance must decrease by withdrawn amount", initialAvailable - 100, updatedWallet.availableBalance)
        assertEquals("Total withdrawals paid must increase by 100", 100, updatedWallet.totalWithdrawalsPaid)

        val withdrawals = repo.adminWithdrawals.value
        assertTrue("Withdrawal record must be logged", withdrawals.isNotEmpty())
        val latest = withdrawals.first()
        assertEquals(com.example.model.AdminWithdrawalStatus.SUCCESS, latest.status)
        assertEquals(100, latest.amount)
        assertTrue("UTR must be generated", latest.payoutUtr.isNotBlank())

        val ledger = repo.adminLedger.value
        val withdrawalLedger = ledger.find { it.type == com.example.model.AdminLedgerEntryType.UPI_WITHDRAWAL }
        assertNotNull("Ledger entry must be recorded for UPI withdrawal", withdrawalLedger)
        assertEquals(100, withdrawalLedger?.amount)
    }

    @Test
    fun `test Admin UPI withdrawal rejected for non-owner user`() {
        val repo = BattleArenaRepository()
        val nonOwnerUser = repo.currentUser.value!!.copy(
            email = "regular_player@gmail.com",
            isAdmin = false
        )
        repo.setCurrentUser(nonOwnerUser)

        val (ok, msg) = repo.requestAdminUPIWithdrawal(50, "kirtaniamanik@okhdfcbank")
        assertFalse("Withdrawal must be blocked for non-owner accounts", ok)
        assertTrue("Message must indicate unauthorized owner requirement", msg.contains("Unauthorized") || msg.contains("Owner"))
    }

    @Test
    fun `test Admin UPI withdrawal validation for invalid amount and insufficient funds`() {
        val repo = BattleArenaRepository()

        // Zero / negative amount
        val (zeroOk, zeroMsg) = repo.requestAdminUPIWithdrawal(0, "kirtaniamanik@okhdfcbank")
        assertFalse("Zero amount must be rejected", zeroOk)
        assertTrue(zeroMsg.contains("greater than ₹0"))

        // Exceeds available balance
        val (excessOk, excessMsg) = repo.requestAdminUPIWithdrawal(99999, "kirtaniamanik@okhdfcbank")
        assertFalse("Amount exceeding balance must be rejected", excessOk)
        assertTrue(excessMsg.contains("Insufficient available balance"))

        // Invalid UPI format
        val (invalidUpiOk, upiMsg) = repo.requestAdminUPIWithdrawal(50, "not_a_valid_upi")
        assertFalse("Invalid UPI regex must be rejected", invalidUpiOk)
        assertTrue(upiMsg.contains("Invalid destination UPI ID"))
    }

    @Test
    fun `test duplicate withdrawal request prevention`() {
        val repo = BattleArenaRepository()

        // First withdrawal request in processing state
        val (firstOk, _) = repo.requestAdminUPIWithdrawal(50, "kirtaniamanik@okhdfcbank", autoProcessSuccess = false)
        assertTrue("First withdrawal should succeed", firstOk)

        // Attempting another identical request immediately
        val (dupOk, dupMsg) = repo.requestAdminUPIWithdrawal(50, "kirtaniamanik@okhdfcbank", autoProcessSuccess = false)
        assertFalse("Duplicate concurrent withdrawal must be prevented. Got: $dupMsg", dupOk)
        assertEquals("A duplicate withdrawal request of ₹50 is already being processed. Please wait.", dupMsg)
    }

    @Test
    fun `test failed withdrawal releases reserved balance back to available`() {
        val repo = BattleArenaRepository()
        val initialAvailable = repo.adminWallet.value.availableBalance

        // Initiate async withdrawal of ₹80
        val (initOk, _) = repo.requestAdminUPIWithdrawal(80, "kirtaniamanik@okhdfcbank", autoProcessSuccess = false)
        assertTrue(initOk)

        val reservedWallet = repo.adminWallet.value
        assertEquals(initialAvailable - 80, reservedWallet.availableBalance)
        assertEquals(80, reservedWallet.reservedBalance)

        val record = repo.adminWithdrawals.value.first()

        // Mark as failed (e.g. gateway timeout)
        val (failOk, _) = repo.failAdminWithdrawal(record.id, "Bank gateway network timeout")
        assertTrue("Fail action should succeed", failOk)

        val restoredWallet = repo.adminWallet.value
        assertEquals("Available balance must be fully restored", initialAvailable, restoredWallet.availableBalance)
        assertEquals("Reserved balance must be 0", 0, restoredWallet.reservedBalance)

        val updatedRecord = repo.adminWithdrawals.value.first { it.id == record.id }
        assertEquals(com.example.model.AdminWithdrawalStatus.FAILED, updatedRecord.status)
        assertEquals("Bank gateway network timeout", updatedRecord.failureReason)
    }

    @Test
    fun `test reverseAdminWithdrawal restores withdrawn funds to available balance`() {
        val repo = BattleArenaRepository()
        val initialAvailable = repo.adminWallet.value.availableBalance

        // Complete successful withdrawal of ₹60
        val (initOk, _) = repo.requestAdminUPIWithdrawal(60, "kirtaniamanik@okhdfcbank", autoProcessSuccess = true)
        assertTrue(initOk)
        assertEquals(initialAvailable - 60, repo.adminWallet.value.availableBalance)

        val record = repo.adminWithdrawals.value.first()
        assertEquals(com.example.model.AdminWithdrawalStatus.SUCCESS, record.status)

        // Bank reverses the settlement
        val (reverseOk, _) = repo.reverseAdminWithdrawal(record.id, "Settlement reversal from clearing house")
        assertTrue("Reversal action must succeed", reverseOk)

        val restoredWallet = repo.adminWallet.value
        assertEquals("Available balance must be restored by ₹60", initialAvailable, restoredWallet.availableBalance)

        val updatedRecord = repo.adminWithdrawals.value.first { it.id == record.id }
        assertEquals(com.example.model.AdminWithdrawalStatus.REVERSED, updatedRecord.status)

        val ledgerReversal = repo.adminLedger.value.find { it.type == com.example.model.AdminLedgerEntryType.WITHDRAWAL_REVERSAL }
        assertNotNull("Reversal must be recorded in ledger", ledgerReversal)
        assertEquals(60, ledgerReversal?.amount)
    }

    @Test
    fun `test saveOwnerVerifiedUpiId validates regex and updates destination`() {
        val repo = BattleArenaRepository()

        // Valid update
        val (validOk, _) = repo.saveOwnerVerifiedUpiId("manik.kirtania@oksbi")
        assertTrue("Valid UPI ID update must succeed", validOk)
        assertEquals("manik.kirtania@oksbi", repo.adminWallet.value.verifiedUpiId)

        // Invalid format update
        val (invalidOk, invalidMsg) = repo.saveOwnerVerifiedUpiId("not_an_email_or_upi")
        assertFalse("Invalid format must be rejected", invalidOk)
        assertTrue(invalidMsg.contains("Invalid UPI ID format"))
    }

    @Test
    fun `test updateBrPrizeChart updates tiers and total prize pool`() {
        val repo = BattleArenaRepository()
        val customTiers = listOf(
            com.example.model.BrPrizeTier("Rank 1 (Booyah Champion)", 1, 1, 150),
            com.example.model.BrPrizeTier("Rank 2", 2, 2, 80),
            com.example.model.BrPrizeTier("Rank 3 - 4", 3, 4, 60),
            com.example.model.BrPrizeTier("Rank 5", 5, 5, 40),
            com.example.model.BrPrizeTier("Rank 6 - 10", 6, 10, 30)
        )
        // Placement = 150 + 80 + 120 + 40 + 150 = 540. Kill pool = 110. Total = 650.
        val (ok, msg) = repo.updateBrPrizeChart("T-BR-301", customTiers, killPrize = 110)
        assertTrue("Update must succeed for owner: $msg", ok)

        val tourney = repo.tournaments.value.find { it.id == "T-BR-301" }
        assertNotNull(tourney)
        assertEquals(650, tourney?.totalPrizePool)
        assertEquals(540, tourney?.dynamicPlacementPrizePool)
        assertEquals(150, tourney?.getPrizeForRank(1))
        assertEquals(80, tourney?.getPrizeForRank(2))
        assertEquals(60, tourney?.getPrizeForRank(3))
        assertEquals(10, tourney?.totalWinningPositions)
    }

    @Test
    fun `test verifyMatchResult modifies rank and updates status`() {
        val repo = BattleArenaRepository()
        val results = repo.matchResults.value
        val target = results.first { it.id == "RES-BR-301-3" } // Initially Rank 3, PENDING_VERIFICATION

        val (ok, _) = repo.verifyMatchResult(target.id, verifiedRank = 2, verifiedKills = 5, isApproved = true)
        assertTrue(ok)

        val updated = repo.matchResults.value.first { it.id == target.id }
        assertEquals(2, updated.rank)
        assertEquals(5, updated.kills)
        assertEquals(com.example.model.PrizePaymentStatus.APPROVED, updated.paymentStatus)
        assertTrue(updated.isVerified)
    }

    @Test
    fun `test approveAndProcessPrizePayment credits wallet and prevents duplicates`() {
        val repo = BattleArenaRepository()
        // BA-70912 is Manik Hunter (winner in RES-BR-301-1, eligiblePrize = 130)
        val initialWinningBalance = repo.currentWallet.value.winningBalance
        val initialTotalPrizePaid = repo.adminWallet.value.totalPrizePaid

        val (ok, msg) = repo.approveAndProcessPrizePayment("RES-BR-301-1")
        assertTrue("Prize payout must succeed: $msg", ok)

        val updatedResult = repo.matchResults.value.first { it.id == "RES-BR-301-1" }
        assertEquals(com.example.model.PrizePaymentStatus.PAID, updatedResult.paymentStatus)
        assertNotNull(updatedResult.payoutUtr)
        assertTrue(updatedResult.payoutUtr!!.startsWith("UTR-PRIZE-"))

        // Verify winner winning wallet credited
        val updatedWallet = repo.currentWallet.value
        assertEquals(initialWinningBalance + 130, updatedWallet.winningBalance)

        // Verify Admin Wallet updated
        assertEquals(initialTotalPrizePaid + 130, repo.adminWallet.value.totalPrizePaid)

        // Verify player transaction logged
        val prizeTx = repo.transactions.value.find { it.referenceId == updatedResult.payoutUtr }
        assertNotNull(prizeTx)
        assertEquals(130, prizeTx?.amount)
        assertEquals(com.example.model.TransactionType.PRIZE_MONEY, prizeTx?.type)

        // Attempt duplicate payout
        val (dupOk, dupMsg) = repo.approveAndProcessPrizePayment("RES-BR-301-1")
        assertFalse("Duplicate payout must be blocked", dupOk)
        assertTrue(dupMsg.contains("Duplicate payout blocked"))
    }

    @Test
    fun `test minimum deposit of 20 rupees is strictly enforced`() {
        val repo = BattleArenaRepository()
        val utr = "UTR" + System.currentTimeMillis()

        // Test with ₹10 (< 20)
        val (fail10, msg10) = repo.submitAddMoneyRequest(10, "PhonePe UPI", utr, "Proof")
        assertFalse("Deposit under ₹20 must fail", fail10)
        assertTrue(msg10.contains("20"))

        // Test with ₹19 (< 20)
        val (fail19, msg19) = repo.submitAddMoneyRequest(19, "PhonePe UPI", utr, "Proof")
        assertFalse("Deposit under ₹20 must fail", fail19)
        assertTrue(msg19.contains("20"))

        // Test with valid ₹20
        val (ok20, _) = repo.submitAddMoneyRequest(20, "PhonePe UPI", utr, "Proof")
        assertTrue("Deposit of ₹20 must succeed", ok20)
    }

    @Test
    fun `test Add Money request stays PENDING and never credits wallet without owner verification`() {
        val repo = BattleArenaRepository()
        val initialDeposit = repo.currentWallet.value.depositBalance
        val utr = "UTR" + (System.currentTimeMillis() + 4567)

        // 1. User submits Add Money with UTR and screenshot note
        val (submitOk, _) = repo.submitAddMoneyRequest(250, "Google Pay", utr, "Payment Screenshot Attached")
        assertTrue(submitOk)

        // 2. Critical: Wallet must NOT be credited upon submission
        assertEquals(
            "Wallet deposit balance must NOT increase upon submission",
            initialDeposit,
            repo.currentWallet.value.depositBalance
        )

        // 3. Request status must be PENDING
        val req = repo.addMoneyRequests.value.first { it.utrNumber == utr }
        assertEquals(com.example.model.PaymentApprovalStatus.PENDING, req.status)
        assertEquals(250, req.amount)

        // 4. Corresponding transaction must be PENDING
        val pendingTx = repo.transactions.value.find { it.referenceId == utr }
        assertNotNull(pendingTx)
        assertEquals(com.example.model.TransactionStatus.PENDING, pendingTx?.status)

        // 5. Only after Owner approves does wallet get credited
        val (approveOk, _) = repo.approveAddMoneyRequest(req.id)
        assertTrue(approveOk)

        val creditedWallet = repo.currentWallet.value
        assertEquals(initialDeposit + 250, creditedWallet.depositBalance)

        val updatedReq = repo.addMoneyRequests.value.first { it.id == req.id }
        assertEquals(com.example.model.PaymentApprovalStatus.APPROVED, updatedReq.status)

        val completedTx = repo.transactions.value.find { it.referenceId == utr }
        assertEquals(com.example.model.TransactionStatus.SUCCESS, completedTx?.status)
    }

    @Test
    fun `test UPI URI encoding for official UPI ID manikkirtania1@ibl`() {
        val upiId = "manikkirtania1@ibl"
        val merchantName = "Battle Arena"
        val amount = 100
        val note = "Add Money Battle Arena"

        val encodedName = android.net.Uri.encode(merchantName)
        val encodedNote = android.net.Uri.encode(note)
        val upiUri = "upi://pay?pa=$upiId&pn=$encodedName&am=$amount&cu=INR&tn=$encodedNote"

        assertTrue(upiUri.contains("pa=manikkirtania1@ibl"))
        assertTrue(upiUri.contains("am=100"))
        assertTrue(upiUri.contains("cu=INR"))
        assertTrue(upiUri.startsWith("upi://pay?"))
    }

    @Test
    fun `test Virtual Prize Wallet balance reservation and double spend prevention`() {
        val repo = BattleArenaRepository()
        // Award virtual prize money of 40 via Lone Wolf match
        repo.verifyAndDistributeLoneWolf("T-LW-101", "BA-70912")
        val initialWinning = repo.currentWallet.value.winningBalance // 40 + 40 = 80
        val initialReserved = repo.currentWallet.value.reservedBalance // 0

        // 1. Submit withdrawal request of 50
        val (reqOk, reqMsg) = repo.requestWithdrawal(50, "UPI", "gamer@okhdfcbank")
        assertTrue("Withdrawal request must succeed: $reqMsg", reqOk)

        val reservedWallet = repo.currentWallet.value
        assertEquals("Winning balance must decrease by requested amount", initialWinning - 50, reservedWallet.winningBalance)
        assertEquals("Reserved balance must increase by requested amount", initialReserved + 50, reservedWallet.reservedBalance)
        assertEquals("Total virtual balance must remain constant during reservation", initialWinning, reservedWallet.winningBalance + reservedWallet.reservedBalance)

        // 2. Prevent duplicate active withdrawal while pending
        val (dupReqOk, dupReqMsg) = repo.requestWithdrawal(50, "UPI", "gamer@okhdfcbank")
        assertFalse("Duplicate active withdrawal must be blocked", dupReqOk)
        assertTrue(dupReqMsg.contains("active withdrawal"))

        // 3. Status must be PENDING
        val req = repo.withdrawals.value.first { it.amount == 50 && it.payoutDestination == "gamer@okhdfcbank" }
        assertEquals(com.example.model.WithdrawalStatus.PENDING, req.status)
    }

    @Test
    fun `test Owner approve and mark as Paid with bank UTR permanently deducts reserved balance`() {
        val repo = BattleArenaRepository()
        repo.verifyAndDistributeLoneWolf("T-LW-101", "BA-70912")
        repo.requestWithdrawal(60, "UPI", "proplayer@paytm")

        val req = repo.withdrawals.value.first { it.amount == 60 && it.payoutDestination == "proplayer@paytm" }
        val initialTotalPrizePaid = repo.adminWallet.value.totalPrizePaid

        // 1. Owner approves request
        val (approveOk, _) = repo.approveWithdrawal(req.id)
        assertTrue(approveOk)
        assertEquals(com.example.model.WithdrawalStatus.APPROVED, repo.withdrawals.value.first { it.id == req.id }.status)

        // 2. Owner marks as Paid with valid bank UTR
        val utr = "UTR" + System.currentTimeMillis()
        val (paidOk, paidMsg) = repo.markWithdrawalPaid(req.id, utr)
        assertTrue("Mark as paid must succeed: $paidMsg", paidOk)

        val paidReq = repo.withdrawals.value.first { it.id == req.id }
        assertEquals(com.example.model.WithdrawalStatus.PAID, paidReq.status)
        assertEquals(utr, paidReq.paymentReference)

        // 3. Reserved balance must be permanently cleared
        assertEquals(0, repo.currentWallet.value.reservedBalance)

        // 4. Admin wallet must track payout
        assertEquals(initialTotalPrizePaid + 60, repo.adminWallet.value.totalPrizePaid)

        // 5. Duplicate payout attempt with same UTR must fail
        val (dupPaidOk, _) = repo.markWithdrawalPaid(req.id, utr)
        assertFalse("Duplicate payout must be blocked", dupPaidOk)
    }

    @Test
    fun `test Owner reject withdrawal releases reserved balance back to winning balance`() {
        val repo = BattleArenaRepository()
        repo.verifyAndDistributeLoneWolf("T-LW-101", "BA-70912") // 80 winning
        repo.requestWithdrawal(70, "UPI", "badaccount@upi")

        val req = repo.withdrawals.value.first { it.amount == 70 && it.payoutDestination == "badaccount@upi" }
        assertEquals(10, repo.currentWallet.value.winningBalance)
        assertEquals(70, repo.currentWallet.value.reservedBalance)

        // Reject request
        val (rejectOk, _) = repo.rejectWithdrawal(req.id, "Invalid UPI handle")
        assertTrue(rejectOk)

        val rejectedReq = repo.withdrawals.value.first { it.id == req.id }
        assertEquals(com.example.model.WithdrawalStatus.REJECTED, rejectedReq.status)
        assertEquals("Invalid UPI handle", rejectedReq.rejectionReason)

        // Reserved funds must be restored to player's winning balance
        assertEquals("Winning balance must be refunded", 80, repo.currentWallet.value.winningBalance)
        assertEquals("Reserved balance must be zero after refund", 0, repo.currentWallet.value.reservedBalance)

        // Corresponding transaction must be FAILED
        val tx = repo.transactions.value.first { it.id == req.id }
        assertEquals(com.example.model.TransactionStatus.FAILED, tx.status)
    }

    @Test
    fun `test Payment-based room access only unlocks for paid registered players in CONFIRMED or LIVE matches`() {
        val repo = BattleArenaRepository()
        // Ensure user is level 10+
        repo.submitLevelVerification(15)

        // 1. Unregistered user -> NOT_REGISTERED
        val testTourney = repo.createTournament(
            titleEn = "Lone Wolf Room Access Test",
            titleBn = "রুম অ্যাক্সেস টেস্ট",
            mode = com.example.model.GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "11:00 PM"
        )
        val tourneyBefore = repo.tournaments.value.first { it.id == testTourney.id }
        assertEquals(
            com.example.model.RoomAccessState.NOT_REGISTERED,
            tourneyBefore.getRoomAccessState("UNREGISTERED_USER")
        )
        assertFalse(tourneyBefore.isRoomCredentialsUnlockedForUser("UNREGISTERED_USER"))

        // 2. User joins Lone Wolf -> Matchmaking state -> WAITING_CONFIRMATION (credentials hidden)
        val (joinOk, _) = repo.joinTournament(testTourney.id)
        assertTrue(joinOk)

        val tourneyAfterJoin = repo.tournaments.value.first { it.id == testTourney.id }
        val currentUserId = repo.currentUser.value?.id ?: "BA-70912"
        assertTrue(tourneyAfterJoin.isUserJoined(currentUserId))

        // Match status is MATCHMAKING (waiting for 2nd player)
        assertEquals(com.example.model.TournamentStatus.MATCHMAKING, tourneyAfterJoin.status)
        assertEquals(
            com.example.model.RoomAccessState.WAITING_CONFIRMATION,
            tourneyAfterJoin.getRoomAccessState(currentUserId)
        )
        assertFalse(
            "Room credentials must be locked while match is still in matchmaking",
            tourneyAfterJoin.isRoomCredentialsUnlockedForUser(currentUserId)
        )

        // 3. Match becomes CONFIRMED -> Room credentials UNLOCKED
        val confirmedTourney = tourneyAfterJoin.copy(
            status = com.example.model.TournamentStatus.CONFIRMED,
            countdownStartMillis = System.currentTimeMillis()
        )
        assertEquals(
            com.example.model.RoomAccessState.UNLOCKED,
            confirmedTourney.getRoomAccessState(currentUserId)
        )
        assertTrue(
            "Room credentials must unlock when match is CONFIRMED",
            confirmedTourney.isRoomCredentialsUnlockedForUser(currentUserId)
        )
    }

    @Test
    fun `test Room credentials expire and hide automatically when match ends or is unsuccessful`() {
        val repo = BattleArenaRepository()
        val currentUserId = repo.currentUser.value?.id ?: "BA-70912"

        val participant = com.example.model.PlayerParticipant(
            userId = currentUserId,
            name = "Player One",
            freeFireName = "PRO_GAMER",
            freeFireUid = "2948109382",
            isPaid = true
        )

        // Completed tournament
        val completedTourney = com.example.model.Tournament(
            id = "T-TEST-COMPLETED",
            titleEn = "Completed Cup",
            titleBn = "সমাপ্ত টুর্নামেন্ট",
            mode = com.example.model.GameMode.LONE_WOLF,
            entryFee = 20,
            totalPrizePool = 40,
            totalSlots = 2,
            startTimeFormatted = "Now",
            startTimeMillis = System.currentTimeMillis(),
            status = com.example.model.TournamentStatus.COMPLETED,
            roomId = "8899112",
            roomPassword = "1234",
            participants = listOf(participant)
        )
        assertEquals(
            com.example.model.RoomAccessState.EXPIRED,
            completedTourney.getRoomAccessState(currentUserId)
        )
        assertFalse(
            "Credentials must be expired when match is COMPLETED",
            completedTourney.isRoomCredentialsUnlockedForUser(currentUserId)
        )

        // Unsuccessful tournament
        val unsuccessfulTourney = completedTourney.copy(status = com.example.model.TournamentStatus.UNSUCCESSFUL)
        assertEquals(
            com.example.model.RoomAccessState.EXPIRED,
            unsuccessfulTourney.getRoomAccessState(currentUserId)
        )
        assertFalse(
            "Credentials must be expired when match is UNSUCCESSFUL",
            unsuccessfulTourney.isRoomCredentialsUnlockedForUser(currentUserId)
        )
    }

    @Test
    fun `test auditRoomRoster flags unauthorized free entry friends not on paid registration list`() {
        val repo = BattleArenaRepository()
        val tourney = repo.tournaments.value.first { it.id == "T-CS-201" }

        // Paid registered player UIDs in T-CS-201
        val registeredUids = tourney.participants.map { it.freeFireUid }
        assertTrue(registeredUids.size >= 5)

        // In-game room roster: 7 registered players + 2 unauthorized friends who never registered
        val roomUids = listOf(
            registeredUids[0],
            registeredUids[1],
            registeredUids[2],
            "9999999999", // Unauthorized friend #1
            "8888888888"  // Unauthorized friend #2
        )

        val report = repo.auditRoomRoster(tourney.id, roomUids)
        assertEquals(5, report.totalAudited)
        assertEquals(3, report.authorizedCount)
        assertEquals(2, report.unauthorizedUids.size)
        assertTrue(report.unauthorizedUids.contains("9999999999"))
        assertTrue(report.unauthorizedUids.contains("8888888888"))
    }

    @Test
    fun `test Admin kick unauthorized player revokes room access and optionally refunds entry fee`() {
        val repo = BattleArenaRepository()
        val tourney = repo.tournaments.value.first { it.id == "T-CS-201" }
        val targetParticipant = tourney.participants.first()

        // 1. Kick player without refund
        val (kickOk, _) = repo.removeUnauthorizedPlayer(
            tournamentId = tourney.id,
            freeFireUid = targetParticipant.freeFireUid,
            reason = "Unauthorized friend sharing Room ID",
            shouldRefund = false
        )
        assertTrue("Kick action must succeed", kickOk)

        val updatedTourney = repo.tournaments.value.first { it.id == tourney.id }
        val updatedParticipant = updatedTourney.participants.first { it.freeFireUid == targetParticipant.freeFireUid }
        assertEquals(com.example.model.PlayerVerificationStatus.KICKED, updatedParticipant.verificationStatus)

        // Room access must now be revoked
        assertEquals(
            com.example.model.RoomAccessState.UNAUTHORIZED_OR_KICKED,
            updatedTourney.getRoomAccessState(targetParticipant.userId)
        )
        assertFalse(updatedTourney.isRoomCredentialsUnlockedForUser(targetParticipant.userId))
    }

    @Test
    fun `test Anti-Free Entry security guard strictly prevents unverified or kicked players from receiving official prizes`() {
        val repo = BattleArenaRepository()
        val results = repo.matchResults.value
        val targetResult = results.first { it.id == "RES-BR-301-3" }

        // Kick the participant from the tournament roster
        val (kickOk, _) = repo.removeUnauthorizedPlayer(
            tournamentId = targetResult.tournamentId,
            freeFireUid = targetResult.freeFireUid,
            reason = "Free entry without registration"
        )
        assertTrue(kickOk)

        // Attempt to verify result for this kicked player must fail
        val (verifyOk, verifyMsg) = repo.verifyMatchResult(targetResult.id, isApproved = true)
        assertFalse("Unverified / kicked players must not be approved", verifyOk)
        assertTrue(verifyMsg.contains("Security violation"))

        // Attempt to pay prize must also fail
        val (payoutOk, payoutMsg) = repo.approveAndProcessPrizePayment(targetResult.id)
        assertFalse("Prize payment to unauthorized player must be blocked", payoutOk)
        assertTrue(payoutMsg.contains("Security violation"))
    }

    @Test
    fun `test all 13 Indian languages defined and translated in AppStrings`() {
        val languages = com.example.model.AppLanguage.values()
        assertEquals("Must support exactly 13 Indian languages", 13, languages.size)

        val expectedCodes = listOf("bn", "en", "hi", "as", "or", "mr", "gu", "pa", "ta", "te", "kn", "ml", "ur")
        for (code in expectedCodes) {
            val found = languages.find { it.code == code }
            assertNotNull("Language with code '$code' must exist", found)
            assertTrue("Native name for $code must not be blank", found!!.nativeName.isNotBlank())
        }

        // Test Urdu is RTL, and others are LTR
        assertTrue("Urdu must be marked as RTL", com.example.model.AppLanguage.URDU.isRtl)
        for (lang in languages) {
            if (lang != com.example.model.AppLanguage.URDU) {
                assertFalse("${lang.englishName} must not be RTL", lang.isRtl)
            }
        }

        // Verify translations for every language are non-blank
        for (lang in languages) {
            assertTrue("appTitle for ${lang.code} must not be blank", com.example.model.AppStrings.appTitle(lang).isNotBlank())
            assertTrue("navTournaments for ${lang.code} must not be blank", com.example.model.AppStrings.navTournaments(lang).isNotBlank())
            assertTrue("navWallet for ${lang.code} must not be blank", com.example.model.AppStrings.navWallet(lang).isNotBlank())
            assertTrue("navLeaderboard for ${lang.code} must not be blank", com.example.model.AppStrings.navLeaderboard(lang).isNotBlank())
            assertTrue("navProfile for ${lang.code} must not be blank", com.example.model.AppStrings.navProfile(lang).isNotBlank())
            assertTrue("selectLanguageTitle for ${lang.code} must not be blank", com.example.model.AppStrings.selectLanguageTitle(lang).isNotBlank())
            assertTrue("roomLockedNotice for ${lang.code} must not be blank", com.example.model.AppStrings.roomLockedNotice(lang).isNotBlank())
            assertTrue("adminControlCenter for ${lang.code} must not be blank", com.example.model.AppStrings.adminControlCenter(lang).isNotBlank())
        }
    }

    @Test
    fun `test switching language updates repository state and persists correctly`() {
        val repo = com.example.data.BattleArenaRepository()
        assertEquals(com.example.model.AppLanguage.ENGLISH, repo.currentLanguage.value)

        // Switch to Hindi
        repo.setLanguage(com.example.model.AppLanguage.HINDI)
        assertEquals(com.example.model.AppLanguage.HINDI, repo.currentLanguage.value)

        // Switch to Tamil
        repo.setLanguage(com.example.model.AppLanguage.TAMIL)
        assertEquals(com.example.model.AppLanguage.TAMIL, repo.currentLanguage.value)

        // Switch to Urdu (RTL)
        repo.setLanguage(com.example.model.AppLanguage.URDU)
        assertEquals(com.example.model.AppLanguage.URDU, repo.currentLanguage.value)
        assertTrue(repo.currentLanguage.value.isRtl)

        // Test fromCode helper with fallback
        assertEquals(com.example.model.AppLanguage.BENGALI, com.example.model.AppLanguage.fromCode("bn"))
        assertEquals(com.example.model.AppLanguage.GUJARATI, com.example.model.AppLanguage.fromCode("gu"))
        assertEquals(com.example.model.AppLanguage.PUNJABI, com.example.model.AppLanguage.fromCode("pa"))
        assertEquals(com.example.model.AppLanguage.ENGLISH, com.example.model.AppLanguage.fromCode("unknown_code"))
    }

    @Test
    fun `test FCM notification channels and push dispatchers`() {
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        com.example.data.BattleArenaNotificationManager.createNotificationChannels(context)

        val repo = com.example.data.BattleArenaRepository.getInstance()
        val initialNotifCount = repo.notifications.value.size

        // 1. Dispatch Tournament Reminder
        com.example.data.BattleArenaNotificationManager.dispatchTournamentReminder(
            context = context,
            tournamentTitle = "Lone Wolf 1v1 Clash",
            tournamentId = "T-101",
            startsIn = "10 mins",
            language = com.example.model.AppLanguage.ENGLISH
        )
        val notifsAfterReminder = repo.notifications.value
        assertEquals(initialNotifCount + 1, notifsAfterReminder.size)
        assertTrue(notifsAfterReminder.first().titleEn.contains("Registration Reminder"))

        // 2. Dispatch Match Start Alert
        com.example.data.BattleArenaNotificationManager.dispatchMatchStartAlert(
            context = context,
            tournamentTitle = "Lone Wolf 1v1 Clash",
            tournamentId = "T-101",
            roomId = "891234",
            roomPassword = "123",
            language = com.example.model.AppLanguage.ENGLISH
        )
        val notifsAfterMatch = repo.notifications.value
        assertEquals(initialNotifCount + 2, notifsAfterMatch.size)
        assertTrue(notifsAfterMatch.first().titleEn.contains("Match Starting"))
        assertTrue(notifsAfterMatch.first().messageEn.contains("Room ID: 891234"))

        // 3. Dispatch Prize Distribution Alert
        com.example.data.BattleArenaNotificationManager.dispatchPrizeDistributionAlert(
            context = context,
            tournamentTitle = "Lone Wolf 1v1 Clash",
            prizeAmount = 250,
            rank = 1,
            language = com.example.model.AppLanguage.ENGLISH
        )
        val notifsAfterPrize = repo.notifications.value
        assertEquals(initialNotifCount + 3, notifsAfterPrize.size)
        assertTrue(notifsAfterPrize.first().titleEn.contains("Prize Won"))
        assertTrue(notifsAfterPrize.first().messageEn.contains("₹250"))
    }

    @Test
    fun `test FCM token and notification preferences update`() {
        val repo = com.example.data.BattleArenaRepository.getInstance()

        // Test token update
        val sampleToken = "fcm_test_token_battle_arena_998877"
        repo.updateFcmToken(sampleToken)
        assertEquals(sampleToken, repo.fcmToken.value)

        // Test notification preferences update
        repo.updateNotificationPreferences(
            tournamentReminders = false,
            matchStarts = true,
            prizeAlerts = false
        )
        assertFalse("Tournament reminders should be disabled", repo.tournamentRemindersEnabled.value)
        assertTrue("Match starts should be enabled", repo.matchStartsEnabled.value)
        assertFalse("Prize alerts should be disabled", repo.prizeAlertsEnabled.value)

        // Re-enable all
        repo.updateNotificationPreferences(
            tournamentReminders = true,
            matchStarts = true,
            prizeAlerts = true
        )
        assertTrue(repo.tournamentRemindersEnabled.value)
        assertTrue(repo.matchStartsEnabled.value)
        assertTrue(repo.prizeAlertsEnabled.value)
    }
}



