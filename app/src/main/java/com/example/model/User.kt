package com.example.model

const val BATTLE_ARENA_OWNER_EMAIL = "kirtaniamanik2010@gmail.com"

enum class LevelVerificationStatus {
    UNVERIFIED,
    PENDING,
    VERIFIED,
    REJECTED
}

enum class AccountStatus(val displayName: String) {
    ACTIVE("Active"),
    TEMPORARILY_SUSPENDED("Temporarily Suspended"),
    PERMANENTLY_BANNED("Permanently Banned")
}

/**
 * User data class representing player profile and Firestore database collections.
 * Contains primary fields: uid, displayName, email, freeFireUID, walletBalance, referralCode.
 * Includes Free Fire level (must be Level 10+), level verification status, ban status, and owner verification.
 */
data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val freeFireUID: String = "",
    val freeFireName: String = "",
    val freeFireLevel: Int = 12, // Level 10+ required for tournament eligibility
    val levelVerificationStatus: LevelVerificationStatus = LevelVerificationStatus.VERIFIED,
    val walletBalance: Int = 0,
    val referralCode: String = "",
    val phone: String = "+91 98765 43210",
    val avatarId: Int = 1,
    val referredBy: String? = null,
    val totalKills: Int = 0,
    val totalWins: Int = 0,
    val totalEarnings: Int = 0,
    val totalTournaments: Int = 0,
    val isAdmin: Boolean = false,
    val isVerified: Boolean = true,
    val isBanned: Boolean = false,
    val banReason: String? = null,
    val playerId: String = "", // Permanent Player ID e.g. "BA-70912"
    val languageCode: String = "en", // Cloud-saved language preference
    val accountStatus: AccountStatus = if (isBanned) AccountStatus.PERMANENTLY_BANNED else AccountStatus.ACTIVE,
    val suspensionDurationHours: Int = 0,
    val suspensionExpiresAt: Long? = null,
    val bannedByAdmin: String? = null,
    val bannedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    // Owner is strictly identified by the designated Super Admin email
    val isOwner: Boolean
        get() = email.trim().equals(BATTLE_ARENA_OWNER_EMAIL, ignoreCase = true)

    // Check if ban or temporary suspension is currently active
    val isSuspensionActive: Boolean
        get() {
            if (accountStatus == AccountStatus.PERMANENTLY_BANNED || isBanned) return true
            if (accountStatus == AccountStatus.TEMPORARILY_SUSPENDED) {
                val expires = suspensionExpiresAt ?: return true
                return System.currentTimeMillis() < expires
            }
            return false
        }

    // Effective display status considering automatic suspension expiration
    val effectiveAccountStatus: AccountStatus
        get() {
            if (accountStatus == AccountStatus.PERMANENTLY_BANNED || isBanned) return AccountStatus.PERMANENTLY_BANNED
            if (accountStatus == AccountStatus.TEMPORARILY_SUSPENDED) {
                val expires = suspensionExpiresAt
                if (expires != null && System.currentTimeMillis() >= expires) {
                    return AccountStatus.ACTIVE
                }
                return AccountStatus.TEMPORARILY_SUSPENDED
            }
            return AccountStatus.ACTIVE
        }

    // Eligibility check: Player must be level 10 or above and not banned/suspended
    val isEligibleForTournament: Boolean
        get() = freeFireLevel >= 10 && !isSuspensionActive

    // Human-readable remaining suspension time
    val suspensionRemainingFormatted: String
        get() {
            if (effectiveAccountStatus == AccountStatus.PERMANENTLY_BANNED) {
                return "Permanent Ban"
            }
            if (effectiveAccountStatus == AccountStatus.TEMPORARILY_SUSPENDED) {
                val expires = suspensionExpiresAt ?: return "Suspended indefinitely"
                val remainingMs = expires - System.currentTimeMillis()
                if (remainingMs <= 0) return "Suspension Expired"
                val hours = remainingMs / 3600_000L
                val minutes = (remainingMs % 3600_000L) / 60_000L
                return if (hours > 0) "${hours}h ${minutes}m remaining" else "${minutes}m remaining"
            }
            return "Active"
        }

    // Secondary constructor to maintain compatibility with legacy parameter names
    constructor(
        id: String,
        name: String,
        email: String,
        freeFireName: String = "",
        freeFireUid: String = "",
        freeFireLevel: Int = 12,
        walletBalance: Int = 0,
        phone: String = "+91 98765 43210",
        avatarId: Int = 1,
        referralCode: String = "",
        referredBy: String? = null,
        totalKills: Int = 0,
        totalWins: Int = 0,
        totalEarnings: Int = 0,
        totalTournaments: Int = 0,
        isAdmin: Boolean = false,
        isVerified: Boolean = true,
        isBanned: Boolean = false,
        banReason: String? = null,
        playerId: String = id,
        languageCode: String = "en",
        accountStatus: AccountStatus = if (isBanned) AccountStatus.PERMANENTLY_BANNED else AccountStatus.ACTIVE,
        suspensionDurationHours: Int = 0,
        suspensionExpiresAt: Long? = null,
        bannedByAdmin: String? = null,
        bannedAt: Long? = null,
        createdAt: Long = System.currentTimeMillis(),
        lastLoginAt: Long = System.currentTimeMillis()
    ) : this(
        uid = id,
        displayName = name,
        email = email,
        freeFireUID = freeFireUid,
        freeFireName = freeFireName,
        freeFireLevel = freeFireLevel,
        levelVerificationStatus = if (freeFireLevel >= 10) LevelVerificationStatus.VERIFIED else LevelVerificationStatus.UNVERIFIED,
        walletBalance = walletBalance,
        referralCode = referralCode,
        phone = phone,
        avatarId = avatarId,
        referredBy = referredBy,
        totalKills = totalKills,
        totalWins = totalWins,
        totalEarnings = totalEarnings,
        totalTournaments = totalTournaments,
        isAdmin = email.trim().equals(BATTLE_ARENA_OWNER_EMAIL, ignoreCase = true) || isAdmin,
        isVerified = isVerified,
        isBanned = isBanned,
        banReason = banReason,
        playerId = if (playerId.isNotBlank()) playerId else id,
        languageCode = languageCode,
        accountStatus = accountStatus,
        suspensionDurationHours = suspensionDurationHours,
        suspensionExpiresAt = suspensionExpiresAt,
        bannedByAdmin = bannedByAdmin,
        bannedAt = bannedAt,
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )

    // Backward-compatible property aliases
    val id: String get() = if (playerId.isNotBlank()) playerId else uid
    val name: String get() = displayName
    val freeFireUid: String get() = freeFireUID
}

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val name: String,
    val freeFireName: String,
    val freeFireUid: String,
    val avatarId: Int,
    val totalEarnings: Int,
    val totalKills: Int,
    val matchesWon: Int
)

