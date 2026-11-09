package com.example.model

import com.example.data.FirestoreManager

enum class MatchCreationType(val displayName: String) {
    SCHEDULED("Scheduled"),
    MANUAL("Manual")
}

enum class RoomPublicationStatus(val displayName: String) {
    PENDING("Pending"),
    PUBLISHED("Published"),
    HIDDEN("Hidden"),
    CLOSED("Closed")
}

enum class TournamentStatus {
    UPCOMING,
    REGISTRATION_OPEN,
    WAITING_FOR_PLAYERS,
    MATCHMAKING,
    CONFIRMED,
    ROOM_PENDING,
    ROOM_PUBLISHED,
    COUNTDOWN,
    LIVE,
    VERIFYING,
    COMPLETED,
    UNSUCCESSFUL,
    CANCELLED
}

enum class RefundStatus {
    PENDING,
    APPROVED,
    REFUNDED,
    REJECTED
}

data class MatchRefundItem(
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val userId: String,
    val userName: String,
    val freeFireUid: String,
    val entryFee: Int,
    val status: RefundStatus = RefundStatus.PENDING,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val reason: String = "Match Unsuccessful",
    val approvedBy: String = ""
)

data class AdminAuditLog(
    val id: String,
    val action: String,
    val targetId: String,
    val details: String,
    val performedBy: String = "kirtaniamanik2010@gmail.com",
    val timestamp: Long = System.currentTimeMillis()
)

enum class PlayerVerificationStatus {
    VERIFIED,
    PENDING_CHECK,
    UNAUTHORIZED,
    KICKED
}

enum class RoomAccessState {
    NOT_REGISTERED,
    UNPAID,
    WAITING_CONFIRMATION,
    UNLOCKED,
    EXPIRED,
    UNAUTHORIZED_OR_KICKED
}

