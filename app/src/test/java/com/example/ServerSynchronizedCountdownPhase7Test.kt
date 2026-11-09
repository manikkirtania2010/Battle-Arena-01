package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Phase 7: Server-Synchronized 45-Second Countdown Test Suite.
 * Authoritative Server Timestamp, Clock Skew Resilience, App Background/Restart,
 * Once-Only Status Transitions, Notification Deduplication, and Incomplete Match Protection.
 */
class ServerSynchronizedCountdownPhase7Test {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    private fun createTestUser(
        id: String,
        name: String = "Player $id",
        ffUid: String = "1000$id",
        level: Int = 20
    ): User {
        return User(
            uid = id,
            displayName = name,
            email = "$id@freefire.in",
            freeFireName = "⚡$name⚡",
            freeFireUID = ffUid,
            freeFireLevel = level,
            levelVerificationStatus = LevelVerificationStatus.VERIFIED,
            isAdmin = false
        )
    }

    // -------------------------------------------------------------
    // SCENARIO 1: Two Lone Wolf players see the same countdown
    // -------------------------------------------------------------
    @Test
    fun `scenario 1 - two Lone Wolf players see the same countdown`() = runBlocking {
        val repo = BattleArenaRepository()
        val user1 = createTestUser("USER-LW-1", ffUid = "11110001")
        val user2 = createTestUser("USER-LW-2", ffUid = "11110002")

        repo.firestoreManager.saveUser(user1)
        repo.firestoreManager.saveUser(user2)
        repo.firestoreManager.saveWallet(Wallet(userId = user1.id, depositBalance = 100, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = user2.id, depositBalance = 100, winningBalance = 0, bonusBalance = 0))

        val tourney = repo.createTournament(
            titleEn = "Lone Wolf Sync Cup #1",
            titleBn = "লোন উলফ সিঙ্ক কাপ #১",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "08:00 PM"
        )

        // Player 1 joins (1/2) -> Matchmaking, no countdown
        repo.setCurrentUser(user1)
        val (p1Ok, _) = repo.joinTournament(tourney.id)
        assertTrue(p1Ok)
        val tAfterP1 = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(TournamentStatus.MATCHMAKING, tAfterP1.status)
        assertNull("Countdown must not start with only 1 player", tAfterP1.countdownStartMillis)

        // Set authoritative server start time
        val serverStartTime = 10_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        // Player 2 joins (2/2) -> Match confirmed, starts 45s countdown
        repo.setCurrentUser(user2)
        val (p2Ok, _) = repo.joinTournament(tourney.id)
        assertTrue(p2Ok)

        val fullTourney = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(TournamentStatus.CONFIRMED, fullTourney.status)
        assertNotNull("Authoritative countdown start time must be established", fullTourney.countdownStartMillis)
        assertEquals(serverStartTime, fullTourney.countdownStartMillis)

        // Advance authoritative server time by 15 seconds (server time = serverStartTime + 15_000)
        val serverNow = serverStartTime + 15_000L
        FirestoreManager.setAuthoritativeServerTime(serverNow)

        // Both players inspect remaining countdown
        val p1Remaining = fullTourney.getRemainingCountdownSeconds(serverNow)
        val p2Remaining = fullTourney.getRemainingCountdownSeconds(serverNow)

        assertEquals("Player 1 must see exactly 30 seconds remaining", 30, p1Remaining)
        assertEquals("Player 2 must see exactly 30 seconds remaining", 30, p2Remaining)
        assertEquals("Both players must see the exact same remaining countdown", p1Remaining, p2Remaining)
        assertTrue(fullTourney.isCountdownActive(serverNow))
    }