enum class PaymentApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class AddMoneyRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val freeFireUid: String,
    val amount: Int,
    val paymentMethod: String,
    val utrNumber: String,
    val screenshotNote: String = "Screenshot Uploaded",
    val status: PaymentApprovalStatus = PaymentApprovalStatus.PENDING,
    val requestedAt: Long = System.currentTimeMillis(),
    val rejectionReason: String? = null
)

data class AppNotification(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val messageEn: String,
    val messageBn: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "GENERAL"
)

enum class IncidentReportStatus(val displayName: String) {
    PENDING("Pending"),
    UNDER_REVIEW("Under Review"),
    VERIFIED_VIOLATION("Verified Violation"),
    DISMISSED("Dismissed")
}

enum class CheatCategory(val displayName: String) {
    AIMBOT_AUTO_AIM("Aimbot / Auto-Aim"),
    WALLHACK_ESP("Wallhack / ESP / Antenna"),
    SPEED_HACK("Speed Hack / Teleport"),
    NO_RECOIL_DAMAGE_MOD("No Recoil / Damage Hack"),
    TEAMING("Teaming in Solo / Lone Wolf"),
    FAKE_RESULT_SUBMISSION("Duplicate / Inconsistent Result Submission"),
    OTHER("Suspicious Activity / Third-party Script")
}

data class PlayerAppeal(
    val id: String = "",
    val reportId: String = "",
    val userId: String = "",
    val playerId: String = "",
    val explanation: String = "",
    val proofUrl: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "REJECTED"
    val reviewedBy: String? = null,
    val reviewNotes: String? = null,
    val reviewedAt: Long? = null
)

data class IncidentReport(
    val id: String,
    val reportedUserId: String,
    val reportedUserName: String,
    val reportedUserUid: String,
    val tournamentId: String,
    val reason: String,
    val status: String = "INVESTIGATING", // Backward compatibility: "INVESTIGATING", "BANNED", "RESOLVED"
    val timestamp: Long = System.currentTimeMillis(),
    val reportedByUserId: String = "anonymous",
    val reporterPlayerId: String = "",
    val targetPlayerId: String = "",
    val category: CheatCategory = CheatCategory.OTHER,
    val reportStatus: IncidentReportStatus = IncidentReportStatus.PENDING,
    val evidenceText: String? = null,
    val evidenceUrl: String? = null,
    val investigationNotes: String? = null,
    val reviewedByAdmin: String? = null,
    val reviewedAt: Long? = null,
    val penaltyApplied: String? = null,
    val appeal: PlayerAppeal? = null
)
