package com.example.model

data class Wallet(
    val userId: String = "",
    val depositBalance: Int = 50,
    val winningBalance: Int = 0,
    val bonusBalance: Int = 10,
    val reservedBalance: Int = 0, // Virtual winning balance currently reserved for pending withdrawal
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Total virtual funds owned by the player (including funds currently reserved in pending withdrawals)
    val totalBalance: Int
        get() = depositBalance + winningBalance + bonusBalance + reservedBalance

    // Available funds that can be spent on match entry fees
    val availableBalance: Int
        get() = depositBalance + winningBalance + bonusBalance

    // Virtual prize winnings that can be requested for withdrawal
    val withdrawableBalance: Int
        get() = winningBalance
}

enum class TransactionType {
    ADD_MONEY,
    ENTRY_FEE,
    PRIZE_MONEY,
    MATCH_WINNING,
    REFERRAL_BONUS,
    WITHDRAWAL,
    REFUND
}

enum class TransactionStatus {
    SUCCESS,
    PENDING,
    FAILED
}

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.ADD_MONEY,
    val amount: Int = 0,
    val status: TransactionStatus = TransactionStatus.SUCCESS,
    val timestamp: Long = System.currentTimeMillis(),
    val referenceId: String = "",
    val titleEn: String = "",
    val titleBn: String = "",
    val subtitleEn: String = "",
    val subtitleBn: String = ""
)

enum class WithdrawalStatus {
    PENDING,
    APPROVED,
    PROCESSING,
    PAID,
    REJECTED,
    FAILED
}

data class WithdrawalRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val freeFireUid: String,
    val amount: Int,
    val method: String = "UPI", // "UPI"
    val payoutDestination: String, // e.g. "player@paytm" or "9876543210@ibl"
    val status: WithdrawalStatus = WithdrawalStatus.PENDING,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val paymentReference: String? = null, // Bank UTR or Transaction Reference entered upon manual real money payment
    val adminNotes: String? = null,
    val rejectionReason: String? = null
)
