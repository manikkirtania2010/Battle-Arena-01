package com.example

import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.GameMode
import com.example.model.MatchCreationType
import com.example.model.RoomAccessState
import com.example.model.RoomPublicationStatus
import com.example.model.TournamentStatus
import com.example.model.User
import com.example.model.Wallet
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdminCustomRoomSystemTest {

    @Before
    fun setUp() {
        FirestoreManager.resetCloudDatabaseForTesting()
    }

    @Test
    fun `scheduled match creation creates independent match with IST schedule`() = runBlocking {
        val repo = BattleArenaRepository()
        val initialCount = repo.tournaments.value.size

        val match = repo.createScheduledMatch(
            titleEn = "BR Pro Championship Series - Match 1",
            titleBn = "বিআর প্রো চ্যাম্পিয়নশিপ - ম্যাচ ১",
            mode = GameMode.BATTLE_ROYALE,
            matchDate = "05 Nov 2026",
            matchTimeIst = "08:00 AM IST",
            playerLimit = 40,
            entryFee = 25,
            descriptionEn = "High-tier BR custom room. 40 verified players.",
            descriptionBn = "উচ্চ-মানের বিআর কাস্টম রুম। ৪০ জন যাচাইকৃত খেলোয়াড়।",
            mapName = "Bermuda"
        )

        assertNotNull("Generated match should not be null", match)
        val created = repo.tournaments.value.find { it.id == match.id }
        assertNotNull("Match must exist in repository", created)
        assertEquals(MatchCreationType.SCHEDULED, created!!.creationType)
        assertEquals("05 Nov 2026", created.matchDate)
        assertEquals("08:00 AM IST", created.matchTimeIst)
        assertEquals(40, created.totalSlots)
        assertEquals(25, created.entryFee)
        assertEquals(RoomPublicationStatus.PENDING, created.roomStatus)
        assertEquals(initialCount + 1, repo.tournaments.value.size)
    }

    @Test
    fun `batch schedule generates consecutive 5 scheduled matches without interference`() = runBlocking {
        val repo = BattleArenaRepository()
        val initialCount = repo.tournaments.value.size

        val times = listOf("08:00 AM IST", "08:30 AM IST", "09:00 AM IST", "09:30 AM IST", "10:00 AM IST")
        val createdMatches = repo.createBatchSchedule(
            mode = GameMode.BATTLE_ROYALE,
            matchDate = "Today",
            startTimes = times,
            entryFee = 20
        )

        assertEquals(5, createdMatches.size)
        assertEquals(initialCount + 5, repo.tournaments.value.size)

        // Verify all 5 have unique IDs and correct IST times
        val distinctIds = createdMatches.map { it.id }.toSet()
        assertEquals("Every scheduled match must have a unique ID", 5, distinctIds.size)

        createdMatches.forEachIndexed { idx, match ->
            val found = repo.tournaments.value.find { it.id == match.id }
            assertNotNull(found)
            assertEquals(times[idx], found!!.matchTimeIst)
            assertEquals(MatchCreationType.SCHEDULED, found.creationType)
            assertEquals(0, found.participants.size)
        }
    }

    @Test
    fun `manual match creation creates on-demand match without scheduled interference`() = runBlocking {
        val repo = BattleArenaRepository()
        val initialCount = repo.tournaments.value.size

        val manualMatch = repo.createManualMatch(
            titleEn = "Flash Lone Wolf 1v1 Showdown",
            titleBn = "ফ্ল্যাশ লোন উলফ ১v১ লড়াই",
            mode = GameMode.LONE_WOLF,
            playerLimit = 2,
            entryFee = 10,
            descriptionEn = "Immediate 1v1 on-demand duel",
            descriptionBn = "তাত্ক্ষণিক ১v১ অন-ডিমান্ড লড়াই",
            isRegistrationOpen = true,
            mapName = "Iron Cage"
        )

        val found = repo.tournaments.value.find { it.id == manualMatch.id }
        assertNotNull("Manual match must exist", found)
        assertEquals(MatchCreationType.MANUAL, found!!.creationType)
        assertEquals(2, found.totalSlots)
        assertEquals(10, found.entryFee)
        assertTrue(found.isRegistrationOpen)
        assertEquals(initialCount + 1, repo.tournaments.value.size)
    }

    @Test
    fun `room credentials lifecycle - publish, hide, close, and strict participant gating`() = runBlocking {
        val repo = BattleArenaRepository()

        // Setup eligible user with wallet balance
        val testUser = User(
            id = "user_alpha_1",
            playerId = "BA-10001",
            name = "Alpha Player",
            freeFireName = "AlphaFF",
            freeFireUid = "9988776655",
            email = "alpha@battlearena.in",
            freeFireLevel = 25
        )
        repo.saveCloudUser(testUser)
        repo.saveCloudWallet(Wallet(userId = testUser.id, depositBalance = 500))

        // Create a scheduled match
        val match = repo.createScheduledMatch(
            titleEn = "BR Midnight Clash",
            titleBn = "বিআর মিডনাইট ক্ল্যাশ",
            mode = GameMode.BATTLE_ROYALE,
            matchDate = "Tonight",
            matchTimeIst = "11:00 PM IST",
            playerLimit = 40,
            entryFee = 20,
            descriptionEn = "BR Match",
            descriptionBn = "বিআর ম্যাচ",
            mapName = "Purgatory"
        )

        var currentMatch = repo.tournaments.value.first { it.id == match.id }

        // Unregistered player cannot see room credentials
        assertEquals(RoomAccessState.NOT_REGISTERED, currentMatch.getRoomAccessState(testUser.id))

        // Join match with payment
        val (joinSuccess, joinMsg) = repo.joinTournament(match.id)
        assertTrue("Player should successfully join with payment: $joinMsg", joinSuccess)

        currentMatch = repo.tournaments.value.first { it.id == match.id }
        assertTrue(currentMatch.isUserJoined(testUser.id))

        // Room details not published yet -> WAITING_CONFIRMATION
        assertEquals(RoomAccessState.WAITING_CONFIRMATION, currentMatch.getRoomAccessState(testUser.id))
        assertFalse(currentMatch.isRoomCredentialsUnlockedForUser(testUser.id))

        // Admin publishes room details as verified App Owner
        val adminOwner = User(uid = "owner_admin", email = BATTLE_ARENA_OWNER_EMAIL, isAdmin = true)
        repo.setCurrentUser(adminOwner)

        val (publishSuccess, _) = repo.updateRoomDetails(
            tournamentId = match.id,
            roomId = "88990011",
            roomPassword = "pass#arena77",
            roomStatus = RoomPublicationStatus.PUBLISHED,
            notes = "Join Slot 1-40. Emote allowed."
        )
        assertTrue(publishSuccess)

        // Switch back to player to verify participant access
        repo.setCurrentUser(testUser)

        currentMatch = repo.tournaments.value.first { it.id == match.id }
        assertEquals(RoomPublicationStatus.PUBLISHED, currentMatch.roomStatus)
        assertEquals("88990011", currentMatch.roomId)
        assertEquals("pass#arena77", currentMatch.roomPassword)

        // Confirmed paid player can now unlock room credentials
        assertEquals(RoomAccessState.UNLOCKED, currentMatch.getRoomAccessState(testUser.id))
        assertTrue(currentMatch.isRoomCredentialsUnlockedForUser(testUser.id))

        // An unauthorized / non-registered user cannot see it
        assertEquals(RoomAccessState.NOT_REGISTERED, currentMatch.getRoomAccessState("random_intruder_99"))

        // Admin Hides room details
        repo.setCurrentUser(adminOwner)
        repo.hideRoomDetails(match.id)
        currentMatch = repo.tournaments.value.first { it.id == match.id }
        assertEquals(RoomPublicationStatus.HIDDEN, currentMatch.roomStatus)
        assertEquals(RoomAccessState.WAITING_CONFIRMATION, currentMatch.getRoomAccessState(testUser.id))

        // Admin Closes room access
        repo.closeRoomAccess(match.id)
        currentMatch = repo.tournaments.value.first { it.id == match.id }
        assertEquals(RoomPublicationStatus.CLOSED, currentMatch.roomStatus)
        assertEquals(RoomAccessState.EXPIRED, currentMatch.getRoomAccessState(testUser.id))
    }

    @Test
    fun `schedule editing updates date, time IST, title, and slots`() = runBlocking {
        val repo = BattleArenaRepository()
        val match = repo.createScheduledMatch(
            titleEn = "BR Early Slot",
            titleBn = "বিআর আর্লি স্লট",
            mode = GameMode.BATTLE_ROYALE,
            matchDate = "Today",
            matchTimeIst = "07:00 AM IST",
            playerLimit = 40,
            entryFee = 20,
            descriptionEn = "Early morning match",
            descriptionBn = "সকালের ম্যাচ",
            mapName = "Bermuda"
        )

        val (updated, _) = repo.editMatchSchedule(
            tournamentId = match.id,
            newDate = "Tomorrow",
            newTimeIst = "08:30 AM IST",
            newSlots = 48,
            newTitleEn = "BR Rescheduled Championship Slot"
        )
        assertTrue(updated)

        val updatedMatch = repo.tournaments.value.first { it.id == match.id }
        assertEquals("Tomorrow", updatedMatch.matchDate)
        assertEquals("08:30 AM IST", updatedMatch.matchTimeIst)
        assertEquals(48, updatedMatch.totalSlots)
        assertEquals("BR Rescheduled Championship Slot", updatedMatch.titleEn)
    }

    @Test
    fun `registration toggle blocks joining when closed`() = runBlocking {
        val repo = BattleArenaRepository()

        val testUser = User(
            id = "user_reg_test",
            playerId = "BA-20002",
            name = "Reg Tester",
            freeFireName = "RegTester",
            freeFireUid = "11223344",
            email = "reg@battlearena.in",
            freeFireLevel = 30
        )
        repo.saveCloudUser(testUser)
        repo.saveCloudWallet(Wallet(userId = testUser.id, depositBalance = 500))

        val match = repo.createManualMatch(
            titleEn = "Locked Room",
            titleBn = "লকড রুম",
            mode = GameMode.CLASH_SQUAD,
            playerLimit = 8,
            entryFee = 20,
            descriptionEn = "Test locked",
            descriptionBn = "টেস্ট",
            isRegistrationOpen = false,
            mapName = "Kalahari"
        )

        // Attempting to join a closed match should fail
        val (joinResult, joinMsg) = repo.joinTournament(match.id)
        assertFalse("Cannot join when registration is closed: $joinMsg", joinResult)

        // Open registration
        repo.toggleMatchRegistration(match.id, true)
        val updatedMatch = repo.tournaments.value.first { it.id == match.id }
        assertTrue(updatedMatch.isRegistrationOpen)

        // Now joining succeeds
        val (joinResultAfter, _) = repo.joinTournament(match.id)
        assertTrue("Can join after registration opened", joinResultAfter)
    }
}