data class RoomRosterAuditReport(
    val tournamentId: String,
    val totalAudited: Int,
    val authorizedCount: Int,
    val unauthorizedUids: List<String>,
    val missingAuthorizedUids: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class PlayerParticipant(
    val userId: String,
    val name: String,
    val freeFireName: String,
    val freeFireUid: String,
    val entryId: String = "",
    val registeredAt: Long = System.currentTimeMillis(),
    val team: String? = null, // "Team A" or "Team B" for CS
    val kills: Int = 0,
    val placement: Int = 0,
    val prizeEarned: Int = 0,
    val isWinner: Boolean = false,
    val isPaid: Boolean = true,
    val entryFeePaid: Int = 0,
    val verificationStatus: PlayerVerificationStatus = PlayerVerificationStatus.VERIFIED,
    val checkedInAt: Long? = null,
    val adminNotes: String? = null
)

data class BrPrizeTier(
    val rankRange: String,
    val startRank: Int,
    val endRank: Int,
    val prizePerPlayer: Int
) {
    val totalWinningPositions: Int
        get() = (endRank - startRank + 1)
    val totalTierPrize: Int
        get() = totalWinningPositions * prizePerPlayer
}

enum class PrizePaymentStatus {
    PENDING_VERIFICATION,
    APPROVED,
    PROCESSING,
    PAID,
    FAILED,
    REJECTED
}

data class MatchResultRecord(
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val userId: String,
    val playerName: String,
    val freeFireUid: String,
    val rank: Int,
    val kills: Int = 0,
    val resultSummary: String = "Rank #$rank",
    val placementPrize: Int = 0,
    val killPrize: Int = 0,
    val eligiblePrize: Int = placementPrize + killPrize,
    val isVerified: Boolean = false,
    val paymentStatus: PrizePaymentStatus = PrizePaymentStatus.PENDING_VERIFICATION,
    val payoutUtr: String? = null,
    val payoutTimestamp: Long? = null,
    val verifiedBy: String = ""
)

data class Tournament(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val mode: GameMode,
    val entryFee: Int,
    val totalPrizePool: Int,
    val totalSlots: Int,
    val mapName: String = "Bermuda",
    val startTimeFormatted: String = "08:00 PM",
    val startTimeMillis: Long = System.currentTimeMillis() + 3600000L,
    val status: TournamentStatus = TournamentStatus.UPCOMING,
    val roomId: String? = null,
    val roomPassword: String? = null,
    val rulesEn: List<String> = defaultRulesEn,
    val rulesBn: List<String> = defaultRulesBn,
    val participants: List<PlayerParticipant> = emptyList(),
    val winnerSummaryEn: String? = null,
    val winnerSummaryBn: String? = null,
    val adminCommission: Int = 0,
    val countdownStartMillis: Long? = null,
    val matchStartedNotified: Boolean = false,
    val waitingPlayerJoinedAt: Long? = null,
    val scheduledDailyTime: String? = "08:00 PM IST",
    val isRefundProcessed: Boolean = false,
    val refundedPlayerIds: List<String> = emptyList(),
    val brPrizeChart: List<BrPrizeTier> = defaultBrPrizeChart,
    val brKillPrizePool: Int = 110,
    val creationType: MatchCreationType = MatchCreationType.SCHEDULED,
    val matchDate: String = "Today",
    val matchTimeIst: String = "08:00 AM IST",
    val descriptionEn: String = "",
    val descriptionBn: String = "",
    val roomStatus: RoomPublicationStatus = RoomPublicationStatus.PENDING,
    val roomPublishedAt: Long? = null,
    val roomPublishedBy: String? = null,
    val isRegistrationOpen: Boolean = true,
    val customRoomNotes: String? = null,
    val isSettled: Boolean = false,
    val settlementId: String? = null,
    val settledAt: Long? = null,
    val settledBy: String? = null
) {
    val title: String
        get() = titleEn

    val customRoomId: String?
        get() = roomId

    val customRoomPassword: String?
        get() = roomPassword

    val filledSlots: Int
        get() = participants.size

    val isFull: Boolean
        get() = filledSlots >= totalSlots

    val dynamicPlacementPrizePool: Int
        get() = if (mode == GameMode.BATTLE_ROYALE) brPrizeChart.sumOf { it.totalTierPrize } else totalPrizePool

    val dynamicTotalPrizePool: Int
        get() = if (mode == GameMode.BATTLE_ROYALE) dynamicPlacementPrizePool + brKillPrizePool else totalPrizePool

    val totalWinningPositions: Int
        get() = when (mode) {
            GameMode.BATTLE_ROYALE -> brPrizeChart.sumOf { it.totalWinningPositions }
            GameMode.CLASH_SQUAD -> 4
            GameMode.LONE_WOLF -> 1
        }

    fun getPrizeForRank(rank: Int): Int {
        if (mode != GameMode.BATTLE_ROYALE) return 0
        return brPrizeChart.find { rank in it.startRank..it.endRank }?.prizePerPlayer ?: 0
    }

    fun isCountdownActive(serverCurrentTimeMillis: Long = FirestoreManager.getServerTimeMillis()): Boolean {
        if (status != TournamentStatus.CONFIRMED && status != TournamentStatus.COUNTDOWN) return false
        val start = countdownStartMillis ?: return false
        val elapsedMillis = serverCurrentTimeMillis - start
        return elapsedMillis in 0 until 45_000L
    }

    val isCountdownActive: Boolean
        get() = isCountdownActive(FirestoreManager.getServerTimeMillis())

    fun getRemainingCountdownSeconds(serverCurrentTimeMillis: Long = FirestoreManager.getServerTimeMillis()): Int {
        val start = countdownStartMillis ?: return 0
        val elapsed = (serverCurrentTimeMillis - start) / 1000
        return maxOf(0, (45 - elapsed).toInt())
    }

    val isRefundEligible: Boolean
        get() = (status == TournamentStatus.UNSUCCESSFUL || status == TournamentStatus.CANCELLED) &&
                !isRefundProcessed &&
                participants.isNotEmpty()

    fun isUserJoined(userId: String): Boolean {
        return participants.any { it.userId == userId }
    }

    fun isFreeFireUidRegistered(ffUid: String): Boolean {
        return participants.any { it.freeFireUid.trim().equals(ffUid.trim(), ignoreCase = true) }
    }

    fun getParticipant(userId: String): PlayerParticipant? {
        return participants.find { it.userId == userId }
    }

    fun getEntryIdForUser(userId: String): String? {
        return participants.find { it.userId == userId }?.entryId
    }

    fun isRoomCredentialsUnlockedForUser(userId: String): Boolean {
        return getRoomAccessState(userId) == RoomAccessState.UNLOCKED
    }

    fun getRoomAccessState(userId: String): RoomAccessState {
        val participant = getParticipant(userId) ?: return RoomAccessState.NOT_REGISTERED
        if (!participant.isPaid) return RoomAccessState.UNPAID
        if (participant.verificationStatus == PlayerVerificationStatus.KICKED ||
            participant.verificationStatus == PlayerVerificationStatus.UNAUTHORIZED) {
            return RoomAccessState.UNAUTHORIZED_OR_KICKED
        }
        if (status == TournamentStatus.COMPLETED || status == TournamentStatus.UNSUCCESSFUL || status == TournamentStatus.CANCELLED) {
            return RoomAccessState.EXPIRED
        }
        if (roomStatus == RoomPublicationStatus.CLOSED) {
            return RoomAccessState.EXPIRED
        }
        if (roomStatus == RoomPublicationStatus.HIDDEN) {
            return RoomAccessState.WAITING_CONFIRMATION
        }
        // If Room Details are published by Admin, or match is in CONFIRMED/COUNTDOWN/LIVE state with room credentials present
        if (roomStatus == RoomPublicationStatus.PUBLISHED ||
            status == TournamentStatus.ROOM_PUBLISHED ||
            status == TournamentStatus.CONFIRMED ||
            status == TournamentStatus.COUNTDOWN ||
            status == TournamentStatus.LIVE) {
            return RoomAccessState.UNLOCKED
        }
        if (status == TournamentStatus.UPCOMING ||
            status == TournamentStatus.REGISTRATION_OPEN ||
            status == TournamentStatus.WAITING_FOR_PLAYERS ||
            status == TournamentStatus.ROOM_PENDING ||
            status == TournamentStatus.MATCHMAKING) {
            return RoomAccessState.WAITING_CONFIRMATION
        }
        return RoomAccessState.UNLOCKED
    }

    fun getTitle(lang: AppLanguage): String {
        return AppStrings.formatMatchTitle(id, mode, lang, titleEn, titleBn)
    }

    fun getModeTitle(lang: AppLanguage): String {
        return AppStrings.gameModeTitle(mode, lang)
    }

    companion object {
        val defaultBrPrizeChart = listOf(
            BrPrizeTier("Rank 1 (Booyah Champion)", 1, 1, 100),
            BrPrizeTier("Rank 2", 2, 2, 70),
            BrPrizeTier("Rank 3 - 4", 3, 4, 50),
            BrPrizeTier("Rank 5", 5, 5, 40),
            BrPrizeTier("Rank 6 - 10", 6, 10, 30)
        )

        val defaultRulesEn = listOf(
            "Fair Play only: Strict zero tolerance for hacks, scripts, or game modifications.",
            "Only mobile players allowed (no emulators/PC).",
            "Must register with valid Free Fire In-Game Name (IGN) and Player UID.",
            "Room ID & Password will be provided 15 minutes before the match start time.",
            "Screenshot of final match standing must be saved in case of any dispute.",
            "Prize money is credited automatically to winning wallets upon Admin verification."
        )

        val defaultRulesBn = listOf(
            "ফেয়ার প্লে নীতি: হ্যাক, স্ক্রিপ্ট বা গেম মডিফিকেশন সম্পূর্ণ নিষিদ্ধ।",
            "শুধুমাত্র মোবাইল প্লেয়ারদের জন্য (কোনো এমুলেটর গ্রহণযোগ্য নয়)।",
            "সঠিক ফ্রি ফায়ার প্লেয়ার নাম এবং প্লেয়ার UID দিয়ে অংশগ্রহণ করতে হবে।",
            "ম্যাচ শুরুর ১৫ মিনিট আগে রুম আইডি এবং পাসওয়ার্ড দেওয়া হবে।",
            "যেকোনো বিরোধ নিষ্পত্তির জন্য ফাইনাল ম্যাচের স্ক্রিনশট রাখা আবশ্যক।",
            "অ্যাডমিন ভেরিফিকেশনের পর প্রাইজ মানি স্বয়ংক্রিয়ভাবে ওয়ালেটে জমা হবে।"
        )
    }
}

data class PlatformCommissionRecord(
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val mode: GameMode,
    val totalEntryCollected: Int,
    val totalPrizeDistributed: Int,
    val commissionEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