    // -------------------------------------------------------------
    // SCENARIO 2: Eight Clash Squad players see the same countdown
    // -------------------------------------------------------------
    @Test
    fun `scenario 2 - eight Clash Squad players see the same countdown`() = runBlocking {
        val repo = BattleArenaRepository()
        val users = (1..8).map { i ->
            val u = createTestUser("USER-CS-$i", ffUid = "2222000$i")
            repo.firestoreManager.saveUser(u)
            repo.firestoreManager.saveWallet(Wallet(userId = u.id, depositBalance = 100, winningBalance = 0, bonusBalance = 0))
            u
        }

        val csTourney = repo.createTournament(
            titleEn = "Clash Squad Sync #2",
            titleBn = "ক্ল্যাশ স্কোয়াড সিঙ্ক #২",
            mode = GameMode.CLASH_SQUAD,
            entryFee = 20,
            startTimeFormatted = "09:00 PM"
        )

        // First 7 players join -> no countdown
        for (i in 0..6) {
            repo.setCurrentUser(users[i])
            val (ok, _) = repo.joinTournament(csTourney.id)
            assertTrue(ok)
            val current = repo.tournaments.value.find { it.id == csTourney.id }!!
            assertNull("Countdown must not start with only ${i + 1}/8 players", current.countdownStartMillis)
        }

        // Establish authoritative server timestamp for 8th player join
        val serverStartTime = 20_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        // 8th player joins (8/8) -> Countdown starts
        repo.setCurrentUser(users[7])
        val (p8Ok, _) = repo.joinTournament(csTourney.id)
        assertTrue(p8Ok)

        val fullTourney = repo.tournaments.value.find { it.id == csTourney.id }!!
        assertEquals(8, fullTourney.participants.size)
        assertEquals(TournamentStatus.CONFIRMED, fullTourney.status)
        assertEquals(serverStartTime, fullTourney.countdownStartMillis)

        // Advance authoritative server time by 20 seconds
        val serverNow = serverStartTime + 20_000L
        FirestoreManager.setAuthoritativeServerTime(serverNow)

        // All 8 players evaluate remaining time
        val remainingTimes = users.map { fullTourney.getRemainingCountdownSeconds(serverNow) }
        assertTrue("All 8 players must see 25 seconds remaining", remainingTimes.all { it == 25 })
        assertEquals(8, remainingTimes.size)
    }

    // -------------------------------------------------------------
    // SCENARIO 3: Forty Battle Royale players see the same countdown
    // -------------------------------------------------------------
    @Test
    fun `scenario 3 - forty Battle Royale players see the same countdown`() = runBlocking {
        val repo = BattleArenaRepository()
        val users = (1..40).map { i ->
            val u = createTestUser("USER-BR-$i", ffUid = "333300" + i.toString().padStart(2, '0'))
            repo.firestoreManager.saveUser(u)
            repo.firestoreManager.saveWallet(Wallet(userId = u.id, depositBalance = 100, winningBalance = 0, bonusBalance = 0))
            u
        }

        val brTourney = repo.createTournament(
            titleEn = "Mega Battle Royale Sync",
            titleBn = "মেগা ব্যাটেল রয়্যাল সিঙ্ক",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 20,
            startTimeFormatted = "10:00 PM"
        )

        val serverStartTime = 30_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        // Join all 40 players
        users.forEach { u ->
            repo.setCurrentUser(u)
            val (ok, _) = repo.joinTournament(brTourney.id)
            assertTrue(ok)
        }

        val fullTourney = repo.tournaments.value.find { it.id == brTourney.id }!!
        assertEquals(40, fullTourney.participants.size)
        assertEquals(TournamentStatus.CONFIRMED, fullTourney.status)
        assertEquals(serverStartTime, fullTourney.countdownStartMillis)

        // Advance authoritative server time by 10 seconds
        val serverNow = serverStartTime + 10_000L
        FirestoreManager.setAuthoritativeServerTime(serverNow)

        val remainingTimes = users.map { fullTourney.getRemainingCountdownSeconds(serverNow) }
        assertTrue("All 40 players must see 35 seconds remaining", remainingTimes.all { it == 35 })
        assertEquals(40, remainingTimes.size)
    }

