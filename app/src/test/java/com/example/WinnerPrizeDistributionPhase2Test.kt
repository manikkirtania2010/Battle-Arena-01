package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.GameMode
import com.example.model.TournamentStatus
import com.example.model.TransactionType
import com.example.model.User
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WinnerPrizeDistributionPhase2Test {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    @Test
    fun `test Lone Wolf prize credited to actual winner and not to verifying admin`() = runBlocking {
        val repo = BattleArenaRepository()

        // 1. Setup Admin as current user
        val adminUser = User(
            uid = "admin_uid",
            displayName = "Admin Manik",
            email = BATTLE_ARENA_OWNER_EMAIL,
            isAdmin = true
        )
        repo.setCurrentUser(adminUser)
        val initialAdminWinningBalance = repo.currentWallet.value.winningBalance

        // 2. Setup Tournament with 2 distinct players
        val player1Uid = "player_one_111"
        val player2Uid = "player_two_222"

        // Register both players in cloud store
        repo.firestoreManager.restoreOrRegisterUser(player1Uid, "p1@gmail.com", "Gamer One", "P1_FF", "111111")
        repo.firestoreManager.restoreOrRegisterUser(player2Uid, "p2@gmail.com", "Gamer Two", "P2_FF", "222222")

        val tourney = repo.createTournament(
            titleEn = "Lone Wolf Duel #999",
            titleBn = "লোন উলফ ডুয়েলে #৯৯৯",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "09:00 PM"
        )

        // Set participants in tournament
        val p1 = com.example.model.PlayerParticipant(
            userId = player1Uid,
            name = "Gamer One",
            freeFireName = "P1_FF",
            freeFireUid = "111111",
            isPaid = true
        )
        val p2 = com.example.model.PlayerParticipant(
            userId = player2Uid,
            name = "Gamer Two",
            freeFireName = "P2_FF",
            freeFireUid = "222222",
            isPaid = true
        )
        repo.updateTournamentDirectly(tourney.copy(participants = listOf(p1, p2), status = TournamentStatus.CONFIRMED))

        // Initial wallets of p1 and p2
        val p1WalletBefore = repo.firestoreManager.getWallet(player1Uid)?.winningBalance ?: 0
        val p2WalletBefore = repo.firestoreManager.getWallet(player2Uid)?.winningBalance ?: 0

        // 3. Admin verifies result: player 1 is the Winner!
        val (settled, settleMsg) = repo.verifyAndDistributeLoneWolf(tourney.id, player1Uid)
        assertTrue("Settlement should succeed: $settleMsg", settled)

        // 4. Verify Admin DID NOT receive the prize
        assertEquals("Admin must NOT receive the winner's prize", initialAdminWinningBalance, repo.currentWallet.value.winningBalance)

        // 5. Verify Actual Winner (player 1) received exact ₹40 in cloud wallet
        val p1WalletAfter = repo.firestoreManager.getWallet(player1Uid)?.winningBalance ?: 0
        assertEquals("Winner player 1 must receive exactly ₹40", p1WalletBefore + 40, p1WalletAfter)

        // 6. Verify Player 2 received ₹0
        val p2WalletAfter = repo.firestoreManager.getWallet(player2Uid)?.winningBalance ?: 0
        assertEquals("Losing player 2 must receive ₹0", p2WalletBefore, p2WalletAfter)

        // 7. Verify Winner's transaction history in Firestore
        val p1Txs = repo.firestoreManager.getTransactions(player1Uid)
        val winningTx = p1Txs.find { it.referenceId == tourney.id }
        assertNotNull("Winner must have transaction record", winningTx)
        assertEquals(40, winningTx?.amount)
        assertEquals(TransactionType.MATCH_WINNING, winningTx?.type)

        // 8. Verify Duplicate Settlement Prevention
        val (dupSettled, dupMsg) = repo.verifyAndDistributeLoneWolf(tourney.id, player1Uid)
        assertFalse("Duplicate settlement must be blocked", dupSettled)
        assertTrue("Message must mention duplicate settlement blocked", dupMsg.contains("Duplicate settlement blocked", ignoreCase = true))

        val p1WalletAfterDup = repo.firestoreManager.getWallet(player1Uid)?.winningBalance ?: 0
        assertEquals("Duplicate call must NOT credit wallet again", p1WalletAfter, p1WalletAfterDup)
    }

    @Test
    fun `test Clash Squad prize distribution credits each winning team member across steps 35, 30, 30, 25`() = runBlocking {
        val repo = BattleArenaRepository()

        // Admin logged in
        val adminUser = User(
            uid = "admin_uid",
            displayName = "Admin",
            email = BATTLE_ARENA_OWNER_EMAIL,
            isAdmin = true
        )
        repo.setCurrentUser(adminUser)

        val teamAUids = listOf("cs_a1", "cs_a2", "cs_a3", "cs_a4")
        val teamBUids = listOf("cs_b1", "cs_b2", "cs_b3", "cs_b4")

        teamAUids.forEach { uid -> repo.firestoreManager.restoreOrRegisterUser(uid, "$uid@gmail.com", uid, uid, uid) }
        teamBUids.forEach { uid -> repo.firestoreManager.restoreOrRegisterUser(uid, "$uid@gmail.com", uid, uid, uid) }

        val tourney = repo.createTournament(
            titleEn = "Clash Squad Elite #888",
            titleBn = "ক্ল্যাশ স্কোয়াড এলিট #৮৮৮",
            mode = GameMode.CLASH_SQUAD,
            entryFee = 20,
            startTimeFormatted = "07:00 PM"
        )

        val teamAParticipants = teamAUids.map { uid ->
            com.example.model.PlayerParticipant(userId = uid, name = uid, freeFireName = uid, freeFireUid = uid, team = "Team A", isPaid = true)
        }
        val teamBParticipants = teamBUids.map { uid ->
            com.example.model.PlayerParticipant(userId = uid, name = uid, freeFireName = uid, freeFireUid = uid, team = "Team B", isPaid = true)
        }

        repo.updateTournamentDirectly(tourney.copy(participants = teamAParticipants + teamBParticipants, status = TournamentStatus.CONFIRMED))

        // Verify Team A wins! Expected prizes: 35, 30, 30, 25 (Total: 120)
        val (ok, msg) = repo.verifyAndDistributeClashSquad(tourney.id, "Team A")
        assertTrue("CS distribution must succeed: $msg", ok)

        val expectedPrizes = listOf(35, 30, 30, 25)
        teamAUids.forEachIndexed { index, uid ->
            val w = repo.firestoreManager.getWallet(uid)
            assertEquals("Team A player $index must receive ₹${expectedPrizes[index]}", expectedPrizes[index], w?.winningBalance)

            val txs = repo.firestoreManager.getTransactions(uid)
            val tx = txs.find { it.referenceId == tourney.id }
            assertNotNull("Winning transaction must exist", tx)
            assertEquals(expectedPrizes[index], tx?.amount)
            assertEquals(TransactionType.MATCH_WINNING, tx?.type)
        }

        // Losing team receives 0
        teamBUids.forEach { uid ->
            val w = repo.firestoreManager.getWallet(uid)
            assertEquals("Losing Team B player must receive 0", 0, w?.winningBalance)
        }

        // Duplicate settlement blocked
        val (dupOk, dupMsg) = repo.verifyAndDistributeClashSquad(tourney.id, "Team A")
        assertFalse("Duplicate CS settlement must be blocked", dupOk)
    }

    @Test
    fun `test Battle Royale placement and kill prizes distributed correctly without admin receiving player funds`() = runBlocking {
        val repo = BattleArenaRepository()

        // Verifying Admin
        val adminUser = User(
            uid = "admin_uid",
            displayName = "Admin",
            email = BATTLE_ARENA_OWNER_EMAIL,
            isAdmin = true
        )
        repo.setCurrentUser(adminUser)
        val adminInitialWinningBalance = repo.currentWallet.value.winningBalance

        val playerUids = (1..10).map { "br_p$it" }
        playerUids.forEach { uid -> repo.firestoreManager.restoreOrRegisterUser(uid, "$uid@gmail.com", uid, uid, uid) }

        val tourney = repo.createTournament(
            titleEn = "Mega Battle Royale #777",
            titleBn = "মেগা বিআর #৭৭৭",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 20,
            startTimeFormatted = "08:00 PM"
        )

        // Setup 10 participants with placements 1..10 and kills
        // Player 1: Placement 1 (₹100) + Top Kills Rank 1 (₹30) = ₹130
        // Player 2: Placement 2 (₹70) + Kills Rank 2 (₹25) = ₹95
        // Player 3: Placement 3 (₹50) + Kills Rank 3 (₹20) = ₹70
        val participants = playerUids.mapIndexed { index, uid ->
            com.example.model.PlayerParticipant(
                userId = uid,
                name = uid,
                freeFireName = uid,
                freeFireUid = uid,
                placement = index + 1,
                kills = 10 - index, // p1=10 kills, p2=9 kills, p3=8 kills, p4=7 kills, p5=6 kills...
                isPaid = true
            )
        }
        repo.updateTournamentDirectly(tourney.copy(participants = participants, status = TournamentStatus.CONFIRMED))

        val (ok, msg) = repo.verifyAndDistributeBattleRoyale(tourney.id)
        assertTrue("BR distribution must succeed: $msg", ok)

        // Rank 1 player: 100 + 30 = 130
        val p1Wallet = repo.firestoreManager.getWallet(playerUids[0])
        assertEquals("Player 1 must receive Placement (100) + Kills (30) = 130", 130, p1Wallet?.winningBalance)

        // Rank 2 player: 70 + 25 = 95
        val p2Wallet = repo.firestoreManager.getWallet(playerUids[1])
        assertEquals("Player 2 must receive Placement (70) + Kills (25) = 95", 95, p2Wallet?.winningBalance)

        // Rank 3 player: 50 + 20 = 70
        val p3Wallet = repo.firestoreManager.getWallet(playerUids[2])
        assertEquals("Player 3 must receive Placement (50) + Kills (20) = 70", 70, p3Wallet?.winningBalance)

        // Admin must not receive anything
        assertEquals("Admin must not receive player prize funds", adminInitialWinningBalance, repo.currentWallet.value.winningBalance)

        // Duplicate settlement blocked
        val (dupOk, _) = repo.verifyAndDistributeBattleRoyale(tourney.id)
        assertFalse("Duplicate BR settlement must be blocked", dupOk)
    }

    @Test
    fun `test regular player cannot trigger settlement`() = runBlocking {
        val repo = BattleArenaRepository()

        val regularPlayer = User(
            uid = "player_imposter",
            displayName = "Imposter",
            email = "imposter@gmail.com",
            isAdmin = false
        )
        repo.setCurrentUser(regularPlayer)

        val (ok, msg) = repo.verifyAndDistributeLoneWolf("T-LW-101", regularPlayer.uid)
        assertFalse("Regular player must be blocked from settlement", ok)
        assertTrue("Error message must indicate unauthorized", msg.contains("Unauthorized", ignoreCase = true))
    }
}
