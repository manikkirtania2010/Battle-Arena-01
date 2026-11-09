package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.AccountStatus
import com.example.model.GameMode
import com.example.model.LevelVerificationStatus
import com.example.model.MatchCreationType
import com.example.model.PlayerParticipant
import com.example.model.PlayerVerificationStatus
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.model.User
import com.example.model.Wallet
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

class MatchJoiningConcurrencyPhase6Test {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    private fun createTestUser(
        id: String,
        email: String = "$id@test.com",
        ffUid: String = "294810${id.filter { it.isDigit() }.padStart(4, '0')}",
        level: Int = 25,
        isBanned: Boolean = false,
        verificationStatus: LevelVerificationStatus = LevelVerificationStatus.VERIFIED
    ): User {
        return User(
            uid = id,
            displayName = "Player $id",
            email = email,
            freeFireUID = ffUid,
            freeFireName = "FF_$id",
            freeFireLevel = level,
            levelVerificationStatus = verificationStatus,
            isAdmin = false,
            isBanned = isBanned,
            accountStatus = if (isBanned) AccountStatus.PERMANENTLY_BANNED else AccountStatus.ACTIVE
        )
    }

    // -------------------------------------------------------------
    // SCENARIO 1: Two players compete for the last Lone Wolf slot
    // -------------------------------------------------------------
    @Test
    fun `scenario 1 - two players compete for last Lone Wolf slot`() = runBlocking {
        val repo = BattleArenaRepository()
        val existingPlayer = createTestUser("USER-LW-EXISTING", ffUid = "1111222233")
        val playerA = createTestUser("USER-LW-A", ffUid = "2222333344")
        val playerB = createTestUser("USER-LW-B", ffUid = "3333444455")

        // Setup wallets
        repo.firestoreManager.saveUser(playerA)
        repo.firestoreManager.saveUser(playerB)
        repo.firestoreManager.saveWallet(Wallet(userId = playerA.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = playerB.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        // Create Lone Wolf tournament with 1/2 players already joined
        val initialTourney = Tournament(
            id = "T-LW-RACE-1",
            titleEn = "Lone Wolf Duel Race",
            titleBn = "লোন উলফ ডুয়েল",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            totalPrizePool = 40,
            totalSlots = 2,
            status = TournamentStatus.MATCHMAKING,
            participants = listOf(
                PlayerParticipant(
                    userId = existingPlayer.id,
                    name = existingPlayer.name,
                    freeFireName = existingPlayer.freeFireName,
                    freeFireUid = existingPlayer.freeFireUid,
                    entryId = "ENTRY-EXISTING",
                    entryFeePaid = 25
                )
            )
        )
        repo.firestoreManager.seedTournament(initialTourney)

        val results = listOf(playerA, playerB).map { user ->
            async(Dispatchers.IO) {
                repo.firestoreManager.atomicJoinMatch(
                    tournamentId = initialTourney.id,
                    user = user,
                    fee = 25,
                    uniqueEntryId = "ENTRY-${user.id}",
                    fallbackTournament = initialTourney
                )
            }
        }.awaitAll()

        val successes = results.filter { it.success }
        val failures = results.filter { !it.success }

        assertEquals("Exactly 1 player must win the final slot", 1, successes.size)
        assertEquals("Exactly 1 player must be rejected", 1, failures.size)
        assertTrue("Failure message must indicate tournament is full", failures.first().message.contains("Tournament is full"))

        // Final match must have exactly 2 participants, status CONFIRMED, 45s countdown started
        val finalTourney = repo.firestoreManager.getTournament(initialTourney.id)!!
        assertEquals(2, finalTourney.participants.size)
        assertEquals(TournamentStatus.CONFIRMED, finalTourney.status)
        assertNotNull(finalTourney.countdownStartMillis)

        // Verify the losing player had 0 fee deducted
        val winnerId = successes.first().participant!!.userId
        val loserId = if (winnerId == playerA.id) playerB.id else playerA.id
        val loserWallet = repo.firestoreManager.getWallet(loserId)!!
        assertEquals("Losing player wallet must remain untouched at 50", 50, loserWallet.depositBalance)

        val winnerWallet = repo.firestoreManager.getWallet(winnerId)!!
        assertEquals("Winning player wallet must have 25 deducted (50 - 25 = 25)", 25, winnerWallet.depositBalance)
    }

    // -------------------------------------------------------------
    // SCENARIO 2: Multiple players compete for the last Clash Squad slots
    // -------------------------------------------------------------
    @Test
    fun `scenario 2 - multiple players compete for last Clash Squad slots`() = runBlocking {
        val repo = BattleArenaRepository()

        // 6 participants already in match (Team A has 3, Team B has 3)
        val existingParticipants = (1..6).map { i ->
            PlayerParticipant(
                userId = "CS-EXISTING-$i",
                name = "CS Player $i",
                freeFireName = "CS_FF_$i",
                freeFireUid = "444455550$i",
                team = if (i <= 3) "Team A" else "Team B",
                entryId = "ENTRY-CS-$i",
                entryFeePaid = 20
            )
        }

        val csTourney = Tournament(
            id = "T-CS-RACE-2",
            titleEn = "Clash Squad 4v4 Final Slots",
            titleBn = "ক্ল্যাশ স্কোয়াড ফাইনাল স্লট",
            mode = GameMode.CLASH_SQUAD,
            entryFee = 20,
            totalPrizePool = 120,
            totalSlots = 8,
            status = TournamentStatus.UPCOMING,
            participants = existingParticipants
        )
        repo.firestoreManager.seedTournament(csTourney)

        // 6 candidates compete for the remaining 2 slots
        val candidates = (1..6).map { i ->
            val u = createTestUser("CANDIDATE-$i", ffUid = "777788880$i")
            repo.firestoreManager.saveUser(u)
            repo.firestoreManager.saveWallet(Wallet(userId = u.id, depositBalance = 40, winningBalance = 0, bonusBalance = 0))
            u
        }

        val results = candidates.map { user ->
            async(Dispatchers.IO) {
                repo.firestoreManager.atomicJoinMatch(
                    tournamentId = csTourney.id,
                    user = user,
                    fee = 20,
                    uniqueEntryId = "ENTRY-${user.id}",
                    fallbackTournament = csTourney
                )
            }
        }.awaitAll()

        val successes = results.filter { it.success }
        val failures = results.filter { !it.success }

        assertEquals("Exactly 2 candidates must succeed", 2, successes.size)
        assertEquals("Exactly 4 candidates must fail", 4, failures.size)

        val finalTourney = repo.firestoreManager.getTournament(csTourney.id)!!
        assertEquals("Total participants must be strictly 8", 8, finalTourney.participants.size)
        assertEquals(TournamentStatus.CONFIRMED, finalTourney.status)
        assertEquals(4, finalTourney.participants.count { it.team == "Team A" })
        assertEquals(4, finalTourney.participants.count { it.team == "Team B" })

        // Check wallet balances
        val winnerIds = successes.map { it.participant!!.userId }.toSet()
        candidates.forEach { u ->
            val w = repo.firestoreManager.getWallet(u.id)!!
            if (winnerIds.contains(u.id)) {
                assertEquals("Winner must be charged 20", 20, w.depositBalance)
            } else {
                assertEquals("Loser must remain at 40", 40, w.depositBalance)
            }
        }
    }

    // -------------------------------------------------------------
    // SCENARIO 3: Multiple players compete for the last Battle Royale slots
    // -------------------------------------------------------------
    @Test
    fun `scenario 3 - multiple players compete for last Battle Royale slots`() = runBlocking {
        val repo = BattleArenaRepository()

        // 38 participants already joined (capacity 40, 2 slots left)
        val existingParticipants = (1..38).map { i ->
            PlayerParticipant(
                userId = "BR-EXISTING-$i",
                name = "BR Player $i",
                freeFireName = "BR_FF_$i",
                freeFireUid = "55556666" + i.toString().padStart(2, '0'),
                entryId = "ENTRY-BR-$i",
                entryFeePaid = 20
            )
        }

        val brTourney = Tournament(
            id = "T-BR-RACE-3",
            titleEn = "Mega Battle Royale Race",
            titleBn = "মেগা ব্যাটেল রয়্যাল",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 20,
            totalPrizePool = 570,
            totalSlots = 40,
            status = TournamentStatus.UPCOMING,
            participants = existingParticipants
        )
        repo.firestoreManager.seedTournament(brTourney)

        val contenders = (1..8).map { i ->
            val u = createTestUser("BR-CONTENDER-$i", ffUid = "99990000" + i.toString().padStart(2, '0'))
            repo.firestoreManager.saveUser(u)
            repo.firestoreManager.saveWallet(Wallet(userId = u.id, depositBalance = 100, winningBalance = 0, bonusBalance = 0))
            u
        }

        val results = contenders.map { user ->
            async(Dispatchers.IO) {
                repo.firestoreManager.atomicJoinMatch(
                    tournamentId = brTourney.id,
                    user = user,
                    fee = 20,
                    uniqueEntryId = "ENTRY-${user.id}",
                    fallbackTournament = brTourney
                )
            }
        }.awaitAll()

        assertEquals("Exactly 2 contenders succeed", 2, results.count { it.success })
        assertEquals("Exactly 6 contenders fail", 6, results.count { !it.success })

        val finalTourney = repo.firestoreManager.getTournament(brTourney.id)!!
        assertEquals("Total slots filled must be exactly 40", 40, finalTourney.participants.size)
        assertEquals(TournamentStatus.CONFIRMED, finalTourney.status)
    }

    // -------------------------------------------------------------
    // SCENARIO 4: Repeated Join requests from the same player
    // -------------------------------------------------------------
    @Test
    fun `scenario 4 - repeated Join requests from the same player`() = runBlocking {
        val repo = BattleArenaRepository()
        val user = createTestUser("USER-REPEAT-04", ffUid = "1234509876")
        repo.firestoreManager.saveUser(user)
        repo.firestoreManager.saveWallet(Wallet(userId = user.id, depositBalance = 100, winningBalance = 0, bonusBalance = 0))
        repo.setCurrentUser(user)

        val tourney = repo.createTournament(
            titleEn = "Lone Wolf Repeat Test",
            titleBn = "লোন উলফ রিপিট টেস্ট",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "04:00 PM"
        )

        // 1st Join
        val (firstSuccess, _) = repo.joinTournament(tourney.id)
        assertTrue("First join must succeed", firstSuccess)
        assertEquals(75, repo.currentWallet.value.depositBalance)

        // 2nd Join attempt by same user
        val (secondSuccess, secondMsg) = repo.joinTournament(tourney.id)
        assertFalse("Second join must be rejected", secondSuccess)
        assertTrue("Message must indicate already joined: $secondMsg", secondMsg.contains("Already joined") || secondMsg.contains("ইতিমধ্যে জয়েন করেছেন"))

        // Verify balance was NOT deducted a second time (still 75, not 50)
        assertEquals(75, repo.currentWallet.value.depositBalance)
        assertEquals(75, FirestoreManager.getCloudWalletForTesting(user.id)!!.depositBalance)

        // Verify player appears only once in participant list
        val savedTourney = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(1, savedTourney.participants.count { it.userId == user.id })
    }

    // -------------------------------------------------------------
    // SCENARIO 5: Same Free Fire UID attempting duplicate registration
    // -------------------------------------------------------------
    @Test
    fun `scenario 5 - same Free Fire UID attempting duplicate registration`() = runBlocking {
        val repo = BattleArenaRepository()
        val sharedFfUid = "9876543210"

        val player1 = createTestUser("USER-DEVICE-1", ffUid = sharedFfUid)
        val player2 = createTestUser("USER-DEVICE-2", ffUid = sharedFfUid)

        repo.firestoreManager.saveUser(player1)
        repo.firestoreManager.saveUser(player2)
        repo.firestoreManager.saveWallet(Wallet(userId = player1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = player2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val tourney = repo.createTournament(
            titleEn = "Unique FF UID Match",
            titleBn = "ইউনিক এফএফ ম্যাচ",
            mode = GameMode.CLASH_SQUAD,
            entryFee = 20,
            startTimeFormatted = "05:00 PM"
        )

        // Player 1 joins with shared Free Fire UID
        repo.setCurrentUser(player1)
        val (p1Success, _) = repo.joinTournament(tourney.id)
        assertTrue(p1Success)

        // Player 2 attempts to join same match using same Free Fire UID
        repo.setCurrentUser(player2)
        val (p2Success, p2Msg) = repo.joinTournament(tourney.id)
        assertFalse("Player 2 with duplicate Free Fire UID must be rejected", p2Success)
        assertTrue("Error message must mention duplicate Free Fire UID: $p2Msg", p2Msg.contains("already registered"))

        // Player 2 wallet must remain untouched at 50
        assertEquals(50, repo.currentWallet.value.depositBalance)
        assertEquals(50, FirestoreManager.getCloudWalletForTesting(player2.id)!!.depositBalance)
    }

    // -------------------------------------------------------------
    // SCENARIO 6: Network failure during registration
    // -------------------------------------------------------------
    @Test
    fun `scenario 6 - network failure during registration`() = runBlocking {
        val repo = BattleArenaRepository()
        val user = createTestUser("USER-NET-06", ffUid = "7778889990")
        repo.firestoreManager.saveUser(user)
        repo.firestoreManager.saveWallet(Wallet(userId = user.id, depositBalance = 60, winningBalance = 0, bonusBalance = 0))
        repo.setCurrentUser(user)

        val tourney = repo.createTournament(
            titleEn = "Network Resiliency Match",
            titleBn = "নেটওয়ার্ক রেজিলিয়েন্সি",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "06:00 PM"
        )

        // Simulate network failure
        FirestoreManager.simulateNetworkFailure = true
        val (joinSuccess, msg) = repo.joinTournament(tourney.id)
        FirestoreManager.simulateNetworkFailure = false

        assertFalse("Join must fail when network fails", joinSuccess)
        assertTrue("Message must mention network failure: $msg", msg.contains("Network connection timed out"))

        // Wallet must not be deducted
        assertEquals(60, repo.currentWallet.value.depositBalance)
        assertEquals(60, FirestoreManager.getCloudWalletForTesting(user.id)!!.depositBalance)

        // Tournament must have 0 participants
        val t = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(0, t.participants.size)
    }

    // -------------------------------------------------------------
    // SCENARIO 7: Failed registration causes no unintended deduction
    // -------------------------------------------------------------
    @Test
    fun `scenario 7 - failed registration causes no unintended deduction`() = runBlocking {
        val repo = BattleArenaRepository()

        // Test 7a: Insufficient balance
        val poorUser = createTestUser("USER-POOR", ffUid = "1122334455")
        repo.firestoreManager.saveUser(poorUser)
        repo.firestoreManager.saveWallet(Wallet(userId = poorUser.id, depositBalance = 10, winningBalance = 0, bonusBalance = 0))
        repo.setCurrentUser(poorUser)

        val lwTourney = repo.createTournament(
            titleEn = "Lone Wolf Match",
            titleBn = "লোন উলফ ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "07:00 PM"
        )
        val (poorSuccess, _) = repo.joinTournament(lwTourney.id)
        assertFalse(poorSuccess)
        assertEquals(10, repo.currentWallet.value.totalBalance)

        // Test 7b: Account level < 10
        val lowLevelUser = createTestUser("USER-LOW-LVL", level = 6, ffUid = "2233445566")
        repo.firestoreManager.saveUser(lowLevelUser)
        repo.firestoreManager.saveWallet(Wallet(userId = lowLevelUser.id, depositBalance = 100, winningBalance = 0, bonusBalance = 0))
        repo.setCurrentUser(lowLevelUser)

        val (lowLvlSuccess, msgLvl) = repo.joinTournament(lwTourney.id)
        assertFalse(lowLvlSuccess)
        assertTrue(msgLvl.contains("Level 10"))
        assertEquals(100, repo.currentWallet.value.totalBalance)

        // Test 7c: Banned user
        val bannedUser = createTestUser("USER-BANNED", isBanned = true, ffUid = "3344556677")
        repo.firestoreManager.saveUser(bannedUser)
        repo.firestoreManager.saveWallet(Wallet(userId = bannedUser.id, depositBalance = 100, winningBalance = 0, bonusBalance = 0))
        repo.setCurrentUser(bannedUser)

        val (bannedSuccess, msgBan) = repo.joinTournament(lwTourney.id)
        assertFalse(bannedSuccess)
        assertTrue(msgBan.contains("blocked"))
        assertEquals(100, repo.currentWallet.value.totalBalance)
    }

    // -------------------------------------------------------------
    // SCENARIO 8: Match never exceeds its capacity (Concurrency Stress)
    // -------------------------------------------------------------
    @Test
    fun `scenario 8 - match never exceeds its capacity under heavy concurrent load`() = runBlocking {
        val repo = BattleArenaRepository()

        val emptyLoneWolf = Tournament(
            id = "T-LW-STRESS",
            titleEn = "Lone Wolf Stress Test",
            titleBn = "লোন উলফ স্ট্রেস",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            totalPrizePool = 40,
            totalSlots = 2,
            status = TournamentStatus.UPCOMING,
            participants = emptyList()
        )
        repo.firestoreManager.seedTournament(emptyLoneWolf)

        // 20 concurrent tasks trying to take 2 slots
        val candidates = (1..20).map { i ->
            val u = createTestUser("STRESS-$i", ffUid = "88889999" + i.toString().padStart(2, '0'))
            repo.firestoreManager.saveUser(u)
            repo.firestoreManager.saveWallet(Wallet(userId = u.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
            u
        }

        val results = candidates.map { user ->
            async(Dispatchers.IO) {
                repo.firestoreManager.atomicJoinMatch(
                    tournamentId = emptyLoneWolf.id,
                    user = user,
                    fee = 25,
                    uniqueEntryId = "ENTRY-${user.id}",
                    fallbackTournament = emptyLoneWolf
                )
            }
        }.awaitAll()

        val successes = results.filter { it.success }
        val failures = results.filter { !it.success }

        assertEquals("Exactly 2 participants must successfully register", 2, successes.size)
        assertEquals("Exactly 18 candidates must be rejected", 18, failures.size)

        val finalTourney = repo.firestoreManager.getTournament(emptyLoneWolf.id)!!
        assertEquals("Participants list size must never exceed 2", 2, finalTourney.participants.size)
    }

    // -------------------------------------------------------------
    // SCENARIO 9: Next match is created correctly
    // -------------------------------------------------------------
    @Test
    fun `scenario 9 - next match is created correctly when match becomes full`() = runBlocking {
        val repo = BattleArenaRepository()
        val user1 = createTestUser("USER-AUTO-1", ffUid = "9900112233")
        val user2 = createTestUser("USER-AUTO-2", ffUid = "9900112244")

        repo.firestoreManager.saveUser(user1)
        repo.firestoreManager.saveUser(user2)
        repo.firestoreManager.saveWallet(Wallet(userId = user1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = user2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val tourney = repo.createTournament(
            titleEn = "Lone Wolf Quick Cup #101",
            titleBn = "লোন উলফ কুইক কাপ #১০১",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "08:00 PM"
        )

        // Player 1 joins (1/2) -> matchmaking state, no auto-creation yet
        repo.setCurrentUser(user1)
        val (p1Success, _) = repo.joinTournament(tourney.id)
        assertTrue(p1Success)
        val tAfterP1 = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(TournamentStatus.MATCHMAKING, tAfterP1.status)

        // Player 2 joins (2/2) -> match confirmed, triggers auto-creation of next Lone Wolf match
        val tourneyCountBefore = repo.tournaments.value.size
        repo.setCurrentUser(user2)
        val (p2Success, _) = repo.joinTournament(tourney.id)
        assertTrue(p2Success)

        val tAfterP2 = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(TournamentStatus.CONFIRMED, tAfterP2.status)
        assertNotNull("45s countdown start millis must be set", tAfterP2.countdownStartMillis)

        // Check that next match was auto-created
        val tourneyCountAfter = repo.tournaments.value.size
        assertEquals("One subsequent tournament must be created", tourneyCountBefore + 1, tourneyCountAfter)

        val autoCreated = repo.tournaments.value.first { it.id != tourney.id && it.mode == GameMode.LONE_WOLF && it.status == TournamentStatus.UPCOMING }
        assertNotNull("Auto-created match must exist", autoCreated)
        assertEquals(0, autoCreated.participants.size)
        assertTrue("Auto-created match must have valid Lone Wolf format", autoCreated.id.startsWith("T-LW-"))
    }

    // -------------------------------------------------------------
    // SCENARIO 10: Scheduled and Manual matches remain independent
    // -------------------------------------------------------------
    @Test
    fun `scenario 10 - scheduled and manual matches remain independent`() = runBlocking {
        val repo = BattleArenaRepository()

        // 1. Create a Scheduled Match
        val schedMatch = repo.createScheduledMatch(
            titleEn = "Daily Scheduled Clash Squad",
            titleBn = "দৈনিক শিডিউলড ক্ল্যাশ স্কোয়াড",
            mode = GameMode.CLASH_SQUAD,
            matchDate = "Today",
            matchTimeIst = "09:00 PM IST",
            playerLimit = 8,
            entryFee = 20
        )
        assertEquals(MatchCreationType.SCHEDULED, schedMatch.creationType)

        // 2. Create a Manual Match
        val manualMatch = repo.createManualMatch(
            titleEn = "Private Custom 1v1 Room",
            titleBn = "প্রাইভেট কাস্টম রুম",
            mode = GameMode.LONE_WOLF,
            playerLimit = 2,
            entryFee = 25
        )
        assertEquals(MatchCreationType.MANUAL, manualMatch.creationType)

        // 3. Register a player for the Scheduled Match
        val schedPlayer = createTestUser("SCHED-PLAYER-10", ffUid = "6655443322")
        repo.firestoreManager.saveUser(schedPlayer)
        repo.firestoreManager.saveWallet(Wallet(userId = schedPlayer.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.setCurrentUser(schedPlayer)

        val (joinSchedSuccess, _) = repo.joinTournament(schedMatch.id)
        assertTrue(joinSchedSuccess)

        // 4. Verify Manual Match is completely unaffected
        val freshManual = repo.tournaments.value.find { it.id == manualMatch.id }!!
        assertEquals(0, freshManual.participants.size)
        assertEquals(MatchCreationType.MANUAL, freshManual.creationType)

        val freshSched = repo.tournaments.value.find { it.id == schedMatch.id }!!
        assertEquals(1, freshSched.participants.size)
        assertEquals(MatchCreationType.SCHEDULED, freshSched.creationType)
        assertEquals("Today, 09:00 PM IST", freshSched.startTimeFormatted)
    }
}