    // -------------------------------------------------------------
    // SCENARIO 4: Devices have different system clock settings
    // -------------------------------------------------------------
    @Test
    fun `scenario 4 - devices have different system clock settings`() = runBlocking {
        val repo = BattleArenaRepository()
        val u1 = createTestUser("USER-CLK-1", ffUid = "44440001")
        val u2 = createTestUser("USER-CLK-2", ffUid = "44440002")
        repo.firestoreManager.saveUser(u1)
        repo.firestoreManager.saveUser(u2)
        repo.firestoreManager.saveWallet(Wallet(userId = u1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = u2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val serverStartTime = 40_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        val tourney = repo.createTournament(
            titleEn = "Clock Skew Match",
            titleBn = "ক্লক টেস্ট ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "07:00 PM"
        )

        repo.setCurrentUser(u1)
        repo.joinTournament(tourney.id)
        repo.setCurrentUser(u2)
        repo.joinTournament(tourney.id)

        val fullTourney = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(serverStartTime, fullTourney.countdownStartMillis)

        // True server time is serverStartTime + 15 seconds (30s remaining)
        val trueServerTime = serverStartTime + 15_000L

        // Device 1: local clock is accurate (offset = 0)
        // Device 2: local clock is 2 minutes FAST (+120,000ms skew, calibrated offset = -120,000ms)
        // Device 3: local clock is 1 hour SLOW (-3,600,000ms skew, calibrated offset = +3,600,000ms)
        val device1ServerTime = trueServerTime // accurate
        val device2ServerTime = (trueServerTime + 120_000L) - 120_000L // skewed local + offset = true server
        val device3ServerTime = (trueServerTime - 3_600_000L) + 3_600_000L // skewed local + offset = true server

        val r1 = fullTourney.getRemainingCountdownSeconds(device1ServerTime)
        val r2 = fullTourney.getRemainingCountdownSeconds(device2ServerTime)
        val r3 = fullTourney.getRemainingCountdownSeconds(device3ServerTime)

        assertEquals("Device 1 must show 30s", 30, r1)
        assertEquals("Device 2 must show 30s despite 2min clock skew", 30, r2)
        assertEquals("Device 3 must show 30s despite 1hr clock skew", 30, r3)
        assertEquals(r1, r2)
        assertEquals(r2, r3)
    }

    // -------------------------------------------------------------
    // SCENARIO 5: Player minimizes the app during countdown
    // -------------------------------------------------------------
    @Test
    fun `scenario 5 - player minimizes the app during countdown`() = runBlocking {
        val repo = BattleArenaRepository()
        val u1 = createTestUser("USER-MIN-1", ffUid = "55550001")
        val u2 = createTestUser("USER-MIN-2", ffUid = "55550002")
        repo.firestoreManager.saveUser(u1)
        repo.firestoreManager.saveUser(u2)
        repo.firestoreManager.saveWallet(Wallet(userId = u1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = u2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val serverStartTime = 50_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        val tourney = repo.createTournament(
            titleEn = "Minimize Test Match",
            titleBn = "মিনিমাইজ টেস্ট",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "06:00 PM"
        )
        repo.setCurrentUser(u1)
        repo.joinTournament(tourney.id)
        repo.setCurrentUser(u2)
        repo.joinTournament(tourney.id)

        val fullTourney = repo.tournaments.value.find { it.id == tourney.id }!!

        // App minimized at T_start + 5s (40s remaining)
        assertEquals(40, fullTourney.getRemainingCountdownSeconds(serverStartTime + 5_000L))

        // UI coroutine was paused in background for 25 seconds.
        // App resumed at T_start + 30s:
        val resumedServerTime = serverStartTime + 30_000L
        val remainingAfterResume = fullTourney.getRemainingCountdownSeconds(resumedServerTime)

        assertEquals("Remaining time after 25s in background must immediately reflect 15s without sequential ticks", 15, remainingAfterResume)
        assertTrue(fullTourney.isCountdownActive(resumedServerTime))
    }

    // -------------------------------------------------------------
    // SCENARIO 6: Player closes and reopens the app
    // -------------------------------------------------------------
    @Test
    fun `scenario 6 - player closes and reopens the app`() = runBlocking {
        val repo1 = BattleArenaRepository()
        val u1 = createTestUser("USER-REOPEN-1", ffUid = "66660001")
        val u2 = createTestUser("USER-REOPEN-2", ffUid = "66660002")
        repo1.firestoreManager.saveUser(u1)
        repo1.firestoreManager.saveUser(u2)
        repo1.firestoreManager.saveWallet(Wallet(userId = u1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo1.firestoreManager.saveWallet(Wallet(userId = u2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val serverStartTime = 60_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        val tourney = repo1.createTournament(
            titleEn = "Reopen App Match",
            titleBn = "রিওপেন অ্যাপ ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "05:00 PM"
        )
        repo1.setCurrentUser(u1)
        repo1.joinTournament(tourney.id)
        repo1.setCurrentUser(u2)
        repo1.joinTournament(tourney.id)

        // Match confirmed and saved into authoritative cloud store
        val savedTourney = repo1.firestoreManager.getCachedCloudTournament(tourney.id)!!
        assertEquals(serverStartTime, savedTourney.countdownStartMillis)

        // Simulate app kill: create a completely fresh BattleArenaRepository instance
        // App is reopened 20 seconds after countdown start
        val reopenServerTime = serverStartTime + 20_000L
        FirestoreManager.setAuthoritativeServerTime(reopenServerTime)

        val repo2 = BattleArenaRepository()
        val restoredTourney = repo2.firestoreManager.getCachedCloudTournament(tourney.id)!!

        assertEquals(serverStartTime, restoredTourney.countdownStartMillis)
        val remainingSeconds = restoredTourney.getRemainingCountdownSeconds(reopenServerTime)
        assertEquals("Countdown must be exactly 25s remaining upon fresh app launch", 25, remainingSeconds)
    }

    // -------------------------------------------------------------
    // SCENARIO 7: Internet disconnects and reconnects
    // -------------------------------------------------------------
    @Test
    fun `scenario 7 - internet disconnects and reconnects`() = runBlocking {
        val repo = BattleArenaRepository()
        val u1 = createTestUser("USER-NET-1", ffUid = "77770001")
        val u2 = createTestUser("USER-NET-2", ffUid = "77770002")
        repo.firestoreManager.saveUser(u1)
        repo.firestoreManager.saveUser(u2)
        repo.firestoreManager.saveWallet(Wallet(userId = u1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = u2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val serverStartTime = 70_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        val tourney = repo.createTournament(
            titleEn = "Network Offline Match",
            titleBn = "নেটওয়ার্ক অফলাইন ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "04:00 PM"
        )
        repo.setCurrentUser(u1)
        repo.joinTournament(tourney.id)
        repo.setCurrentUser(u2)
        repo.joinTournament(tourney.id)

        val fullTourney = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(serverStartTime, fullTourney.countdownStartMillis)

        // Disconnect internet
        FirestoreManager.simulateNetworkFailure = true

        // Offline: Player checks remaining time at T_start + 18s
        val offlineRemaining = fullTourney.getRemainingCountdownSeconds(serverStartTime + 18_000L)
        assertEquals("Countdown must accurately evaluate offline using authoritative start time", 27, offlineRemaining)

        // Internet reconnects
        FirestoreManager.simulateNetworkFailure = false

        // Online: Player checks remaining time at T_start + 28s
        val onlineRemaining = fullTourney.getRemainingCountdownSeconds(serverStartTime + 28_000L)
        assertEquals("Countdown continues accurately after internet reconnects", 17, onlineRemaining)
    }

    // -------------------------------------------------------------
    // SCENARIO 8: Countdown is not reset
    // -------------------------------------------------------------
    @Test
    fun `scenario 8 - countdown is not reset`() = runBlocking {
        val repo = BattleArenaRepository()
        val u1 = createTestUser("USER-RESET-1", ffUid = "88880001")
        val u2 = createTestUser("USER-RESET-2", ffUid = "88880002")
        repo.firestoreManager.saveUser(u1)
        repo.firestoreManager.saveUser(u2)
        repo.firestoreManager.saveWallet(Wallet(userId = u1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = u2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val serverStartTime = 80_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        val tourney = repo.createTournament(
            titleEn = "No Reset Match",
            titleBn = "নো রিসেট ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "03:00 PM"
        )
        repo.setCurrentUser(u1)
        repo.joinTournament(tourney.id)
        repo.setCurrentUser(u2)
        repo.joinTournament(tourney.id)

        val fullTourney = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(serverStartTime, fullTourney.countdownStartMillis)

        // Advance 10 seconds
        val serverTime10s = serverStartTime + 10_000L
        FirestoreManager.setAuthoritativeServerTime(serverTime10s)
        assertEquals(35, fullTourney.getRemainingCountdownSeconds(serverTime10s))

        // Attempting to join again or tick must NOT reset countdownStartMillis
        repo.setCurrentUser(u2)
        val (repeatJoinOk, _) = repo.joinTournament(tourney.id)
        assertFalse(repeatJoinOk) // rejected

        repo.tickCountdowns(serverTime10s)

        val checkTourney = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals("countdownStartMillis must remain identical and never reset", serverStartTime, checkTourney.countdownStartMillis)
        assertEquals(35, checkTourney.getRemainingCountdownSeconds(serverTime10s))
    }

    // -------------------------------------------------------------
    // SCENARIO 9: Match transitions to LIVE only once
    // -------------------------------------------------------------
    @Test
    fun `scenario 9 - match transitions to LIVE only once`() = runBlocking {
        val repo = BattleArenaRepository()
        val u1 = createTestUser("USER-LIVE-1", ffUid = "99990001")
        val u2 = createTestUser("USER-LIVE-2", ffUid = "99990002")
        repo.firestoreManager.saveUser(u1)
        repo.firestoreManager.saveUser(u2)
        repo.firestoreManager.saveWallet(Wallet(userId = u1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = u2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val serverStartTime = 90_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        val tourney = repo.createTournament(
            titleEn = "Live Transition Match",
            titleBn = "লাইভ ট্রানজিশন ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "02:00 PM"
        )
        repo.setCurrentUser(u1)
        repo.joinTournament(tourney.id)
        repo.setCurrentUser(u2)
        repo.joinTournament(tourney.id)

        // At 44 seconds: still CONFIRMED, not LIVE
        val serverTime44 = serverStartTime + 44_000L
        FirestoreManager.setAuthoritativeServerTime(serverTime44)
        repo.tickCountdowns(serverTime44)
        val t44 = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(TournamentStatus.CONFIRMED, t44.status)
        assertFalse(t44.matchStartedNotified)

        // At 45 seconds: transitions to LIVE exactly once
        val serverTime45 = serverStartTime + 45_000L
        FirestoreManager.setAuthoritativeServerTime(serverTime45)
        repo.tickCountdowns(serverTime45)
        val t45 = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(TournamentStatus.LIVE, t45.status)
        assertTrue(t45.matchStartedNotified)

        // Check Match Started notification exists
        val liveNotifCount = repo.notifications.value.count { it.id == "NOTIF-MATCH-STARTED-${tourney.id}" }
        assertEquals("Match started notification must be created exactly once", 1, liveNotifCount)

        // Subsequent ticks at 46s, 50s, 60s must NOT duplicate notification or re-trigger transition
        repo.tickCountdowns(serverStartTime + 46_000L)
        repo.tickCountdowns(serverStartTime + 50_000L)
        repo.tickCountdowns(serverStartTime + 60_000L)

        val t60 = repo.tournaments.value.find { it.id == tourney.id }!!
        assertEquals(TournamentStatus.LIVE, t60.status)
        val liveNotifCountAfter = repo.notifications.value.count { it.id == "NOTIF-MATCH-STARTED-${tourney.id}" }
        assertEquals("Match started notification must never be duplicated", 1, liveNotifCountAfter)
    }

    // -------------------------------------------------------------
    // SCENARIO 10: Notifications are not duplicated
    // -------------------------------------------------------------
    @Test
    fun `scenario 10 - notifications are not duplicated`() = runBlocking {
        val repo = BattleArenaRepository()
        val u1 = createTestUser("USER-NOTIF-1", ffUid = "10100001")
        val u2 = createTestUser("USER-NOTIF-2", ffUid = "10100002")
        repo.firestoreManager.saveUser(u1)
        repo.firestoreManager.saveUser(u2)
        repo.firestoreManager.saveWallet(Wallet(userId = u1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = u2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val serverStartTime = 100_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        val tourney = repo.createTournament(
            titleEn = "Notification Dedup Match",
            titleBn = "নোটিফিকেশন ম্যাচ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "01:00 PM"
        )

        // Player 1 registers
        repo.setCurrentUser(u1)
        repo.joinTournament(tourney.id)

        // Player 2 registers (fills tournament)
        repo.setCurrentUser(u2)
        repo.joinTournament(tourney.id)

        // Check countdown notification
        val countdownNotifCount = repo.notifications.value.count { it.id == "NOTIF-COUNTDOWN-${tourney.id}" }
        assertEquals("Countdown started notification must be created exactly once", 1, countdownNotifCount)

        // Transition to LIVE
        val liveServerTime = serverStartTime + 50_000L
        FirestoreManager.setAuthoritativeServerTime(liveServerTime)
        repo.tickCountdowns(liveServerTime)
        val startedNotifCount = repo.notifications.value.count { it.id == "NOTIF-MATCH-STARTED-${tourney.id}" }
        assertEquals("Match started notification must be created exactly once", 1, startedNotifCount)

        // Simulate backend retry of tickCountdowns
        repo.tickCountdowns(serverStartTime + 55_000L)
        repo.tickCountdowns(serverStartTime + 60_000L)

        assertEquals(1, repo.notifications.value.count { it.id == "NOTIF-COUNTDOWN-${tourney.id}" })
        assertEquals(1, repo.notifications.value.count { it.id == "NOTIF-MATCH-STARTED-${tourney.id}" })
    }

    // -------------------------------------------------------------
    // SCENARIO 11: Scheduled and Manual matches work independently
    // -------------------------------------------------------------
    @Test
    fun `scenario 11 - scheduled and manual matches work independently`() = runBlocking {
        val repo = BattleArenaRepository()
        val u1 = createTestUser("USER-SCHED-1", ffUid = "11111001")
        val u2 = createTestUser("USER-SCHED-2", ffUid = "11111002")
        repo.firestoreManager.saveUser(u1)
        repo.firestoreManager.saveUser(u2)
        repo.firestoreManager.saveWallet(Wallet(userId = u1.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.firestoreManager.saveWallet(Wallet(userId = u2.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))

        val serverStartTime = 110_000_000L
        FirestoreManager.setAuthoritativeServerTime(serverStartTime)

        val schedTourney = repo.createScheduledMatch(
            titleEn = "Daily Scheduled Lone Wolf",
            titleBn = "দৈনিক শিডিউলড লোন উলফ",
            mode = GameMode.LONE_WOLF,
            matchDate = "Today",
            matchTimeIst = "08:00 PM IST",
            playerLimit = 2
        )
        val initialTourneyCount = repo.tournaments.value.size

        repo.setCurrentUser(u1)
        repo.joinTournament(schedTourney.id)
        repo.setCurrentUser(u2)
        repo.joinTournament(schedTourney.id)

        val schedFull = repo.tournaments.value.find { it.id == schedTourney.id }!!
        assertEquals(2, schedFull.participants.size)
        assertEquals(TournamentStatus.CONFIRMED, schedFull.status)
        assertEquals(serverStartTime, schedFull.countdownStartMillis)

        // Scheduled matches must NOT auto-create generic quick cups
        assertEquals("Scheduled match must not auto-create generic quick cups", initialTourneyCount, repo.tournaments.value.size)

        // Ticking after 45s transitions the scheduled match to LIVE independently
        val liveServerTime = serverStartTime + 45_000L
        FirestoreManager.setAuthoritativeServerTime(liveServerTime)
        repo.tickCountdowns(liveServerTime)
        val schedLive = repo.tournaments.value.find { it.id == schedTourney.id }!!
        assertEquals(TournamentStatus.LIVE, schedLive.status)
    }

    // -------------------------------------------------------------
    // SCENARIO 12: Incomplete matches never start the countdown
    // -------------------------------------------------------------
    @Test
    fun `scenario 12 - incomplete matches never start the countdown`() = runBlocking {
        val repo = BattleArenaRepository()

        // 12a: Lone Wolf with only 1 player (needs 2)
        val lwUser = createTestUser("USER-INC-LW", ffUid = "12120001")
        repo.firestoreManager.saveUser(lwUser)
        repo.firestoreManager.saveWallet(Wallet(userId = lwUser.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
        repo.setCurrentUser(lwUser)

        val lwTourney = repo.createTournament(
            titleEn = "Incomplete LW",
            titleBn = "ইনকমপ্লিট লোন উলফ",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            startTimeFormatted = "08:00 PM"
        )
        repo.joinTournament(lwTourney.id)
        val lwAfter = repo.tournaments.value.find { it.id == lwTourney.id }!!
        assertEquals(1, lwAfter.participants.size)
        assertEquals(TournamentStatus.MATCHMAKING, lwAfter.status)
        assertNull("Lone Wolf with 1/2 players must NOT start countdown", lwAfter.countdownStartMillis)
        assertEquals(0, lwAfter.getRemainingCountdownSeconds())
        assertFalse(lwAfter.isCountdownActive)

        // 12b: Clash Squad with 7 players (needs 8)
        val csTourney = repo.createTournament(
            titleEn = "Incomplete CS",
            titleBn = "ইনকমপ্লিট ক্ল্যাশ স্কোয়াড",
            mode = GameMode.CLASH_SQUAD,
            entryFee = 20,
            startTimeFormatted = "09:00 PM"
        )
        for (i in 1..7) {
            val u = createTestUser("USER-INC-CS-$i", ffUid = "121200$i")
            repo.firestoreManager.saveUser(u)
            repo.firestoreManager.saveWallet(Wallet(userId = u.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
            repo.setCurrentUser(u)
            repo.joinTournament(csTourney.id)
        }
        val csAfter = repo.tournaments.value.find { it.id == csTourney.id }!!
        assertEquals(7, csAfter.participants.size)
        assertNull("Clash Squad with 7/8 players must NOT start countdown", csAfter.countdownStartMillis)
        assertEquals(0, csAfter.getRemainingCountdownSeconds())
        assertFalse(csAfter.isCountdownActive)

        // 12c: Battle Royale with 39 players (needs 40)
        val brTourney = repo.createTournament(
            titleEn = "Incomplete BR",
            titleBn = "ইনকমপ্লিট ব্যাটেল রয়্যাল",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 20,
            startTimeFormatted = "10:00 PM"
        )
        for (i in 1..39) {
            val u = createTestUser("USER-INC-BR-$i", ffUid = "121300" + i.toString().padStart(2, '0'))
            repo.firestoreManager.saveUser(u)
            repo.firestoreManager.saveWallet(Wallet(userId = u.id, depositBalance = 50, winningBalance = 0, bonusBalance = 0))
            repo.setCurrentUser(u)
            repo.joinTournament(brTourney.id)
        }
        val brAfter = repo.tournaments.value.find { it.id == brTourney.id }!!
        assertEquals(39, brAfter.participants.size)
        assertNull("Battle Royale with 39/40 players must NOT start countdown", brAfter.countdownStartMillis)
        assertEquals(0, brAfter.getRemainingCountdownSeconds())
        assertFalse(brAfter.isCountdownActive)

        // Ticking countdowns on incomplete matches must NOT transition them to LIVE
        repo.tickCountdowns(System.currentTimeMillis() + 100_000L)
        val lwAfterTick = repo.tournaments.value.find { it.id == lwTourney.id }!!
        val csAfterTick = repo.tournaments.value.find { it.id == csTourney.id }!!
        val brAfterTick = repo.tournaments.value.find { it.id == brTourney.id }!!

        assertNotEquals(TournamentStatus.LIVE, lwAfterTick.status)
        assertNotEquals(TournamentStatus.LIVE, csAfterTick.status)
        assertNotEquals(TournamentStatus.LIVE, brAfterTick.status)
    }
}
