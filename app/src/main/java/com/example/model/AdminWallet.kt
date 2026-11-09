package com.example.model

enum class AdminWithdrawalStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    REVERSED
}

enum class AdminLedgerEntryType {
    MATCH_COMMISSION,
    PRIZE_PAYOUT,
    REFUND_DEDUCTION,
    UPI_WITHDRAWAL,
    WITHDRAWAL_REVERSAL
}

data class AdminLedgerEntry(
    val id: String,
    val type: AdminLedgerEntryType,
    val amount: Int,
    val descriptionEn: String,
    val descriptionBn: String,
    val referenceId: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AdminWithdrawalRecord(
    val id: String,
    val amount: Int,
    val destinationUpiId: String,
    val status: AdminWithdrawalStatus = AdminWithdrawalStatus.PENDING,
    val providerReference: String = "",
    val payoutUtr: String = "",
    val failureReason: String? = null,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val idempotencyKey: String = ""
)

data class AdminWallet(
    val availableBalance: Int = 280, // Initial balance from host commissions
    val reservedBalance: Int = 0,
    val totalMatchEarnings: Int = 280,
    val totalPrizePaid: Int = 730,
    val totalRefundsPaid: Int = 0,
    val totalWithdrawalsPaid: Int = 0,
    val verifiedUpiId: String = "kirtaniamanik@okhdfcbank",
    val isUpiVerified: Boolean = true
) {
    val totalBalance: Int
        get() = availableBalance + reservedBalance
}
