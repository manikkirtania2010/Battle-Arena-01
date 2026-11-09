package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import com.example.data.BattleArenaNotificationManager
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Dialog
import android.widget.Toast
import com.example.model.AccountStatus
import com.example.model.AddMoneyRequest
import com.example.model.AdminAuditLog
import com.example.model.AdminLedgerEntry
import com.example.model.AdminLedgerEntryType
import com.example.model.AdminWallet
import com.example.model.AdminWithdrawalRecord
import com.example.model.AdminWithdrawalStatus
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.BrPrizeTier
import com.example.model.GameMode
import com.example.model.IncidentReport
import com.example.model.IncidentReportStatus
import com.example.model.CheatCategory
import com.example.model.PlayerAppeal
import com.example.model.MatchRefundItem
import com.example.model.MatchResultRecord
import com.example.model.PaymentApprovalStatus
import com.example.model.PlatformCommissionRecord
import com.example.model.PlayerParticipant
import com.example.model.PlayerVerificationStatus
import com.example.model.PrizePaymentStatus
import com.example.model.RefundStatus
import com.example.model.MatchCreationType
import com.example.model.RoomPublicationStatus
import com.example.model.RoomRosterAuditReport
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.model.User
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import com.example.ui.dialogs.CreateManualMatchDialog
import com.example.ui.dialogs.CreateScheduledMatchDialog
import com.example.ui.dialogs.ManageCustomRoomDialog
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Link
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.GamingPrimary
import com.example.ui.theme.GamingRed
import com.example.ui.theme.GamingSecondary
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.GamingSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    tournaments: List<Tournament>,
    withdrawals: List<WithdrawalRequest>,
    commissions: List<PlatformCommissionRecord>,
    addMoneyRequests: List<AddMoneyRequest> = emptyList(),
    incidentReports: List<IncidentReport> = emptyList(),
    refundRecords: List<MatchRefundItem> = emptyList(),
    adminAuditLogs: List<AdminAuditLog> = emptyList(),
    adminWallet: AdminWallet = AdminWallet(),
    adminWithdrawals: List<AdminWithdrawalRecord> = emptyList(),
    adminLedger: List<AdminLedgerEntry> = emptyList(),
    matchResults: List<MatchResultRecord> = emptyList(),
    isOwner: Boolean = true,
    language: AppLanguage,
    onCreateTournamentClick: () -> Unit,
    onVerifyTournamentClick: (Tournament) -> Unit,
    onUpdateRoomCredentials: (tournamentId: String, roomId: String, pass: String) -> Unit,
    onUpdateWithdrawalStatus: (requestId: String, status: WithdrawalStatus, reason: String?) -> Unit,
    onApproveAddMoney: (requestId: String) -> Unit = {},
    onRejectAddMoney: (requestId: String, reason: String) -> Unit = { _, _ -> },
    allPlayers: List<User> = emptyList(),
    onBanPlayer: (userId: String, reason: String) -> Unit = { _, _ -> },
    onSuspendPlayer: (targetPlayerId: String, durationHours: Int, reason: String) -> Unit = { _, _, _ -> },
    onPermanentlyBanPlayer: (targetPlayerId: String, reason: String) -> Unit = { _, _ -> },
    onUnbanPlayer: (userId: String) -> Unit = {},
    onMarkTournamentUnsuccessful: (tournamentId: String, reason: String) -> Unit = { _, _ -> },
    onApproveMatchRefund: (tournamentId: String, refundItemId: String) -> Unit = { _, _ -> },
    onUpdateBattleRoyaleSchedule: (tournamentId: String, newScheduledTime: String) -> Unit = { _, _ -> },
    onRequestAdminUPIWithdrawal: (amount: Int, upiId: String, autoSuccess: Boolean) -> Unit = { _, _, _ -> },
    onSaveOwnerVerifiedUpiId: (newUpiId: String) -> Unit = {},
    onFailAdminWithdrawal: (withdrawalId: String, reason: String) -> Unit = { _, _ -> },
    onReverseAdminWithdrawal: (withdrawalId: String, reason: String) -> Unit = { _, _ -> },
    onUpdateBrPrizeChart: (tournamentId: String, newTiers: List<BrPrizeTier>, killPrize: Int) -> Unit = { _, _, _ -> },
    onVerifyMatchResult: (resultId: String, verifiedRank: Int?, verifiedKills: Int?, isApproved: Boolean) -> Unit = { _, _, _, _ -> },
    onApprovePrizePayment: (resultId: String) -> Unit = {},
    onUpdateTournamentSettings: (tournamentId: String, scheduledTime: String?, entryFee: Int?, totalSlots: Int?, mapName: String?) -> Unit = { _, _, _, _, _ -> },
    onVerifyRoomPlayer: (tournamentId: String, freeFireUid: String, status: PlayerVerificationStatus, notes: String?) -> Unit = { _, _, _, _ -> },
    onRemoveUnauthorizedPlayer: (tournamentId: String, freeFireUid: String, reason: String, shouldRefund: Boolean) -> Unit = { _, _, _, _ -> },
    onAuditRoomRoster: (tournamentId: String, roomPlayerUids: List<String>) -> RoomRosterAuditReport = { _, _ -> RoomRosterAuditReport("", 0, 0, emptyList(), emptyList()) },
    onCreateScheduledMatch: (titleEn: String, titleBn: String, mode: GameMode, date: String, time: String, slots: Int, fee: Int, descEn: String, descBn: String, map: String) -> Unit = { _, _, _, _, _, _, _, _, _, _ -> },
    onGenerateBatchSchedule: (mode: GameMode, date: String, times: List<String>, fee: Int) -> Unit = { _, _, _, _ -> },
    onCreateManualMatch: (titleEn: String, titleBn: String, mode: GameMode, slots: Int, fee: Int, descEn: String, descBn: String, isRegOpen: Boolean, map: String) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onEditMatchSchedule: (tournamentId: String, newDate: String?, newTimeIst: String?, newSlots: Int?, newTitleEn: String?) -> Unit = { _, _, _, _, _ -> },
    onPublishRoomDetails: (tournamentId: String, roomId: String, roomPass: String, notes: String?) -> Unit = { _, _, _, _ -> },
    onHideRoomDetails: (tournamentId: String) -> Unit = {},
    onCloseRoomAccess: (tournamentId: String) -> Unit = {},
    onToggleMatchRegistration: (tournamentId: String, isOpen: Boolean) -> Unit = { _, _ -> },
    onUpdateMatchStatus: (tournamentId: String, newStatus: TournamentStatus) -> Unit = { _, _ -> },
    onReviewCheatReport: (reportId: String, newStatus: IncidentReportStatus, adminNotes: String, actionType: String?, suspensionHours: Int) -> Unit = { _, _, _, _, _ -> },
    onReviewPlayerAppeal: (reportId: String, acceptAppeal: Boolean, reviewNotes: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val totalCommission = remember(commissions) { commissions.sumOf { it.commissionEarned } }
    val pendingWithdrawals = remember(withdrawals) { withdrawals.filter { it.status == WithdrawalStatus.PENDING } }
    val pendingAddMoney = remember(addMoneyRequests) { addMoneyRequests.filter { it.status == PaymentApprovalStatus.PENDING } }
    val pendingRefunds = remember(refundRecords) { refundRecords.filter { it.status == RefundStatus.PENDING } }
    val pendingResultsCount = remember(matchResults) {
        matchResults.count { it.paymentStatus == PrizePaymentStatus.PENDING_VERIFICATION || it.paymentStatus == PrizePaymentStatus.APPROVED }
    }

    var selectedAdminTab by remember { mutableStateOf(0) }
    var editRoomTournament by remember { mutableStateOf<Tournament?>(null) }
    var manageCustomRoomTarget by remember { mutableStateOf<Tournament?>(null) }
    var showCreateScheduledDialog by remember { mutableStateOf(false) }
    var showCreateManualDialog by remember { mutableStateOf(false) }

    // Scheduled Matches Filter & Sort state
    var scheduledSearchQuery by remember { mutableStateOf("") }
    var scheduledModeFilter by remember { mutableStateOf<GameMode?>(null) }
    var scheduledStatusFilter by remember { mutableStateOf<TournamentStatus?>(null) }
    var scheduledSort by remember { mutableStateOf("TIME") }

    // Manual Matches Filter & Sort state
    var manualSearchQuery by remember { mutableStateOf("") }
    var manualModeFilter by remember { mutableStateOf<GameMode?>(null) }
    var manualStatusFilter by remember { mutableStateOf<TournamentStatus?>(null) }
    var manualSort by remember { mutableStateOf("DATE") }

    var rejectDepositTarget by remember { mutableStateOf<AddMoneyRequest?>(null) }
    var rejectDepositReason by remember { mutableStateOf("UTR not received in owner bank account") }

    var banTargetUserId by remember { mutableStateOf("") }
    var banReasonInput by remember { mutableStateOf("Fair Play / Third-party script detection") }
    var playerSearchQuery by remember { mutableStateOf("") }
    var playerStatusFilter by remember { mutableStateOf("ALL") }
    var selectedActionStatus by remember { mutableStateOf(AccountStatus.TEMPORARILY_SUSPENDED) }
    var suspensionHoursInput by remember { mutableStateOf("24") }
    var showPermanentBanConfirmDialog by remember { mutableStateOf(false) }
    var permanentBanTargetUser by remember { mutableStateOf<User?>(null) }

    // Anti-Cheat Investigation & Review state
    var cheatReportFilter by remember { mutableStateOf("ALL") }
    var reviewReportTarget by remember { mutableStateOf<IncidentReport?>(null) }
    var reviewDecisionStatus by remember { mutableStateOf(IncidentReportStatus.UNDER_REVIEW) }
    var reviewAdminNotesInput by remember { mutableStateOf("") }
    var reviewPenaltyAction by remember { mutableStateOf<String?>(null) }
    var reviewSuspensionHoursInput by remember { mutableStateOf("24") }
    var reviewAppealTarget by remember { mutableStateOf<IncidentReport?>(null) }
    var reviewAppealNotesInput by remember { mutableStateOf("") }

    var markUnsuccessfulTarget by remember { mutableStateOf<Tournament?>(null) }
    var markUnsuccessfulReason by remember { mutableStateOf("Custom room server glitch / match canceled") }

    var editScheduleTarget by remember { mutableStateOf<Tournament?>(null) }
    var editScheduleInput by remember { mutableStateOf("08:00 PM IST") }
    var editScheduleDateInput by remember { mutableStateOf("Today") }
    var editScheduleSlotsInput by remember { mutableStateOf("40") }
    var editScheduleTitleInput by remember { mutableStateOf("") }

    var showAdminWithdrawDialog by remember { mutableStateOf(false) }
    var showEditOwnerUpiDialog by remember { mutableStateOf(false) }
    var adminWithdrawAmountInput by remember { mutableStateOf("100") }
    var editOwnerUpiInput by remember { mutableStateOf(adminWallet.verifiedUpiId) }
    var adminWithdrawalFilter by remember { mutableStateOf("ALL") }
    var showConfirmWithdrawalStep by remember { mutableStateOf(false) }
    var withdrawErrorMessage by remember { mutableStateOf<String?>(null) }

    // Match Results state
    var resultFilter by remember { mutableStateOf("ALL") }
    var editResultTarget by remember { mutableStateOf<MatchResultRecord?>(null) }
    var editRankInput by remember { mutableStateOf("1") }
    var editKillsInput by remember { mutableStateOf("0") }
    var editApproveInput by remember { mutableStateOf(true) }

    // BR Prize Chart & Schedule state
    val brTournament = remember(tournaments) { tournaments.find { it.mode == GameMode.BATTLE_ROYALE } ?: tournaments.firstOrNull() }
    var selectedBrTourneyId by remember(brTournament) { mutableStateOf(brTournament?.id ?: "T-BR-301") }
    var rank1PrizeInput by remember(brTournament) { mutableStateOf(brTournament?.brPrizeChart?.find { it.startRank == 1 }?.prizePerPlayer?.toString() ?: "100") }
    var rank2PrizeInput by remember(brTournament) { mutableStateOf(brTournament?.brPrizeChart?.find { it.startRank == 2 }?.prizePerPlayer?.toString() ?: "70") }
    var rank3to4PrizeInput by remember(brTournament) { mutableStateOf(brTournament?.brPrizeChart?.find { it.startRank == 3 }?.prizePerPlayer?.toString() ?: "50") }
    var rank5PrizeInput by remember(brTournament) { mutableStateOf(brTournament?.brPrizeChart?.find { it.startRank == 5 }?.prizePerPlayer?.toString() ?: "40") }
    var rank6to10PrizeInput by remember(brTournament) { mutableStateOf(brTournament?.brPrizeChart?.find { it.startRank == 6 }?.prizePerPlayer?.toString() ?: "30") }
    var killPoolPrizeInput by remember(brTournament) { mutableStateOf(brTournament?.brKillPrizePool?.toString() ?: "110") }

    var brScheduleTimeInput by remember(brTournament) { mutableStateOf(brTournament?.scheduledDailyTime ?: "08:00 PM IST") }
    var brEntryFeeInput by remember(brTournament) { mutableStateOf(brTournament?.entryFee?.toString() ?: "20") }
    var brSlotsInput by remember(brTournament) { mutableStateOf(brTournament?.totalSlots?.toString() ?: "40") }
    var brMapInput by remember(brTournament) { mutableStateOf(brTournament?.mapName ?: "Bermuda") }
    var chartSaveSuccessMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Player Withdrawal Management state
    var playerWithdrawalFilter by remember { mutableStateOf("ALL") }
    var markPaidTarget by remember { mutableStateOf<WithdrawalRequest?>(null) }
    var paymentUtrInput by remember { mutableStateOf("") }
    var confirmPaymentChecked by remember { mutableStateOf(false) }
    var rejectWithdrawalTarget by remember { mutableStateOf<WithdrawalRequest?>(null) }
    var rejectWithdrawalReasonInput by remember { mutableStateOf("Invalid UPI address") }

    // Authorized Players & Anti-Free Entry state
    var authorizedPlayersTarget by remember { mutableStateOf<Tournament?>(null) }
    var roomUidsAuditInput by remember { mutableStateOf("") }
    var currentAuditReport by remember { mutableStateOf<RoomRosterAuditReport?>(null) }
    var kickReasonInput by remember { mutableStateOf("Unauthorized room entry / unapproved UID") }
    var kickTargetParticipant by remember { mutableStateOf<PlayerParticipant?>(null) }
    var kickWithRefundChecked by remember { mutableStateOf(false) }

    val filteredPlayerWithdrawals = remember(withdrawals, playerWithdrawalFilter) {
        when (playerWithdrawalFilter) {
            "PENDING" -> withdrawals.filter { it.status == WithdrawalStatus.PENDING }
            "APPROVED" -> withdrawals.filter { it.status == WithdrawalStatus.APPROVED }
            "PROCESSING" -> withdrawals.filter { it.status == WithdrawalStatus.PROCESSING }
            "PAID" -> withdrawals.filter { it.status == WithdrawalStatus.PAID }
            "REJECTED" -> withdrawals.filter { it.status == WithdrawalStatus.REJECTED }
            "FAILED" -> withdrawals.filter { it.status == WithdrawalStatus.FAILED }
            else -> withdrawals
        }
    }

    val filteredPlayers = remember(allPlayers, playerSearchQuery, playerStatusFilter) {
        allPlayers.filter { player ->
            val query = playerSearchQuery.trim().lowercase()
            val matchesQuery = query.isBlank() ||
                player.playerId.lowercase().contains(query) ||
                player.id.lowercase().contains(query) ||
                player.freeFireUid.lowercase().contains(query) ||
                player.name.lowercase().contains(query) ||
                player.freeFireName.lowercase().contains(query) ||
                player.email.lowercase().contains(query)

            val matchesStatus = when (playerStatusFilter) {
                "ACTIVE" -> player.effectiveAccountStatus == AccountStatus.ACTIVE
                "SUSPENDED" -> player.effectiveAccountStatus == AccountStatus.TEMPORARILY_SUSPENDED
                "BANNED" -> player.effectiveAccountStatus == AccountStatus.PERMANENTLY_BANNED
                else -> true
            }

            matchesQuery && matchesStatus
        }
    }

    val sdf = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())

    val adminTabs = listOf(
        "Wallet & UPI",
        "Results & Payouts ($pendingResultsCount)",
        "BR Prize Chart",
        "Scheduled Matches",
        "Manual Matches",
        "Push (FCM)",
        "Refunds (${pendingRefunds.size})",
        "Deposits (${pendingAddMoney.size})",
        "Withdrawals (${pendingWithdrawals.size})",
        "Player Management"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .testTag("admin_screen"),
        contentPadding = PaddingValues(14.dp, 14.dp, 14.dp, 100.dp)
    ) {
        // Admin Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GamingPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = GamingPrimary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppStrings.adminControlCenter(language),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isOwner) "Authorized: $BATTLE_ARENA_OWNER_EMAIL" else "Restricted View",
                            fontSize = 11.sp,
                            color = if (isOwner) GamingGreen else GamingGold
                        )
                    }
                }

                Button(
                    onClick = onCreateTournamentClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_create_tournament_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = AppStrings.createMatch(language), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Stats Dashboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GamingSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "প্ল্যাটফর্ম রাজস্ব ও পরিসংখ্যান" else "Platform Financials & Revenue",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatPill(
                            label = if (language == AppLanguage.BENGALI) "ওয়ালেট ব্যালেন্স" else "Available Balance",
                            value = "₹${adminWallet.availableBalance}",
                            color = GamingGold,
                            modifier = Modifier.weight(1.2f)
                        )
                        AdminStatPill(
                            label = if (language == AppLanguage.BENGALI) "ম্যাচ রাজস্ব" else "Host Earnings",
                            value = "₹${adminWallet.totalMatchEarnings}",
                            color = GamingGreen,
                            modifier = Modifier.weight(1.1f)
                        )
                        AdminStatPill(
                            label = if (language == AppLanguage.BENGALI) "ডিপোজিট" else "Deposits",
                            value = "${pendingAddMoney.size}",
                            color = if (pendingAddMoney.isNotEmpty()) GamingGold else GamingGreen,
                            modifier = Modifier.weight(0.9f)
                        )
                        AdminStatPill(
                            label = if (language == AppLanguage.BENGALI) "রিফান্ড" else "Refunds",
                            value = "${pendingRefunds.size}",
                            color = if (pendingRefunds.isNotEmpty()) GamingRed else TextSecondary,
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (!isOwner) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .testTag("owner_access_denied_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GamingRed)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GamingRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Restricted", tint = GamingRed, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "অননুমোদিত: শুধুমাত্র প্ল্যাটফর্ম মালিকের জন্য" else "Restricted: Owner Access Only",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "ব্যাটেল এরিনা অ্যাডমিন প্যানেল, অ্যাডমিন ওয়ালেট ও ইউপিআই পেআউট শুধুমাত্র প্ল্যাটফর্মের যাচাইকৃত মালিক ($BATTLE_ARENA_OWNER_EMAIL) দ্বারা নিয়ন্ত্রিত। সাধারণ প্লেয়ার বা গেস্ট ইউজার এটি অ্যাক্সেস করতে পারবেন না।"
                            else
                                "The Battle Arena Admin Panel, Admin Wallet, Commission Ledger, and UPI Payouts are strictly reserved for the verified platform Owner ($BATTLE_ARENA_OWNER_EMAIL). Permissions are verified on the server.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Tab Selector Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingSurface)
                        .horizontalScroll(rememberScrollState())
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    adminTabs.forEachIndexed { index, title ->
                        val isSelected = selectedAdminTab == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingPrimary else Color.Transparent)
                                .clickable { selectedAdminTab = index }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // ==========================================
            // TAB 0: ADMIN WALLET & UPI WITHDRAWALS
            // ==========================================
            if (selectedAdminTab == 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "অ্যাডমিন ওয়ালেট ও কমিশন রাজস্ব" else "Admin Wallet & Platform Revenue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI) "মালিকের নিজস্ব ব্যাংক অ্যাকাউন্টে তাৎক্ষণিক উইথড্র" else "Instant host payout to owner verified UPI VPA",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingGreen.copy(alpha = 0.15f))
                                .border(1.dp, GamingGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = GamingGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Owner Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingGreen)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Wallet Balance Cards
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GamingGold.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "উইথড্রয়ালযোগ্য উপলব্ধ ব্যালেন্স" else "Available Balance",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "₹${adminWallet.availableBalance}",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GamingGold
                                        )
                                        if (adminWallet.reservedBalance > 0) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "(+₹${adminWallet.reservedBalance} reserved in transit)",
                                                fontSize = 11.sp,
                                                color = GamingGold.copy(alpha = 0.8f),
                                                modifier = Modifier.padding(bottom = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        adminWithdrawAmountInput = if (adminWallet.availableBalance > 0) minOf(100, adminWallet.availableBalance).toString() else "0"
                                        showConfirmWithdrawalStep = false
                                        withdrawErrorMessage = null
                                        showAdminWithdrawDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = isOwner && adminWallet.availableBalance > 0,
                                    modifier = Modifier.testTag("admin_withdraw_upi_btn")
                                ) {
                                    Icon(Icons.Default.Paid, contentDescription = "Withdraw", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "উইথড্র করুন" else "Withdraw to UPI",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = GamingBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Financial Breakdown Metrics Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminStatPill(
                                    label = if (language == AppLanguage.BENGALI) "মোট ম্যাচ উপার্জন" else "Total Earnings",
                                    value = "₹${adminWallet.totalMatchEarnings}",
                                    color = GamingGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatPill(
                                    label = if (language == AppLanguage.BENGALI) "মোট উইথড্র পরিশোধ" else "Total Payouts",
                                    value = "₹${adminWallet.totalWithdrawalsPaid}",
                                    color = GamingGold,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatPill(
                                    label = if (language == AppLanguage.BENGALI) "খেলোয়াড় পুরস্কার" else "Prizes Paid",
                                    value = "₹${adminWallet.totalPrizePaid}",
                                    color = TextSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminStatPill(
                                    label = if (language == AppLanguage.BENGALI) "প্লেয়ার রিফান্ড" else "Refunds Paid",
                                    value = "₹${adminWallet.totalRefundsPaid}",
                                    color = GamingRed,
                                    modifier = Modifier.weight(1f)
                                )
                                val pendingPayoutsCount = adminWithdrawals.count { it.status == AdminWithdrawalStatus.PENDING || it.status == AdminWithdrawalStatus.PROCESSING }
                                AdminStatPill(
                                    label = if (language == AppLanguage.BENGALI) "চলমান উইথড্র" else "Pending Payouts",
                                    value = "$pendingPayoutsCount",
                                    color = if (pendingPayoutsCount > 0) GamingGold else TextSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                val failedCount = adminWithdrawals.count { it.status == AdminWithdrawalStatus.FAILED }
                                AdminStatPill(
                                    label = if (language == AppLanguage.BENGALI) "ব্যর্থ উইথড্র" else "Failed Payouts",
                                    value = "$failedCount",
                                    color = if (failedCount > 0) GamingRed else TextSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Owner Verified UPI Destination Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(GamingPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GamingPrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "মালিকের নিবন্ধিত UPI আইডি" else "Registered Owner UPI ID",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = adminWallet.verifiedUpiId,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Automated Instant IMPS • Zero Deduction",
                                        fontSize = 10.sp,
                                        color = GamingGreen
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    editOwnerUpiInput = adminWallet.verifiedUpiId
                                    showEditOwnerUpiDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingPrimary),
                                modifier = Modifier.testTag("admin_edit_upi_btn")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Withdrawal History & Reconciliation
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "অ্যাডমিন উইথড্রাল হিস্ট্রি (${adminWithdrawals.size})" else "Admin Payouts History (${adminWithdrawals.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Status Filters
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("ALL", "SUCCESS", "PROCESSING", "FAILED", "REVERSED").forEach { filter ->
                                val isSel = adminWithdrawalFilter == filter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) GamingPrimary else GamingSurfaceVariant)
                                        .clickable { adminWithdrawalFilter = filter }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = filter,
                                        fontSize = 9.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val filteredAdminWithdrawals = if (adminWithdrawalFilter == "ALL") {
                    adminWithdrawals
                } else {
                    adminWithdrawals.filter { it.status.name == adminWithdrawalFilter }
                }

                if (filteredAdminWithdrawals.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurface)
                        ) {
                            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "কোনো উইথড্রাল রেকর্ড নেই" else "No admin withdrawal records found",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    items(filteredAdminWithdrawals, key = { it.id }) { rec ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "₹${rec.amount}",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val (badgeBg, badgeFg, label) = when (rec.status) {
                                            AdminWithdrawalStatus.SUCCESS -> Triple(GamingGreen.copy(alpha = 0.15f), GamingGreen, "SUCCESS")
                                            AdminWithdrawalStatus.PROCESSING -> Triple(GamingGold.copy(alpha = 0.15f), GamingGold, "PROCESSING")
                                            AdminWithdrawalStatus.PENDING -> Triple(GamingGold.copy(alpha = 0.15f), GamingGold, "PENDING")
                                            AdminWithdrawalStatus.FAILED -> Triple(GamingRed.copy(alpha = 0.15f), GamingRed, "FAILED")
                                            AdminWithdrawalStatus.REVERSED -> Triple(GamingSecondary.copy(alpha = 0.15f), GamingSecondary, "REVERSED")
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(badgeBg)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeFg)
                                        }
                                    }

                                    Text(
                                        text = sdf.format(Date(rec.requestedAt)),
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "To: ${rec.destinationUpiId} • ID: ${rec.id}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )

                                if (rec.payoutUtr.isNotEmpty()) {
                                    Text(
                                        text = "Payout Bank UTR: ${rec.payoutUtr}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GamingGreen
                                    )
                                }
                                if (rec.providerReference.isNotEmpty()) {
                                    Text(
                                        text = "Gateway Ref: ${rec.providerReference}",
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                                if (!rec.failureReason.isNullOrBlank()) {
                                    Text(
                                        text = "Reason: ${rec.failureReason}",
                                        fontSize = 10.sp,
                                        color = GamingRed
                                    )
                                }

                                if (rec.status == AdminWithdrawalStatus.PROCESSING || rec.status == AdminWithdrawalStatus.PENDING) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                onRequestAdminUPIWithdrawal(rec.amount, rec.destinationUpiId, true)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Re-try Payout", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                onFailAdminWithdrawal(rec.id, "Payment gateway bank timeout")
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingRed),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Simulate Fail", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else if (rec.status == AdminWithdrawalStatus.SUCCESS) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                onReverseAdminWithdrawal(rec.id, "Bank settlement reversal / test dispute")
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingSecondary)
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Process Reversal", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(14.dp)) }
                }

                // Ledger Entries Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "প্ল্যাটফর্ম অ্যাকাউন্টিং লেজার (${adminLedger.size})" else "Platform Ledger (${adminLedger.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Double-Entry Audit",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (adminLedger.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurface)
                        ) {
                            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "কোনো লেজার এন্ট্রি নেই" else "No ledger entries recorded yet",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(adminLedger, key = { it.id }) { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    val isCredit = entry.type == AdminLedgerEntryType.MATCH_COMMISSION || entry.type == AdminLedgerEntryType.WITHDRAWAL_REVERSAL
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(if (isCredit) GamingGreen.copy(alpha = 0.15f) else GamingRed.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = if (isCredit) GamingGreen else GamingRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (language == AppLanguage.BENGALI) entry.descriptionBn else entry.descriptionEn,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${sdf.format(Date(entry.timestamp))} • Ref: ${entry.referenceId}",
                                            fontSize = 9.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                val isCredit = entry.type == AdminLedgerEntryType.MATCH_COMMISSION || entry.type == AdminLedgerEntryType.WITHDRAWAL_REVERSAL
                                Text(
                                    text = (if (isCredit) "+₹" else "-₹") + entry.amount,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCredit) GamingGreen else GamingRed
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // TAB 1: MATCH RESULTS & PRIZE PAYMENT APPROVALS
            // ==========================================
            if (selectedAdminTab == 1) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ম্যাচ ফলাফল যাচাই ও প্রাইজ পেমেন্ট অনুমোদন" else "Match Results & Prize Payment Approval",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI) "অনুমোদনের সাথে সাথে পুরস্কার সরাসরি খেলোয়াড়ের ওয়ালেটে জমা হয়" else "Owner authorizes prize transfers directly to winner wallets",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Summary Stats
                item {
                    val approvedCount = matchResults.count { it.paymentStatus == PrizePaymentStatus.APPROVED }
                    val paidCount = matchResults.count { it.paymentStatus == PrizePaymentStatus.PAID }
                    val pendingVerifyCount = matchResults.count { it.paymentStatus == PrizePaymentStatus.PENDING_VERIFICATION }
                    val totalPaidPrize = matchResults.filter { it.paymentStatus == PrizePaymentStatus.PAID }.sumOf { it.eligiblePrize }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminStatPill(
                                    label = "Pending Verify",
                                    value = "$pendingVerifyCount",
                                    color = if (pendingVerifyCount > 0) GamingGold else TextSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatPill(
                                    label = "Approved (Ready)",
                                    value = "$approvedCount",
                                    color = if (approvedCount > 0) GamingPrimary else TextSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatPill(
                                    label = "Total Paid",
                                    value = "₹$totalPaidPrize ($paidCount)",
                                    color = GamingGreen,
                                    modifier = Modifier.weight(1.2f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "PENDING_VERIFICATION", "APPROVED", "PAID", "REJECTED").forEach { filter ->
                            val label = when (filter) {
                                "ALL" -> "All (${matchResults.size})"
                                "PENDING_VERIFICATION" -> "Pending (${matchResults.count { it.paymentStatus == PrizePaymentStatus.PENDING_VERIFICATION }})"
                                "APPROVED" -> "Approved (${matchResults.count { it.paymentStatus == PrizePaymentStatus.APPROVED }})"
                                "PAID" -> "Paid (${matchResults.count { it.paymentStatus == PrizePaymentStatus.PAID }})"
                                "REJECTED" -> "Rejected (${matchResults.count { it.paymentStatus == PrizePaymentStatus.REJECTED }})"
                                else -> filter
                            }
                            val isSelected = resultFilter == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
                                    .clickable { resultFilter = filter }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val filteredResults = if (resultFilter == "ALL") {
                    matchResults
                } else {
                    matchResults.filter { it.paymentStatus.name == resultFilter }
                }

                if (filteredResults.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurface)
                        ) {
                            Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No match results found for selected filter.",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(filteredResults, key = { it.id }) { res ->
                        val isPaid = res.paymentStatus == PrizePaymentStatus.PAID
                        val isRejected = res.paymentStatus == PrizePaymentStatus.REJECTED
                        val isApproved = res.paymentStatus == PrizePaymentStatus.APPROVED

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurface),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isPaid) GamingGreen.copy(alpha = 0.5f)
                                else if (isApproved) GamingPrimary.copy(alpha = 0.5f)
                                else if (res.paymentStatus == PrizePaymentStatus.PENDING_VERIFICATION) GamingGold.copy(alpha = 0.5f)
                                else GamingBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (res.rank == 1) GamingGold.copy(alpha = 0.2f) else GamingSurfaceVariant)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (res.rank == 1) "🏆 Rank #1" else "Rank #${res.rank}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (res.rank == 1) GamingGold else TextPrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${res.playerName} • FF UID: ${res.freeFireUid}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${res.tournamentTitle} • ${res.kills} Kills",
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${res.eligiblePrize}",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GamingGold
                                        )
                                        Text(
                                            text = "Place: ₹${res.placementPrize} | Kill: ₹${res.killPrize}",
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Status Pills Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (res.isVerified) GamingGreen.copy(alpha = 0.15f) else GamingGold.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (res.isVerified) "✓ Verified" else "Pending Verification",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (res.isVerified) GamingGreen else GamingGold
                                            )
                                        }

                                        val statusColor = when (res.paymentStatus) {
                                            PrizePaymentStatus.PAID -> GamingGreen
                                            PrizePaymentStatus.APPROVED -> GamingPrimary
                                            PrizePaymentStatus.PROCESSING -> GamingSecondary
                                            PrizePaymentStatus.PENDING_VERIFICATION -> GamingGold
                                            PrizePaymentStatus.FAILED -> GamingRed
                                            PrizePaymentStatus.REJECTED -> TextMuted
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(statusColor.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = res.paymentStatus.name,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
                                            )
                                        }
                                    }

                                    if (isPaid && res.payoutUtr != null) {
                                        Text(
                                            text = "UTR: ${res.payoutUtr}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GamingGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            editResultTarget = res
                                            editRankInput = res.rank.toString()
                                            editKillsInput = res.kills.toString()
                                            editApproveInput = res.paymentStatus != PrizePaymentStatus.REJECTED
                                        },
                                        enabled = !isPaid,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit / Verify", fontSize = 11.sp)
                                    }

                                    if (isPaid) {
                                        Button(
                                            onClick = {},
                                            enabled = false,
                                            colors = ButtonDefaults.buttonColors(
                                                disabledContainerColor = GamingGreen.copy(alpha = 0.2f),
                                                disabledContentColor = GamingGreen
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1.3f)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = "Paid", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Paid to Wallet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { onApprovePrizePayment(res.id) },
                                            enabled = !isRejected && res.eligiblePrize > 0,
                                            colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1.3f)
                                        ) {
                                            Icon(Icons.Default.Paid, contentDescription = "Approve Payout", tint = Color.Black, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Approve Payout (₹${res.eligiblePrize})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // TAB 2: BATTLE ROYALE PRIZE CHART & DAILY SCHEDULE
            // ==========================================
            if (selectedAdminTab == 2) {
                item {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ব্যাটেল রয়্যাল প্রাইজ চার্ট ও দৈনিক সময়সূচী" else "Battle Royale Prize Distribution & Schedule",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Configure rank-wise prize money, total prize pool, and daily 8:00 PM IST match parameters.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Live Pool Calculator Card
                item {
                    val r1 = rank1PrizeInput.toIntOrNull() ?: 100
                    val r2 = rank2PrizeInput.toIntOrNull() ?: 70
                    val r34 = rank3to4PrizeInput.toIntOrNull() ?: 50
                    val r5 = rank5PrizeInput.toIntOrNull() ?: 40
                    val r610 = rank6to10PrizeInput.toIntOrNull() ?: 30
                    val killPool = killPoolPrizeInput.toIntOrNull() ?: 110

                    val placementSum = (r1 * 1) + (r2 * 1) + (r34 * 2) + (r5 * 1) + (r610 * 5)
                    val totalPool = placementSum + killPool
                    val fee = brEntryFeeInput.toIntOrNull() ?: 20
                    val slots = brSlotsInput.toIntOrNull() ?: 40
                    val totalCollection = fee * slots
                    val commission = totalCollection - totalPool

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingGold)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = GamingGold, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Prize Pool Live Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GamingGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "10 Winning Positions", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingGold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminStatPill(
                                    label = "Total Pool",
                                    value = "₹$totalPool",
                                    color = GamingGold,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatPill(
                                    label = "Placement (10P)",
                                    value = "₹$placementSum",
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatPill(
                                    label = "Kill Pool",
                                    value = "₹$killPool",
                                    color = GamingSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatPill(
                                    label = "Commission",
                                    value = "₹${maxOf(0, commission)}",
                                    color = GamingGreen,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Rank Prize Configuration Editor
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Rank-wise Placement Prizes (₹)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Rank 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Rank 1 (Booyah Champion)", fontSize = 12.sp, color = GamingGold, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                OutlinedTextField(
                                    value = rank1PrizeInput,
                                    onValueChange = { rank1PrizeInput = it },
                                    singleLine = true,
                                    modifier = Modifier.width(110.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            // Rank 2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Rank 2", fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1.5f))
                                OutlinedTextField(
                                    value = rank2PrizeInput,
                                    onValueChange = { rank2PrizeInput = it },
                                    singleLine = true,
                                    modifier = Modifier.width(110.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            // Rank 3-4
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Rank 3 - 4 (each, x2 slots)", fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1.5f))
                                OutlinedTextField(
                                    value = rank3to4PrizeInput,
                                    onValueChange = { rank3to4PrizeInput = it },
                                    singleLine = true,
                                    modifier = Modifier.width(110.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            // Rank 5
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Rank 5", fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1.5f))
                                OutlinedTextField(
                                    value = rank5PrizeInput,
                                    onValueChange = { rank5PrizeInput = it },
                                    singleLine = true,
                                    modifier = Modifier.width(110.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            // Rank 6-10
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Rank 6 - 10 (each, x5 slots)", fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1.5f))
                                OutlinedTextField(
                                    value = rank6to10PrizeInput,
                                    onValueChange = { rank6to10PrizeInput = it },
                                    singleLine = true,
                                    modifier = Modifier.width(110.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            // Kill Prize Pool
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Kill Prize Pool (Top 5 Fraggers)", fontSize = 12.sp, color = GamingSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                OutlinedTextField(
                                    value = killPoolPrizeInput,
                                    onValueChange = { killPoolPrizeInput = it },
                                    singleLine = true,
                                    modifier = Modifier.width(110.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val r1 = rank1PrizeInput.toIntOrNull() ?: 100
                                    val r2 = rank2PrizeInput.toIntOrNull() ?: 70
                                    val r34 = rank3to4PrizeInput.toIntOrNull() ?: 50
                                    val r5 = rank5PrizeInput.toIntOrNull() ?: 40
                                    val r610 = rank6to10PrizeInput.toIntOrNull() ?: 30
                                    val killPool = killPoolPrizeInput.toIntOrNull() ?: 110

                                    val updatedTiers = listOf(
                                        BrPrizeTier("Rank 1 (Booyah Champion)", 1, 1, r1),
                                        BrPrizeTier("Rank 2", 2, 2, r2),
                                        BrPrizeTier("Rank 3 - 4", 3, 4, r34),
                                        BrPrizeTier("Rank 5", 5, 5, r5),
                                        BrPrizeTier("Rank 6 - 10", 6, 10, r610)
                                    )
                                    onUpdateBrPrizeChart(selectedBrTourneyId, updatedTiers, killPool)
                                    chartSaveSuccessMsg = "Prize distribution chart updated successfully!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = "Save", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save & Update BR Prize Chart", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Daily Schedule & Match Parameters Editor
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Daily 8:00 PM Match Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = brScheduleTimeInput,
                                onValueChange = { brScheduleTimeInput = it },
                                label = { Text("Scheduled Time") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = brEntryFeeInput,
                                    onValueChange = { brEntryFeeInput = it },
                                    label = { Text("Entry Fee (₹)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = brSlotsInput,
                                    onValueChange = { brSlotsInput = it },
                                    label = { Text("Total Slots") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = brMapInput,
                                onValueChange = { brMapInput = it },
                                label = { Text("Map Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val fee = brEntryFeeInput.toIntOrNull()
                                    val slots = brSlotsInput.toIntOrNull()
                                    onUpdateTournamentSettings(selectedBrTourneyId, brScheduleTimeInput, fee, slots, brMapInput)
                                    chartSaveSuccessMsg = "Schedule & Match settings updated!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Update Daily Match Settings", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            chartSaveSuccessMsg?.let { msg ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = msg, fontSize = 11.sp, color = GamingGreen)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // ==========================================
            // TAB 3: SCHEDULED MATCHES MANAGEMENT
            // ==========================================
            if (selectedAdminTab == 3) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "নির্ধারিত ম্যাচ পরিচালনা (IST সময়সূচী)" else "Scheduled Matches Management",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Indian Standard Time (IST) • Independent slots & room credentials",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Action buttons: Create Scheduled Match & Quick 5-Match Schedule
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showCreateScheduledDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_create_scheduled_match_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Create", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create Scheduled Match", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val todayStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                                onGenerateBatchSchedule(
                                    GameMode.BATTLE_ROYALE,
                                    todayStr,
                                    listOf("08:00 AM IST", "08:30 AM IST", "09:00 AM IST", "09:30 AM IST", "10:00 AM IST"),
                                    20
                                )
                                Toast.makeText(context, "Batch 5-Match Schedule created (08:00 AM - 10:00 AM IST)!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_batch_schedule_btn")
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = "Batch", tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("5-Match Quick Schedule", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = scheduledSearchQuery,
                        onValueChange = { scheduledSearchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_scheduled_search_input"),
                        label = { Text("Search by Match ID, Title or Map") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (scheduledSearchQuery.isNotBlank()) {
                                IconButton(onClick = { scheduledSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Mode Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val modes = listOf<GameMode?>(null, GameMode.BATTLE_ROYALE, GameMode.CLASH_SQUAD, GameMode.LONE_WOLF)
                        modes.forEach { mode ->
                            val isSelected = scheduledModeFilter == mode
                            val label = mode?.tagEn ?: "All Modes"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
                                    .border(1.dp, if (isSelected) GamingPrimary else GamingBorder, RoundedCornerShape(8.dp))
                                    .clickable { scheduledModeFilter = mode }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Status Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val statuses = listOf<TournamentStatus?>(
                            null,
                            TournamentStatus.UPCOMING,
                            TournamentStatus.CONFIRMED,
                            TournamentStatus.LIVE,
                            TournamentStatus.COMPLETED,
                            TournamentStatus.CANCELLED
                        )
                        statuses.forEach { st ->
                            val isSelected = scheduledStatusFilter == st
                            val label = st?.name ?: "All Statuses"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GamingSecondary else GamingSurfaceVariant)
                                    .border(1.dp, if (isSelected) GamingSecondary else GamingBorder, RoundedCornerShape(8.dp))
                                    .clickable { scheduledStatusFilter = st }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else TextSecondary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Filtered Scheduled Matches List
                val filteredScheduled = tournaments.filter { t ->
                    t.creationType == MatchCreationType.SCHEDULED &&
                    (scheduledModeFilter == null || t.mode == scheduledModeFilter) &&
                    (scheduledStatusFilter == null || t.status == scheduledStatusFilter) &&
                    (scheduledSearchQuery.isBlank() ||
                        t.id.contains(scheduledSearchQuery, ignoreCase = true) ||
                        t.title.contains(scheduledSearchQuery, ignoreCase = true) ||
                        t.mapName.contains(scheduledSearchQuery, ignoreCase = true))
                }.sortedBy { item -> item.matchTimeIst }

                if (filteredScheduled.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurface)
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No scheduled matches found matching filters.", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                    }
                } else {
                    items(filteredScheduled, key = { it.id }) { t ->
                        AdminMatchCard(
                            tournament = t,
                            language = language,
                            isOwner = isOwner,
                            onManageRoom = { manageCustomRoomTarget = t },
                            onEditSchedule = {
                                editScheduleTarget = t
                                editScheduleInput = t.matchTimeIst
                                editScheduleDateInput = t.matchDate
                                editScheduleSlotsInput = t.totalSlots.toString()
                                editScheduleTitleInput = t.title
                            },
                            onToggleRegistration = { onToggleMatchRegistration(t.id, !t.isRegistrationOpen) },
                            onUpdateStatus = { newSt -> onUpdateMatchStatus(t.id, newSt) },
                            onCancelAndRefund = {
                                markUnsuccessfulTarget = t
                                markUnsuccessfulReason = "Match canceled by Admin with full entry fee refunds"
                            },
                            onVerifyTournament = { onVerifyTournamentClick(t) },
                            onAuditRoster = {
                                authorizedPlayersTarget = t
                                roomUidsAuditInput = ""
                                currentAuditReport = null
                            }
                        )
                    }
                }
            }

            // ==========================================
            // TAB 4: MANUAL MATCHES MANAGEMENT
            // ==========================================
            if (selectedAdminTab == 4) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ম্যানুয়াল / তাৎক্ষণিক ম্যাচ পরিচালনা" else "Manual Match Creation & Management",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "On-demand matches created at any time without predefined schedule slots",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Action Button: Create Manual Match
                item {
                    Button(
                        onClick = { showCreateManualDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_create_manual_match_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Manual On-Demand Match", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = manualSearchQuery,
                        onValueChange = { manualSearchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_manual_search_input"),
                        label = { Text("Search by Match ID, Title or Mode") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (manualSearchQuery.isNotBlank()) {
                                IconButton(onClick = { manualSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Mode Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val modes = listOf<GameMode?>(null, GameMode.BATTLE_ROYALE, GameMode.CLASH_SQUAD, GameMode.LONE_WOLF)
                        modes.forEach { mode ->
                            val isSelected = manualModeFilter == mode
                            val label = mode?.tagEn ?: "All Modes"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
                                    .border(1.dp, if (isSelected) GamingPrimary else GamingBorder, RoundedCornerShape(8.dp))
                                    .clickable { manualModeFilter = mode }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Filtered Manual Matches List
                val filteredManual = tournaments.filter { t ->
                    t.creationType == MatchCreationType.MANUAL &&
                    (manualModeFilter == null || t.mode == manualModeFilter) &&
                    (manualStatusFilter == null || t.status == manualStatusFilter) &&
                    (manualSearchQuery.isBlank() ||
                        t.id.contains(manualSearchQuery, ignoreCase = true) ||
                        t.title.contains(manualSearchQuery, ignoreCase = true) ||
                        t.mapName.contains(manualSearchQuery, ignoreCase = true))
                }

                if (filteredManual.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurface)
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No manual matches found matching filters.", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                    }
                } else {
                    items(filteredManual, key = { it.id }) { t ->
                        AdminMatchCard(
                            tournament = t,
                            language = language,
                            isOwner = isOwner,
                            onManageRoom = { manageCustomRoomTarget = t },
                            onEditSchedule = {
                                editScheduleTarget = t
                                editScheduleInput = t.matchTimeIst
                                editScheduleDateInput = t.matchDate
                                editScheduleSlotsInput = t.totalSlots.toString()
                                editScheduleTitleInput = t.title
                            },
                            onToggleRegistration = { onToggleMatchRegistration(t.id, !t.isRegistrationOpen) },
                            onUpdateStatus = { newSt -> onUpdateMatchStatus(t.id, newSt) },
                            onCancelAndRefund = {
                                markUnsuccessfulTarget = t
                                markUnsuccessfulReason = "Manual match canceled by Admin with full entry fee refunds"
                            },
                            onVerifyTournament = { onVerifyTournamentClick(t) },
                            onAuditRoster = {
                                authorizedPlayersTarget = t
                                roomUidsAuditInput = ""
                                currentAuditReport = null
                            }
                        )
                    }
                }
            }

        // ==========================================
        // TAB 5: FIREBASE CLOUD MESSAGING (FCM) PUSH NOTIFICATIONS
        // ==========================================
        if (selectedAdminTab == 5) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "FCM Push Notifications Control",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Send tournament reminders, match start alerts & prize payouts",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingPrimary.copy(alpha = 0.2f))
                            .border(1.dp, GamingPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("FCM READY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // FCM Configuration & Status Card
            item {
                val currentToken = remember { BattleArenaNotificationManager.getStoredToken(context) }
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("admin_fcm_status_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, contentDescription = "FCM", tint = GamingGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("FCM Service Status", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Text("Topic: all_players", fontSize = 11.sp, color = GamingGreen, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentToken.isNotBlank()) "Device FCM Token: ${currentToken.take(24)}..." else "FCM Token: Generated automatically on device connect",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Active Channels: Reminders, Match Starts, Prizes", fontSize = 10.sp, color = TextMuted)
                            if (currentToken.isNotBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("FCM Token", currentToken))
                                        Toast.makeText(context, "FCM Token copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Copy Token", fontSize = 10.sp, color = GamingSecondary)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 1. Send Tournament Registration Reminder
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("admin_card_send_reminder"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingPrimary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(GamingPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⏰", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("1. Tournament Registration Reminder", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Alerts players to join before match slots fill up", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val defaultTourney = tournaments.firstOrNull()
                        Button(
                            onClick = {
                                val tourney = defaultTourney
                                if (tourney != null) {
                                    BattleArenaNotificationManager.dispatchTournamentReminder(
                                        context = context,
                                        tournamentTitle = tourney.titleEn,
                                        tournamentId = tourney.id,
                                        startsIn = "15 mins",
                                        language = language
                                    )
                                    Toast.makeText(context, "FCM Push Dispatched: Registration Reminder", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "No tournament available", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_broadcast_reminder"),
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("📢 Broadcast Registration Reminder to All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 2. Send Match Start & Room Unlock Notification
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("admin_card_send_match_start"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingSecondary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(GamingSecondary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎮", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("2. Match Start Time & Room Alert", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Sends Room ID & Password to registered participants", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val activeTourney = tournaments.find { it.status == TournamentStatus.CONFIRMED || it.status == TournamentStatus.LIVE } ?: tournaments.firstOrNull()
                        Button(
                            onClick = {
                                val t = activeTourney
                                if (t != null) {
                                    BattleArenaNotificationManager.dispatchMatchStartAlert(
                                        context = context,
                                        tournamentTitle = t.titleEn,
                                        tournamentId = t.id,
                                        roomId = t.roomId ?: "928371",
                                        roomPassword = t.roomPassword ?: "7788",
                                        language = language
                                    )
                                    Toast.makeText(context, "FCM Push Dispatched: Match Starting & Room Details", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "No tournament available", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_broadcast_match_start"),
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSecondary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("🎮 Broadcast Match Start & Room Unlock", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 3. Send Prize Distribution Push Notification
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("admin_card_send_prize_alert"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(GamingGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🏆", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("3. Prize Distribution Alert", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Notifies winners when prize money is credited to wallet", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                BattleArenaNotificationManager.dispatchPrizeDistributionAlert(
                                    context = context,
                                    tournamentTitle = "Battle Royale 40-Player Grand Prix",
                                    prizeAmount = 400,
                                    rank = 1,
                                    language = language
                                )
                                Toast.makeText(context, "FCM Push Dispatched: Prize Won ₹400", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_broadcast_prize_alert"),
                            colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("🏆 Broadcast Prize Distribution Payout", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // ==========================================
        // TAB 6: ENTRY FEE REFUNDS (DIRECT WALLET CREDIT)
        // ==========================================
        if (selectedAdminTab == 6) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "অসফল ম্যাচের এন্ট্রি ফি রিফান্ড" else "Unsuccessful Match Refunds",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (language == AppLanguage.BENGALI) "অনুমোদনের সাথে সাথে খেলোয়াড়ের ওয়ালেটে সরাসরি জমা হয়" else "Direct wallet refund for canceled / unsuccessful matches",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "${pendingRefunds.size} Pending",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (pendingRefunds.isNotEmpty()) GamingGold else GamingGreen
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (refundRecords.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface)
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "কোনো অসফল ম্যাচের রিফান্ড রেকর্ড নেই" else "No match refund records found",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(refundRecords, key = { it.id }) { item ->
                    val isPending = item.status == RefundStatus.PENDING
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPending) GamingGold.copy(alpha = 0.6f) else GamingBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.userName} • UID: ${item.freeFireUid}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Match #${item.tournamentId} • Reason: ${item.reason}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${item.entryFee}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GamingGold
                                    )
                                    Text(
                                        text = item.status.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (item.status) {
                                            RefundStatus.REFUNDED -> GamingGreen
                                            RefundStatus.PENDING -> GamingGold
                                            RefundStatus.APPROVED -> GamingSecondary
                                            RefundStatus.REJECTED -> GamingRed
                                        }
                                    )
                                }
                            }

                            if (isPending) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { onApproveMatchRefund(item.tournamentId, item.id) },
                                    enabled = isOwner,
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_approve_refund_${item.id}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Approve", tint = Color.Black, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isOwner) "Approve & Credit ₹${item.entryFee} to Wallet" else "Owner Auth Required",
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (item.status == RefundStatus.REFUNDED) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "✓ Entry fee ₹${item.entryFee} credited directly to player's wallet balance",
                                    fontSize = 11.sp,
                                    color = GamingGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 7: ADD MONEY MANUAL UTR APPROVALS
        // ==========================================
        if (selectedAdminTab == 7) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ডিপোজিট UTR অনুমোদন (মালিক কর্তৃক)" else "Deposit UTR Verifications (Owner Only)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${pendingAddMoney.size} Pending",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (pendingAddMoney.isNotEmpty()) GamingGold else GamingGreen
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (addMoneyRequests.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface)
                    ) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(text = "No deposit requests submitted yet.", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            } else {
                items(addMoneyRequests, key = { it.id }) { req ->
                    val isPending = req.status == PaymentApprovalStatus.PENDING
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPending) GamingGold.copy(alpha = 0.5f) else GamingBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "${req.userName} • FF UID: ${req.freeFireUid}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "UTR: ${req.utrNumber} (${req.paymentMethod})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GamingGold
                                    )
                                }
                                Text(
                                    text = "+₹${req.amount}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GamingGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Submitted: ${sdf.format(Date(req.requestedAt))} • Proof: ${req.screenshotNote}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (req.status) {
                                                PaymentApprovalStatus.APPROVED -> GamingGreen.copy(alpha = 0.2f)
                                                PaymentApprovalStatus.REJECTED -> GamingRed.copy(alpha = 0.2f)
                                                PaymentApprovalStatus.PENDING -> GamingGold.copy(alpha = 0.2f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = req.status.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (req.status) {
                                            PaymentApprovalStatus.APPROVED -> GamingGreen
                                            PaymentApprovalStatus.REJECTED -> GamingRed
                                            PaymentApprovalStatus.PENDING -> GamingGold
                                        }
                                    )
                                }
                            }

                            if (isPending) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onApproveAddMoney(req.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.3f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Approve", tint = Color.Black, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Verify & Credit Wallet",
                                            fontSize = 11.sp,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = { rejectDepositTarget = req },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingRed.copy(alpha = 0.2f)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Reject", tint = GamingRed, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Reject", fontSize = 11.sp, color = GamingRed, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 8: PLAYER WITHDRAWAL REQUESTS
        // ==========================================
        if (selectedAdminTab == 8) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "উইথড্রয়াল অনুরোধ পরিচালনা" else "Withdrawal Requests Management",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Manual Real Money Payouts via UPI • Virtual Wallet Ledger",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (pendingWithdrawals.isNotEmpty()) GamingGold.copy(alpha = 0.2f) else GamingGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${pendingWithdrawals.size} Pending Review",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pendingWithdrawals.isNotEmpty()) GamingGold else GamingGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Filter Chips Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", "PENDING", "APPROVED", "PROCESSING", "PAID", "REJECTED", "FAILED").forEach { filterKey ->
                        val isSelected = playerWithdrawalFilter == filterKey
                        val count = when (filterKey) {
                            "ALL" -> withdrawals.size
                            "PENDING" -> withdrawals.count { it.status == WithdrawalStatus.PENDING }
                            "APPROVED" -> withdrawals.count { it.status == WithdrawalStatus.APPROVED }
                            "PROCESSING" -> withdrawals.count { it.status == WithdrawalStatus.PROCESSING }
                            "PAID" -> withdrawals.count { it.status == WithdrawalStatus.PAID }
                            "REJECTED" -> withdrawals.count { it.status == WithdrawalStatus.REJECTED }
                            "FAILED" -> withdrawals.count { it.status == WithdrawalStatus.FAILED }
                            else -> 0
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
                                .border(1.dp, if (isSelected) GamingPrimary else GamingBorder, RoundedCornerShape(8.dp))
                                .clickable { playerWithdrawalFilter = filterKey }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$filterKey ($count)",
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (filteredPlayerWithdrawals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface)
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No withdrawal requests found under '$playerWithdrawalFilter' filter.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(filteredPlayerWithdrawals, key = { it.id }) { req ->
                    val statusColor = when (req.status) {
                        WithdrawalStatus.PAID -> GamingGreen
                        WithdrawalStatus.APPROVED -> GamingSecondary
                        WithdrawalStatus.PROCESSING -> Color(0xFF38BDF8)
                        WithdrawalStatus.PENDING -> GamingGold
                        WithdrawalStatus.REJECTED -> GamingRed
                        WithdrawalStatus.FAILED -> Color(0xFFF97316)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: ID, Date & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${req.id}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${sdf.format(Date(req.requestedAt))}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(statusColor.copy(alpha = 0.15f))
                                        .border(0.8.dp, statusColor, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = req.status.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Player Name, FF UID & Amount
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = req.userName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Free Fire UID: ${req.freeFireUid} • User: ${req.userId}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${req.amount}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GamingGold
                                    )
                                    Text(
                                        text = "Reserved Virtual",
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Payout Destination Row with Copy UPI Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GamingDarkBackground)
                                    .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Player UPI ID (${req.method})", fontSize = 9.sp, color = TextMuted)
                                    Text(
                                        text = req.payoutDestination,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GamingSecondary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GamingPrimary.copy(alpha = 0.2f))
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(req.payoutDestination))
                                            Toast.makeText(context, "Copied UPI: ${req.payoutDestination}", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GamingPrimary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Copy UPI", fontSize = 10.sp, color = GamingPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Payment Reference / Bank UTR if present
                            if (!req.paymentReference.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Bank UTR / Ref: ${req.paymentReference}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGreen
                                )
                            }

                            // Rejection Reason if present
                            if (!req.rejectionReason.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Rejection Reason: ${req.rejectionReason} (Funds released to player)",
                                    fontSize = 10.sp,
                                    color = GamingRed
                                )
                            }

                            // Admin Actions
                            if (req.status == WithdrawalStatus.PENDING) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { onUpdateWithdrawalStatus(req.id, WithdrawalStatus.APPROVED, null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingSecondary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Approve", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            markPaidTarget = req
                                            paymentUtrInput = ""
                                            confirmPaymentChecked = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.3f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Paid", tint = Color.Black, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Mark Paid", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            rejectWithdrawalTarget = req
                                            rejectWithdrawalReasonInput = "Invalid UPI ID"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingRed.copy(alpha = 0.2f)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.1f)
                                    ) {
                                        Text(text = "Reject", fontSize = 11.sp, color = GamingRed, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else if (req.status == WithdrawalStatus.APPROVED || req.status == WithdrawalStatus.PROCESSING) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            markPaidTarget = req
                                            paymentUtrInput = ""
                                            confirmPaymentChecked = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.3f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Paid", tint = Color.Black, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Confirm Real Payout", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            rejectWithdrawalTarget = req
                                            rejectWithdrawalReasonInput = "Payment Failed / Reversal"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingRed.copy(alpha = 0.2f)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Reject & Refund", fontSize = 11.sp, color = GamingRed, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 9: PLAYER MANAGEMENT & FAIR PLAY ENFORCEMENT
        // ==========================================
        if (selectedAdminTab == 9) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "প্লেয়ার ও অ্যাকাউন্ট পরিচালনা" else "Player Management & Account Control",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Permanent Player ID search, suspension duration, and permanent bans",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GamingPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${allPlayers.size} Total Players",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Search Bar by Permanent Player ID, FF UID, or Name
            item {
                OutlinedTextField(
                    value = playerSearchQuery,
                    onValueChange = { playerSearchQuery = it },
                    label = { Text("Search by Player ID, FF UID, or Name") },
                    placeholder = { Text("e.g. BA-70912, BA-10293, 2948109382") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GamingSecondary) },
                    trailingIcon = {
                        if (playerSearchQuery.isNotBlank()) {
                            IconButton(onClick = { playerSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_player_search_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Status Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statusChips = listOf(
                        "ALL" to "All (${allPlayers.size})",
                        "ACTIVE" to "Active (${allPlayers.count { it.effectiveAccountStatus == AccountStatus.ACTIVE }})",
                        "SUSPENDED" to "Suspended (${allPlayers.count { it.effectiveAccountStatus == AccountStatus.TEMPORARILY_SUSPENDED }})",
                        "BANNED" to "Banned (${allPlayers.count { it.effectiveAccountStatus == AccountStatus.PERMANENTLY_BANNED }})"
                    )

                    statusChips.forEach { (key, label) ->
                        val isSelected = playerStatusFilter == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
                                .clickable { playerStatusFilter = key }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("player_filter_$key")
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Account Status Action Tool Card (Active / Suspended / Banned)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("player_status_action_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (selectedActionStatus) {
                            AccountStatus.PERMANENTLY_BANNED -> GamingRed.copy(alpha = 0.6f)
                            AccountStatus.TEMPORARILY_SUSPENDED -> GamingGold.copy(alpha = 0.6f)
                            AccountStatus.ACTIVE -> GamingGreen.copy(alpha = 0.6f)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (selectedActionStatus) {
                                    AccountStatus.PERMANENTLY_BANNED -> Icons.Default.Block
                                    AccountStatus.TEMPORARILY_SUSPENDED -> Icons.Default.Timer
                                    AccountStatus.ACTIVE -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                tint = when (selectedActionStatus) {
                                    AccountStatus.PERMANENTLY_BANNED -> GamingRed
                                    AccountStatus.TEMPORARILY_SUSPENDED -> GamingGold
                                    AccountStatus.ACTIVE -> GamingGreen
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Account Status Controller",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Target Player ID input
                        OutlinedTextField(
                            value = banTargetUserId,
                            onValueChange = { banTargetUserId = it },
                            label = { Text("Target Player ID or Free Fire UID") },
                            placeholder = { Text("e.g. BA-70912 or select player below") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_player_id_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3 Account Status Options Radio/Chips
                        Text(text = "Target Account Status:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val statusOptions = listOf(
                                AccountStatus.ACTIVE to "Active",
                                AccountStatus.TEMPORARILY_SUSPENDED to "Suspended",
                                AccountStatus.PERMANENTLY_BANNED to "Perm Ban"
                            )

                            statusOptions.forEach { (status, label) ->
                                val isSelected = selectedActionStatus == status
                                val btnColor = when (status) {
                                    AccountStatus.ACTIVE -> GamingGreen
                                    AccountStatus.TEMPORARILY_SUSPENDED -> GamingGold
                                    AccountStatus.PERMANENTLY_BANNED -> GamingRed
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) btnColor.copy(alpha = 0.25f) else GamingSurfaceVariant)
                                        .border(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) btnColor else GamingBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedActionStatus = status }
                                        .padding(vertical = 8.dp)
                                        .testTag("status_option_${status.name}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) btnColor else TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Duration Picker (when Temporarily Suspended)
                        if (selectedActionStatus == AccountStatus.TEMPORARILY_SUSPENDED) {
                            Text(text = "Suspension Duration:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val durationPresets = listOf(
                                    "24" to "24h (1 Day)",
                                    "72" to "3 Days",
                                    "168" to "7 Days",
                                    "720" to "30 Days"
                                )
                                durationPresets.forEach { (hours, label) ->
                                    val isSelected = suspensionHoursInput == hours
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) GamingGold.copy(alpha = 0.3f) else GamingSurfaceVariant)
                                            .border(0.8.dp, if (isSelected) GamingGold else GamingBorder, RoundedCornerShape(6.dp))
                                            .clickable { suspensionHoursInput = hours }
                                            .padding(horizontal = 8.dp, vertical = 5.dp)
                                            .testTag("duration_preset_$hours")
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) GamingGold else TextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = suspensionHoursInput,
                                onValueChange = { suspensionHoursInput = it.filter { char -> char.isDigit() } },
                                label = { Text("Custom Duration in Hours") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("suspension_hours_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Reason Input (for suspension or ban)
                        if (selectedActionStatus != AccountStatus.ACTIVE) {
                            OutlinedTextField(
                                value = banReasonInput,
                                onValueChange = { banReasonInput = it },
                                label = { Text("Reason for Restriction") },
                                placeholder = { Text("e.g. Third-party script / macro detection") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ban_reason_input")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Quick Reason suggestions
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(
                                    "Third-party script",
                                    "Teaming in solo match",
                                    "Emulator bypass macro",
                                    "Offensive misconduct"
                                ).forEach { reasonSnippet ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GamingSurfaceVariant)
                                            .clickable { banReasonInput = reasonSnippet }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(text = reasonSnippet, fontSize = 9.sp, color = TextMuted)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Action Execution Button
                        when (selectedActionStatus) {
                            AccountStatus.ACTIVE -> {
                                Button(
                                    onClick = {
                                        if (banTargetUserId.isNotBlank()) {
                                            onUnbanPlayer(banTargetUserId.trim())
                                            Toast.makeText(context, "Player unbanned and restored to Active", Toast.LENGTH_SHORT).show()
                                            banTargetUserId = ""
                                        }
                                    },
                                    enabled = banTargetUserId.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_restore_active_btn")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Restore Account to Active", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            AccountStatus.TEMPORARILY_SUSPENDED -> {
                                Button(
                                    onClick = {
                                        val hours = suspensionHoursInput.toIntOrNull() ?: 24
                                        if (banTargetUserId.isNotBlank()) {
                                            onSuspendPlayer(banTargetUserId.trim(), hours, banReasonInput)
                                            Toast.makeText(context, "Player temporarily suspended for ${hours}h", Toast.LENGTH_SHORT).show()
                                            banTargetUserId = ""
                                        }
                                    },
                                    enabled = banTargetUserId.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_apply_suspension_btn")
                                ) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Apply Temporary Suspension", fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                            AccountStatus.PERMANENTLY_BANNED -> {
                                Button(
                                    onClick = {
                                        if (banTargetUserId.isNotBlank()) {
                                            val target = allPlayers.find {
                                                it.id.equals(banTargetUserId.trim(), ignoreCase = true) ||
                                                it.playerId.equals(banTargetUserId.trim(), ignoreCase = true) ||
                                                it.freeFireUid.equals(banTargetUserId.trim(), ignoreCase = true)
                                            } ?: User(
                                                id = banTargetUserId.trim(),
                                                name = "Player ${banTargetUserId.trim()}",
                                                email = "",
                                                freeFireName = "PLAYER",
                                                freeFireUid = banTargetUserId.trim()
                                            )
                                            permanentBanTargetUser = target
                                            showPermanentBanConfirmDialog = true
                                        }
                                    },
                                    enabled = banTargetUserId.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_permanent_ban_btn")
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Permanently Ban Account (Requires Confirmation)", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Player Directory Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Player Registry (${filteredPlayers.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tap any player to manage",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Players List
            if (filteredPlayers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "No players match the filter.", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else {
                items(filteredPlayers, key = { it.id }) { player ->
                    val status = player.effectiveAccountStatus
                    val statusColor = when (status) {
                        AccountStatus.ACTIVE -> GamingGreen
                        AccountStatus.TEMPORARILY_SUSPENDED -> GamingGold
                        AccountStatus.PERMANENTLY_BANNED -> GamingRed
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("player_card_${player.playerId}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Top Row: Player ID and Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(statusColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = statusColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = player.playerId,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                color = TextPrimary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(player.playerId))
                                                    Toast.makeText(context, "Player ID copied", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                        Text(text = "${player.name} • ${player.freeFireName}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }

                                // Status Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(statusColor.copy(alpha = 0.2f))
                                        .border(0.8.dp, statusColor, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = status.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = GamingBorder.copy(alpha = 0.5f)
                            )

                            // Middle Row: FF Details & Preserved Wallet Balance
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Free Fire UID", fontSize = 9.sp, color = TextMuted)
                                    Text(player.freeFireUid, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Column {
                                    Text("FF Level", fontSize = 9.sp, color = TextMuted)
                                    Text("Lvl ${player.freeFireLevel}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GamingSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Cloud Wallet (Preserved)", fontSize = 9.sp, color = TextMuted)
                                    Text("₹${player.walletBalance}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = GamingGreen)
                                }
                            }

                            // Ban or Suspension Notice Details
                            if (status != AccountStatus.ACTIVE) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(statusColor.copy(alpha = 0.08f))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Reason: ${player.banReason ?: "Fair Play violation"}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = statusColor
                                        )
                                        if (status == AccountStatus.TEMPORARILY_SUSPENDED) {
                                            Text(
                                                text = "Remaining: ${player.suspensionRemainingFormatted}",
                                                fontSize = 9.sp,
                                                color = TextMuted
                                            )
                                        }
                                        if (!player.bannedByAdmin.isNullOrBlank()) {
                                            Text(
                                                text = "Admin: ${player.bannedByAdmin}",
                                                fontSize = 9.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Bottom Action Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = {
                                        banTargetUserId = player.playerId
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Select ID", fontSize = 10.sp, color = TextPrimary)
                                }

                                if (status != AccountStatus.ACTIVE) {
                                    Button(
                                        onClick = {
                                            onUnbanPlayer(player.id)
                                            Toast.makeText(context, "${player.playerId} restored to Active", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Unban / Restore", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            banTargetUserId = player.playerId
                                            selectedActionStatus = AccountStatus.TEMPORARILY_SUSPENDED
                                            onSuspendPlayer(player.id, 24, "Fair Play safety check")
                                            Toast.makeText(context, "Suspended ${player.playerId} for 24h", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Suspend 24h", fontSize = 10.sp, color = GamingGold)
                                    }

                                    Button(
                                        onClick = {
                                            permanentBanTargetUser = player
                                            showPermanentBanConfirmDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Ban", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // ==========================================
            // FAIR PLAY & ANTI-CHEAT MONITORING DASHBOARD
            // ==========================================
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "অ্যান্টি-চিট ও ফেয়ার প্লে নজরদারি" else "Fair Play & Anti-Cheat Monitoring",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Audited evidence review • Player reports & automated result inconsistency flags",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GamingRed.copy(alpha = 0.2f))
                            .border(1.dp, GamingRed.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${incidentReports.count { it.reportStatus == IncidentReportStatus.PENDING || it.reportStatus == IncidentReportStatus.UNDER_REVIEW }} Pending",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingRed
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Scope & Privacy Notice in Admin Panel
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingDarkBackground)
                        .border(1.dp, GamingBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = GamingGreen, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Fair Play Guidelines & Scope", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GamingGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Tournament-Level Integrity: BATTLE ARENA audits match records, duplicate result submissions, and player-submitted proof.\n• Privacy & Security Guarantee: We do NOT inspect, tamper with, or collect private data from the internal Free Fire client.\n• No Auto-Banning on Suspicion: Serious penalties require documented proof, authorized Admin approval, and provide an appeal channel.",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Report Status Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", "PENDING", "UNDER_REVIEW", "VERIFIED_VIOLATION", "DISMISSED", "WITH_APPEAL").forEach { filterKey ->
                        val isSelected = cheatReportFilter == filterKey
                        val count = when (filterKey) {
                            "ALL" -> incidentReports.size
                            "PENDING" -> incidentReports.count { it.reportStatus == IncidentReportStatus.PENDING }
                            "UNDER_REVIEW" -> incidentReports.count { it.reportStatus == IncidentReportStatus.UNDER_REVIEW }
                            "VERIFIED_VIOLATION" -> incidentReports.count { it.reportStatus == IncidentReportStatus.VERIFIED_VIOLATION }
                            "DISMISSED" -> incidentReports.count { it.reportStatus == IncidentReportStatus.DISMISSED }
                            "WITH_APPEAL" -> incidentReports.count { it.appeal != null }
                            else -> 0
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingRed else GamingSurfaceVariant)
                                .border(1.dp, if (isSelected) GamingRed else GamingBorder, RoundedCornerShape(8.dp))
                                .clickable { cheatReportFilter = filterKey }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$filterKey ($count)",
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            val filteredIncidentReports = incidentReports.filter { r ->
                when (cheatReportFilter) {
                    "PENDING" -> r.reportStatus == IncidentReportStatus.PENDING
                    "UNDER_REVIEW" -> r.reportStatus == IncidentReportStatus.UNDER_REVIEW
                    "VERIFIED_VIOLATION" -> r.reportStatus == IncidentReportStatus.VERIFIED_VIOLATION
                    "DISMISSED" -> r.reportStatus == IncidentReportStatus.DISMISSED
                    "WITH_APPEAL" -> r.appeal != null
                    else -> true
                }
            }

            if (filteredIncidentReports.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface)
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("No anti-cheat reports found under '$cheatReportFilter' filter.", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            } else {
                items(filteredIncidentReports, key = { it.id }) { report ->
                    val statusColor = when (report.reportStatus) {
                        IncidentReportStatus.PENDING -> GamingGold
                        IncidentReportStatus.UNDER_REVIEW -> GamingSecondary
                        IncidentReportStatus.VERIFIED_VIOLATION -> GamingRed
                        IncidentReportStatus.DISMISSED -> GamingGreen
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .testTag("incident_report_card_${report.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header: Report ID, Match ID & Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "#${report.id}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "• Match #${report.tournamentId}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GamingGold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(statusColor.copy(alpha = 0.2f))
                                        .border(0.8.dp, statusColor, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = report.reportStatus.displayName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Reported Target info
                            Text(
                                text = "Suspect: ${report.reportedUserName} • Player ID: ${report.targetPlayerId.ifBlank { report.reportedUserId }} • UID: ${report.reportedUserUid}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Category: ${report.category.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GamingSecondary
                            )
                            Text(
                                text = "Reason: ${report.reason}",
                                fontSize = 11.sp,
                                color = GamingRed
                            )

                            // Evidence details
                            if (!report.evidenceText.isNullOrBlank() || !report.evidenceUrl.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GamingDarkBackground)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        if (!report.evidenceUrl.isNullOrBlank()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Link, contentDescription = null, tint = GamingSecondary, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "Evidence URL: ${report.evidenceUrl}", fontSize = 10.sp, color = GamingSecondary)
                                            }
                                        }
                                        if (!report.evidenceText.isNullOrBlank()) {
                                            Text(text = "Observations: ${report.evidenceText}", fontSize = 10.sp, color = TextSecondary)
                                        }
                                    }
                                }
                            }

                            // Appeal section if present
                            if (report.appeal != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                val appeal = report.appeal
                                val appealColor = when (appeal.status) {
                                    "ACCEPTED" -> GamingGreen
                                    "REJECTED" -> GamingRed
                                    else -> GamingGold
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(appealColor.copy(alpha = 0.12f))
                                        .border(1.dp, appealColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "🛡️ Player Appeal Filed (#${appeal.id})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = appealColor)
                                            Text(text = "Status: ${appeal.status}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = appealColor)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Player Statement: ${appeal.explanation}", fontSize = 10.sp, color = TextPrimary)
                                        if (!appeal.proofUrl.isNullOrBlank()) {
                                            Text(text = "Proof Link: ${appeal.proofUrl}", fontSize = 10.sp, color = GamingSecondary)
                                        }
                                        if (appeal.status == "PENDING") {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        onReviewPlayerAppeal(report.id, true, "Appeal accepted upon gameplay re-check. Penalty lifted.")
                                                        Toast.makeText(context, "Appeal accepted! Player restored to Active status.", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.weight(1f).testTag("accept_appeal_btn_${report.id}")
                                                ) {
                                                    Text("Accept & Restore", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                }
                                                Button(
                                                    onClick = {
                                                        onReviewPlayerAppeal(report.id, false, "Appeal rejected. Evidence of third-party script confirmed.")
                                                        Toast.makeText(context, "Appeal rejected.", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = GamingRed.copy(alpha = 0.2f)),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.weight(1f).testTag("reject_appeal_btn_${report.id}")
                                                ) {
                                                    Text("Reject Appeal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Investigation notes & Admin Actions
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (!report.reviewedByAdmin.isNullOrBlank()) "Reviewed by: ${report.reviewedByAdmin}" else "Filed: ${sdf.format(Date(report.timestamp))}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )

                                Button(
                                    onClick = {
                                        reviewReportTarget = report
                                        reviewDecisionStatus = report.reportStatus
                                        reviewAdminNotesInput = report.investigationNotes ?: ""
                                        reviewPenaltyAction = null
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                                    modifier = Modifier.testTag("admin_review_report_btn_${report.id}")
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Review & Decide", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Security & Admin Audit Logs
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "অ্যাডমিন নিরীক্ষা লগ" else "Security & Admin Audit Logs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(text = "${adminAuditLogs.size} Events", fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (adminAuditLogs.isEmpty()) {
                item {
                    Text(text = "No administrative events logged yet.", fontSize = 11.sp, color = TextMuted)
                }
            } else {
                items(adminAuditLogs.reversed(), key = { it.id }) { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = log.action,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingSecondary
                                )
                                Text(
                                    text = sdf.format(Date(log.timestamp)),
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${log.details} (Target: ${log.targetId})",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "By: ${log.performedBy}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

    // Edit Room Credentials Dialog
    if (editRoomTournament != null) {
        val t = editRoomTournament!!
        var roomInput by remember { mutableStateOf(t.roomId ?: "") }
        var passInput by remember { mutableStateOf(t.roomPassword ?: "") }

        Dialog(onDismissRequest = { editRoomTournament = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Set Custom Room ID & Password", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = roomInput,
                        onValueChange = { roomInput = it },
                        label = { Text("Custom Room ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passInput,
                        onValueChange = { passInput = it },
                        label = { Text("Custom Room Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { editRoomTournament = null },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                onUpdateRoomCredentials(t.id, roomInput, passInput)
                                editRoomTournament = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Reject Deposit Dialog
    if (rejectDepositTarget != null) {
        val target = rejectDepositTarget!!
        Dialog(onDismissRequest = { rejectDepositTarget = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Reject Deposit Request #${target.id}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingRed
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rejectDepositReason,
                        onValueChange = { rejectDepositReason = it },
                        label = { Text("Reason for Rejection") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { rejectDepositTarget = null },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                onRejectAddMoney(target.id, rejectDepositReason)
                                rejectDepositTarget = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirm Reject", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog: Mark Tournament as Unsuccessful
    if (markUnsuccessfulTarget != null) {
        val target = markUnsuccessfulTarget!!
        Dialog(onDismissRequest = { markUnsuccessfulTarget = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingRed, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mark Match #${target.id} as Unsuccessful",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingRed
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This will mark the match as Unsuccessful and automatically queue full entry fee refunds (₹${target.entryFee}) for all ${target.filledSlots} registered player(s) directly to their wallets.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = markUnsuccessfulReason,
                        onValueChange = { markUnsuccessfulReason = it },
                        label = { Text("Reason for Cancellation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { markUnsuccessfulTarget = null },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                onMarkTournamentUnsuccessful(target.id, markUnsuccessfulReason)
                                markUnsuccessfulTarget = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("admin_confirm_mark_unsuccessful_btn")
                        ) {
                            Text("Confirm & Queue Refunds", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog: Edit Match Schedule (Date, Time IST, Title, Slots)
    if (editScheduleTarget != null) {
        val target = editScheduleTarget!!
        Dialog(onDismissRequest = { editScheduleTarget = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Edit Match Schedule #${target.id}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Update the scheduled date, IST start time, title, and player capacity. Participants will be notified.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editScheduleTitleInput,
                        onValueChange = { editScheduleTitleInput = it },
                        label = { Text("Match Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editScheduleDateInput,
                        onValueChange = { editScheduleDateInput = it },
                        label = { Text("Match Date (e.g. 05 Nov 2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editScheduleInput,
                        onValueChange = { editScheduleInput = it },
                        label = { Text("Scheduled Time (IST)") },
                        placeholder = { Text("e.g. 08:30 AM IST or 09:00 PM IST") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editScheduleSlotsInput,
                        onValueChange = { editScheduleSlotsInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Total Slots") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { editScheduleTarget = null },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                val slotsInt = editScheduleSlotsInput.toIntOrNull()
                                onEditMatchSchedule(target.id, editScheduleDateInput, editScheduleInput, slotsInt, editScheduleTitleInput)
                                onUpdateBattleRoyaleSchedule(target.id, editScheduleInput)
                                editScheduleTarget = null
                                Toast.makeText(context, "Match #${target.id} schedule updated successfully!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("admin_confirm_update_schedule_btn")
                        ) {
                            Text("Save Schedule", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Dialog: Custom Room Credentials Management
    // ==========================================
    if (manageCustomRoomTarget != null) {
        val target = manageCustomRoomTarget!!
        ManageCustomRoomDialog(
            tournament = target,
            language = language,
            onDismiss = { manageCustomRoomTarget = null },
            onPublishRoom = { rId, pass, notes ->
                onPublishRoomDetails(target.id, rId, pass, notes)
                manageCustomRoomTarget = null
                Toast.makeText(context, "Room credentials published to eligible paid participants of Match #${target.id}!", Toast.LENGTH_LONG).show()
            },
            onUpdateRoom = { rId, pass, status, notes ->
                onPublishRoomDetails(target.id, rId, pass, notes)
                manageCustomRoomTarget = null
                Toast.makeText(context, "Room details updated for Match #${target.id}!", Toast.LENGTH_SHORT).show()
            },
            onHideRoom = {
                onHideRoomDetails(target.id)
                manageCustomRoomTarget = null
                Toast.makeText(context, "Room credentials hidden for Match #${target.id}.", Toast.LENGTH_SHORT).show()
            },
            onCloseRoomAccess = {
                onCloseRoomAccess(target.id)
                manageCustomRoomTarget = null
                Toast.makeText(context, "Room access closed for Match #${target.id}.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // ==========================================
    // Dialog: Create Scheduled Match
    // ==========================================
    if (showCreateScheduledDialog) {
        CreateScheduledMatchDialog(
            language = language,
            onDismiss = { showCreateScheduledDialog = false },
            onCreateScheduled = { titleEn, titleBn, mode, date, time, slots, fee, descEn, descBn, map ->
                onCreateScheduledMatch(titleEn, titleBn, mode, date, time, slots, fee, descEn, descBn, map)
                showCreateScheduledDialog = false
                Toast.makeText(context, "Scheduled match '$titleEn' ($time) created!", Toast.LENGTH_SHORT).show()
            },
            onGenerateBatchSchedule = { mode, date, times, fee ->
                onGenerateBatchSchedule(mode, date, times, fee)
                showCreateScheduledDialog = false
                Toast.makeText(context, "Generated 5-Match Schedule successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // ==========================================
    // Dialog: Create Manual Match
    // ==========================================
    if (showCreateManualDialog) {
        CreateManualMatchDialog(
            language = language,
            onDismiss = { showCreateManualDialog = false },
            onCreateManual = { titleEn, titleBn, mode, slots, fee, descEn, descBn, isRegOpen, map ->
                onCreateManualMatch(titleEn, titleBn, mode, slots, fee, descEn, descBn, isRegOpen, map)
                showCreateManualDialog = false
                Toast.makeText(context, "Manual match '$titleEn' created successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // ==========================================
    // Dialog: Withdraw to Owner UPI
    // ==========================================
    if (showAdminWithdrawDialog) {
        val amountInt = adminWithdrawAmountInput.toIntOrNull() ?: 0
        val isValidAmount = amountInt > 0 && amountInt <= adminWallet.availableBalance
        val destinationUpi = adminWallet.verifiedUpiId

        Dialog(onDismissRequest = {
            showAdminWithdrawDialog = false
            showConfirmWithdrawalStep = false
            withdrawErrorMessage = null
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GamingGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GamingGreen, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "মালিকের UPI তে ক্যাশ উইথড্র" else "Withdraw to Owner UPI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        IconButton(onClick = {
                            showAdminWithdrawDialog = false
                            showConfirmWithdrawalStep = false
                            withdrawErrorMessage = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!showConfirmWithdrawalStep) {
                        // Balance Pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingSurfaceVariant)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "উইথড্রয়ালযোগ্য উপলব্ধ ব্যালেন্স:" else "Available Balance:",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "₹${adminWallet.availableBalance}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GamingGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (language == AppLanguage.BENGALI) "উইথড্র পরিমাণ (টাকা):" else "Withdrawal Amount (₹):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = adminWithdrawAmountInput,
                            onValueChange = {
                                adminWithdrawAmountInput = it.filter { ch -> ch.isDigit() }
                                withdrawErrorMessage = null
                            },
                            placeholder = { Text("Enter amount in ₹") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_withdraw_amount_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingGreen,
                                unfocusedBorderColor = GamingBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Amount Selector Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(50, 100, 200, 500).forEach { chipAmount ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GamingSurfaceVariant)
                                        .clickable {
                                            adminWithdrawAmountInput = chipAmount.toString()
                                            withdrawErrorMessage = null
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "₹$chipAmount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GamingGreen)
                                }
                            }
                            // Max button
                            Box(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GamingGold.copy(alpha = 0.15f))
                                    .border(1.dp, GamingGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        adminWithdrawAmountInput = adminWallet.availableBalance.toString()
                                        withdrawErrorMessage = null
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Max (₹${adminWallet.availableBalance})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Destination VPA display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingSurfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, GamingBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = GamingGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Destination VPA / UPI:", fontSize = 10.sp, color = TextSecondary)
                                    Text(destinationUpi, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }

                        withdrawErrorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = err, fontSize = 11.sp, color = GamingRed)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (amountInt <= 0) {
                                    withdrawErrorMessage = "Amount must be greater than ₹0"
                                } else if (amountInt > adminWallet.availableBalance) {
                                    withdrawErrorMessage = "Amount exceeds available balance (₹${adminWallet.availableBalance})"
                                } else {
                                    showConfirmWithdrawalStep = true
                                    withdrawErrorMessage = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                            shape = RoundedCornerShape(10.dp),
                            enabled = isValidAmount,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_continue_withdraw_btn")
                        ) {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "পরবর্তী ধাপ (যাচাই)" else "Review & Confirm",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    } else {
                        // Confirmation Step
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GamingGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "উইথড্রয়াল সামারি নিশ্চিত করুন" else "Confirm Payout Details",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Payout Amount:", fontSize = 12.sp, color = TextSecondary)
                                    Text("₹$amountInt", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Destination UPI:", fontSize = 12.sp, color = TextSecondary)
                                    Text(destinationUpi, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GamingGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Settlement Method:", fontSize = 12.sp, color = TextSecondary)
                                    Text("Instant Automated IMPS", fontSize = 12.sp, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Balance After:", fontSize = 12.sp, color = TextSecondary)
                                    Text("₹${adminWallet.availableBalance - amountInt}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GamingGold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showConfirmWithdrawalStep = false },
                                colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back", color = TextSecondary)
                            }

                            Button(
                                onClick = {
                                    onRequestAdminUPIWithdrawal(amountInt, destinationUpi, true)
                                    showAdminWithdrawDialog = false
                                    showConfirmWithdrawalStep = false
                                    withdrawErrorMessage = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("admin_confirm_payout_btn")
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "অনুমোদন ও পেআউট" else "Authorize Payout",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Dialog: Edit Owner Verified UPI ID
    // ==========================================
    if (showEditOwnerUpiDialog) {
        val upiRegex = Regex("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$")
        var upiFormatError by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = { showEditOwnerUpiDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "মালিকের UPI আইডি পরিবর্তন ও যাচাই" else "Update Owner Verified UPI ID",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Withdrawals are strictly routed to this verified VPA. Only the app owner ($BATTLE_ARENA_OWNER_EMAIL) can modify this setting.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editOwnerUpiInput,
                        onValueChange = {
                            editOwnerUpiInput = it
                            upiFormatError = null
                        },
                        label = { Text("Owner UPI ID (VPA)") },
                        placeholder = { Text("e.g. kirtaniamanik@okhdfcbank") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_edit_upi_input"),
                        singleLine = true
                    )

                    // Quick Bank Suffix Chips
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("@okhdfcbank", "@oksbi", "@paytm", "@ybl", "@axl").forEach { handle ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GamingSurfaceVariant)
                                    .clickable {
                                        val prefix = editOwnerUpiInput.substringBefore("@").ifBlank { "kirtaniamanik" }
                                        editOwnerUpiInput = "$prefix$handle"
                                        upiFormatError = null
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(text = handle, fontSize = 9.sp, color = GamingGold)
                            }
                        }
                    }

                    upiFormatError?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = err, fontSize = 11.sp, color = GamingRed)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showEditOwnerUpiDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                val clean = editOwnerUpiInput.trim()
                                if (!upiRegex.matches(clean)) {
                                    upiFormatError = "Invalid UPI format. Must be username@bank (e.g. name@okhdfcbank)"
                                } else {
                                    onSaveOwnerVerifiedUpiId(clean)
                                    showEditOwnerUpiDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("admin_save_upi_btn")
                        ) {
                            Text("Verify & Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Dialog: Edit & Verify Match Result
    // ==========================================
    editResultTarget?.let { target ->
        Dialog(onDismissRequest = { editResultTarget = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Verify / Edit Match Standing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${target.playerName} (${target.freeFireUid}) • ${target.tournamentTitle}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editRankInput,
                            onValueChange = { editRankInput = it },
                            label = { Text("Final Rank #") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editKillsInput,
                            onValueChange = { editKillsInput = it },
                            label = { Text("Kills Count") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Verification Decision:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { editApproveInput = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (editApproveInput) GamingGreen else GamingSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Approve",
                                fontWeight = FontWeight.Bold,
                                color = if (editApproveInput) Color.Black else TextSecondary
                            )
                        }

                        Button(
                            onClick = { editApproveInput = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!editApproveInput) GamingRed else GamingSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Reject",
                                fontWeight = FontWeight.Bold,
                                color = if (!editApproveInput) Color.White else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { editResultTarget = null },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                val rank = editRankInput.toIntOrNull()
                                val kills = editKillsInput.toIntOrNull()
                                onVerifyMatchResult(target.id, rank, kills, editApproveInput)
                                editResultTarget = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Save Verification", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Dialog: Manual Real Money Payment Confirmation (Mark as Paid)
    // ==========================================
    markPaidTarget?.let { target ->
        Dialog(onDismissRequest = { markPaidTarget = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(18.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Manual Real Money Payout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = { markPaidTarget = null }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Player & Amount Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = target.userName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(text = "FF UID: ${target.freeFireUid}", fontSize = 11.sp, color = TextSecondary)
                                }
                                Text(text = "₹${target.amount}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = GamingGold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GamingSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Pay to Player UPI:", fontSize = 9.sp, color = TextMuted)
                                    Text(text = target.payoutDestination, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GamingSecondary)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GamingPrimary.copy(alpha = 0.2f))
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(target.payoutDestination))
                                            Toast.makeText(context, "UPI ID copied!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "Copy UPI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "1. Open PhonePe, Google Pay, or Paytm and send ₹${target.amount}.\n2. Enter the generated Bank UTR number below to record the payment:",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = paymentUtrInput,
                        onValueChange = { paymentUtrInput = it.trim().uppercase() },
                        label = { Text("Bank UTR / Transaction Reference") },
                        placeholder = { Text("e.g. 492019482910") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_payout_utr_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Confirmation Checkbox Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (confirmPaymentChecked) GamingGreen.copy(alpha = 0.15f) else GamingSurfaceVariant)
                            .clickable { confirmPaymentChecked = !confirmPaymentChecked }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (confirmPaymentChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Confirm",
                            tint = if (confirmPaymentChecked) GamingGreen else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I have manually transferred ₹${target.amount} to this UPI ID.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (confirmPaymentChecked) TextPrimary else TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val canConfirm = paymentUtrInput.length >= 6 && confirmPaymentChecked
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { markPaidTarget = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                if (canConfirm) {
                                    onUpdateWithdrawalStatus(target.id, WithdrawalStatus.PAID, paymentUtrInput.trim().uppercase())
                                    markPaidTarget = null
                                }
                            },
                            enabled = canConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                            modifier = Modifier.weight(1.4f).testTag("confirm_mark_paid_btn")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Paid", tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Confirm & Mark Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Dialog: Reject Withdrawal & Refund Funds
    // ==========================================
    rejectWithdrawalTarget?.let { target ->
        Dialog(onDismissRequest = { rejectWithdrawalTarget = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(18.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Reject Withdrawal & Refund Balance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Player: ${target.userName} • Request #${target.id} (₹${target.amount})",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingGold.copy(alpha = 0.12f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "ℹ️ Rejecting will immediately release ₹${target.amount} back to the player's Winning Balance in their virtual wallet.",
                            fontSize = 11.sp,
                            color = GamingGold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Quick Reasons:", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Invalid UPI ID", "Duplicate Request", "Under Fair Play Review").forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (rejectWithdrawalReasonInput == preset) GamingPrimary else GamingSurfaceVariant)
                                    .clickable { rejectWithdrawalReasonInput = preset }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 9.sp,
                                    color = if (rejectWithdrawalReasonInput == preset) Color.White else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = rejectWithdrawalReasonInput,
                        onValueChange = { rejectWithdrawalReasonInput = it },
                        label = { Text("Rejection Reason") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { rejectWithdrawalTarget = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                onUpdateWithdrawalStatus(target.id, WithdrawalStatus.REJECTED, rejectWithdrawalReasonInput.trim())
                                rejectWithdrawalTarget = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Reject & Refund ₹${target.amount}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Dialog: Authorized Players & Anti-Free Entry Roster Audit
    // ==========================================
    authorizedPlayersTarget?.let { target ->
        Dialog(onDismissRequest = { authorizedPlayersTarget = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(18.dp))
                    .testTag("authorized_players_dialog"),
                color = GamingSurface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Authorized Players & Anti-Free Entry",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${target.titleEn} (#${target.id}) • Entry Fee: ₹${target.entryFee}",
                                fontSize = 11.sp,
                                color = GamingGold
                            )
                        }
                        IconButton(onClick = { authorizedPlayersTarget = null }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Room ID & Pass Display Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Free Fire Room Credentials", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "Room: ${target.roomId ?: "Pending"} | Pass: ${target.roomPassword ?: "None"}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGreen
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GamingGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Slots: ${target.filledSlots}/${target.totalSlots}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Security Limitation Notice Banner (Requirement 6)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingGold.copy(alpha = 0.12f))
                            .border(1.dp, GamingGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Security Alert",
                                tint = GamingGold,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Important Security Rule: Free Fire Room credentials alone cannot technically prevent players from sharing with friends. Only players who paid the Entry Fee and are listed below are authorized participants. Kick any unverified players from the Free Fire room before starting the match!",
                                fontSize = 10.sp,
                                color = TextPrimary,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tool: Room Roster Cross-Check Tool
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant.copy(alpha = 0.6f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🔍 Room Roster Audit (Cross-Check In-Game Room)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Paste Free Fire Room UIDs (separated by comma, space, or newline) to verify against paid registrations:",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = roomUidsAuditInput,
                                onValueChange = { roomUidsAuditInput = it },
                                placeholder = { Text("e.g. 2948109382, 1829104821, 982019411", fontSize = 11.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("room_uids_audit_input"),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val uids = roomUidsAuditInput
                                        .split(',', ' ', '\n', '\t')
                                        .map { it.trim() }
                                        .filter { it.isNotBlank() }
                                    currentAuditReport = onAuditRoomRoster(target.id, uids)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("audit_room_roster_btn")
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = "Audit", tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Audit In-Game Room Roster", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Audit Report Display
                            currentAuditReport?.let { report ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Total in Room: ${report.totalAudited}", fontSize = 10.sp, color = TextPrimary)
                                    Text(text = "Authorized: ${report.authorizedCount}", fontSize = 10.sp, color = GamingGreen, fontWeight = FontWeight.Bold)
                                    Text(text = "Missing: ${report.missingAuthorizedUids.size}", fontSize = 10.sp, color = GamingGold)
                                }

                                if (report.unauthorizedUids.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GamingRed.copy(alpha = 0.2f))
                                            .border(1.dp, GamingRed, RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Warning, contentDescription = "Alert", tint = GamingRed, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "🚨 FREE ENTRY DETECTED! (${report.unauthorizedUids.size} Unauthorized)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = GamingRed
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "The following UIDs are in the room but NEVER paid the Entry Fee: ${report.unauthorizedUids.joinToString(", ")}. Action: Kick them from the Free Fire room immediately!",
                                                fontSize = 10.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                } else if (report.totalAudited > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GamingGreen.copy(alpha = 0.15f))
                                            .border(1.dp, GamingGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(6.dp)
                                    ) {
                                        Text(
                                            text = "✓ Clean Room! All ${report.authorizedCount} players in the room are authorized and paid.",
                                            fontSize = 10.sp,
                                            color = GamingGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Authorized Registered Players Header
                    Text(
                        text = "Authorized Registered Players (${target.participants.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (target.participants.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingDarkBackground)
                        ) {
                            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                                Text("No registered participants yet for this match.", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    } else {
                        target.participants.forEach { participant ->
                            val statusColor = when (participant.verificationStatus) {
                                PlayerVerificationStatus.VERIFIED -> GamingGreen
                                PlayerVerificationStatus.PENDING_CHECK -> GamingGold
                                PlayerVerificationStatus.UNAUTHORIZED -> GamingRed
                                PlayerVerificationStatus.KICKED -> Color(0xFFF97316)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, GamingBorder)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    // Row 1: Player Name, FF IGN & Verification Badge
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = participant.freeFireName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "Account: ${participant.name} (${participant.userId})",
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(statusColor.copy(alpha = 0.15f))
                                                .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = participant.verificationStatus.name,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Row 2: Free Fire UID & Payment Status
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "FF UID: ${participant.freeFireUid}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GamingSecondary
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(GamingGreen.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "PAID (₹${participant.entryFeePaid.takeIf { it > 0 } ?: target.entryFee})",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GamingGreen
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Bound Entry Pass: ${participant.entryId.takeIf { it.isNotBlank() } ?: "BA-ENTRY-BOUND-${participant.freeFireUid.takeLast(4)}"}",
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )

                                    if (!participant.adminNotes.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "Note: ${participant.adminNotes}", fontSize = 9.sp, color = GamingGold)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Row 3: Action Buttons (Verify / Kick)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (participant.verificationStatus != PlayerVerificationStatus.VERIFIED) {
                                            Button(
                                                onClick = {
                                                    onVerifyRoomPlayer(target.id, participant.freeFireUid, PlayerVerificationStatus.VERIFIED, "Verified by Admin in room")
                                                    authorizedPlayersTarget = target.copy(
                                                        participants = target.participants.map {
                                                            if (it.freeFireUid == participant.freeFireUid) it.copy(verificationStatus = PlayerVerificationStatus.VERIFIED) else it
                                                        }
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f).height(30.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Verify", tint = Color.Black, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Mark Verified", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        if (participant.verificationStatus != PlayerVerificationStatus.KICKED) {
                                            OutlinedButton(
                                                onClick = {
                                                    kickTargetParticipant = participant
                                                    kickReasonInput = "Unauthorized entry / friend room share / unapproved UID"
                                                    kickWithRefundChecked = false
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingRed),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f).height(30.dp)
                                            ) {
                                                Icon(Icons.Default.Block, contentDescription = "Kick", tint = GamingRed, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Kick / Remove", fontSize = 10.sp, color = GamingRed, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Dialog: Kick / Remove Unauthorized Player from Room
    // ==========================================
    kickTargetParticipant?.let { participant ->
        val currentTourney = authorizedPlayersTarget
        Dialog(onDismissRequest = { kickTargetParticipant = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingRed, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kick Player from Room",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingRed
                        )
                        IconButton(onClick = { kickTargetParticipant = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Player: ${participant.freeFireName} (FF UID: ${participant.freeFireUid})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = kickReasonInput,
                        onValueChange = { kickReasonInput = it },
                        label = { Text("Reason for Kick") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { kickWithRefundChecked = !kickWithRefundChecked }
                    ) {
                        Checkbox(
                            checked = kickWithRefundChecked,
                            onCheckedChange = { kickWithRefundChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = GamingGold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Refund Entry Fee (₹${participant.entryFeePaid.takeIf { it > 0 } ?: (currentTourney?.entryFee ?: 20)}) to player wallet",
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { kickTargetParticipant = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                if (currentTourney != null) {
                                    onRemoveUnauthorizedPlayer(
                                        currentTourney.id,
                                        participant.freeFireUid,
                                        kickReasonInput,
                                        kickWithRefundChecked
                                    )
                                    // Update local state in authorizedPlayersTarget
                                    authorizedPlayersTarget = currentTourney.copy(
                                        participants = currentTourney.participants.map {
                                            if (it.freeFireUid == participant.freeFireUid) {
                                                it.copy(
                                                    verificationStatus = PlayerVerificationStatus.KICKED,
                                                    adminNotes = "Kicked: $kickReasonInput"
                                                )
                                            } else it
                                        }
                                    )
                                }
                                kickTargetParticipant = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Confirm Kick", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Confirmation Dialog: Permanent Player Ban
    // ==========================================
    if (showPermanentBanConfirmDialog && permanentBanTargetUser != null) {
        val target = permanentBanTargetUser!!
        Dialog(onDismissRequest = {
            showPermanentBanConfirmDialog = false
            permanentBanTargetUser = null
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, GamingRed, RoundedCornerShape(20.dp))
                    .testTag("permanent_ban_confirmation_dialog"),
                color = GamingDarkBackground
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(GamingRed.copy(alpha = 0.2f))
                            .border(1.dp, GamingRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "Confirm Ban",
                            tint = GamingRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Confirm Permanent Ban",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = GamingRed
                    )

                    Text(
                        text = "Permanent restriction confirmation step",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Player ID:", fontSize = 11.sp, color = TextMuted)
                                Text(target.playerId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Player Name:", fontSize = 11.sp, color = TextMuted)
                                Text(target.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Free Fire UID:", fontSize = 11.sp, color = TextMuted)
                                Text(target.freeFireUid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Wallet (Preserved):", fontSize = 11.sp, color = TextMuted)
                                Text("₹${target.walletBalance}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GamingGreen)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Admin Performer:", fontSize = 11.sp, color = TextMuted)
                                Text(BATTLE_ARENA_OWNER_EMAIL, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingGold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingRed.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, GamingRed.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Reason for Permanent Ban:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingRed
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = banReasonInput.ifBlank { "Violation of Fair Play rules" },
                                fontSize = 11.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingGreen.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, GamingGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = GamingGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Player ID, cloud wallet records, and match history will be preserved. Login & matches will be blocked.",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                lineHeight = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showPermanentBanConfirmDialog = false
                                permanentBanTargetUser = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("cancel_permanent_ban_btn")
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                val reason = banReasonInput.ifBlank { "Violation of Fair Play rules" }
                                onPermanentlyBanPlayer(target.id, reason)
                                onBanPlayer(target.id, reason)
                                Toast.makeText(context, "Player ${target.playerId} permanently banned. Recorded by $BATTLE_ARENA_OWNER_EMAIL", Toast.LENGTH_LONG).show()
                                showPermanentBanConfirmDialog = false
                                permanentBanTargetUser = null
                                banTargetUserId = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("confirm_permanent_ban_btn")
                        ) {
                            Text("Confirm Ban", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // Fair Play & Anti-Cheat Review Dialog
    // ==========================================
    if (reviewReportTarget != null) {
        val report = reviewReportTarget!!
        Dialog(onDismissRequest = { reviewReportTarget = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, GamingBorder, RoundedCornerShape(18.dp))
                    .testTag("admin_cheat_review_dialog"),
                color = GamingDarkBackground
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(GamingPrimary.copy(alpha = 0.2f))
                            .border(1.dp, GamingPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Review Cheat Incident",
                            tint = GamingPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Fair Play & Anti-Cheat Investigation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Incident #${report.id} • Match ${report.tournamentId}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Suspect and Reporter Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Suspect Player ID:", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = report.targetPlayerId ?: "N/A",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingRed
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Reported By (Reporter):", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = report.reporterPlayerId ?: "Anonymous/System",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Category:", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = report.category.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Evidence and Description Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Report Details:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = report.reason, fontSize = 11.sp, color = TextSecondary)

                            if (!report.evidenceUrl.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Evidence Link / Recording:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GamingSecondary)
                                Text(
                                    text = report.evidenceUrl,
                                    fontSize = 11.sp,
                                    color = GamingSecondary,
                                    modifier = Modifier.clickable {
                                        clipboardManager.setText(AnnotatedString(report.evidenceUrl))
                                        Toast.makeText(context, "Evidence URL copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }

                            if (!report.evidenceText.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Evidence Notes: ${report.evidenceText}", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Decision Status Selector
                    Text(
                        text = "Investigation Status:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            IncidentReportStatus.UNDER_REVIEW to "Reviewing",
                            IncidentReportStatus.VERIFIED_VIOLATION to "Verified",
                            IncidentReportStatus.DISMISSED to "Dismiss"
                        ).forEach { (status, label) ->
                            val isSelected = reviewDecisionStatus == status
                            val chipColor = when (status) {
                                IncidentReportStatus.VERIFIED_VIOLATION -> GamingRed
                                IncidentReportStatus.DISMISSED -> GamingGreen
                                else -> GamingGold
                            }
                            Button(
                                onClick = { reviewDecisionStatus = status },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) chipColor else GamingSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("select_status_${status.name}")
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextPrimary
                                )
                            }
                        }
                    }

                    // Penalty Selection if Verified Violation
                    if (reviewDecisionStatus == IncidentReportStatus.VERIFIED_VIOLATION) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Penalty Action:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingRed,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                null to "Warning Only",
                                "SUSPEND" to "Suspend",
                                "BAN" to "Permanent Ban"
                            ).forEach { (action, label) ->
                                val isSelected = reviewPenaltyAction == action
                                OutlinedButton(
                                    onClick = { reviewPenaltyAction = action },
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) GamingRed else GamingBorder
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) GamingRed.copy(alpha = 0.2f) else Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("penalty_action_${action ?: "warning"}")
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) GamingRed else TextSecondary
                                    )
                                }
                            }
                        }

                        if (reviewPenaltyAction == "SUSPEND") {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = reviewSuspensionHoursInput,
                                onValueChange = { reviewSuspensionHoursInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Suspension Duration (Hours)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("review_suspension_hours_input")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Investigation Notes (Enforcing documented evidence)
                    OutlinedTextField(
                        value = reviewAdminNotesInput,
                        onValueChange = { reviewAdminNotesInput = it },
                        label = { Text("Admin Investigation Notes (Required for record)") },
                        placeholder = { Text("e.g. Cross-checked match recording; abnormal speed hacks verified.") },
                        modifier = Modifier.fillMaxWidth().height(80.dp).testTag("review_admin_notes_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Note: BATTLE ARENA monitors tournament rosters, submissions & reported evidence. Client memory inspection requires official Garena SDK.",
                        fontSize = 9.sp,
                        color = TextMuted,
                        lineHeight = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { reviewReportTarget = null },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("cancel_cheat_review_btn")
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                val suspensionHours = reviewSuspensionHoursInput.toIntOrNull() ?: 24
                                val notes = reviewAdminNotesInput.ifBlank { "Reviewed and resolved by Admin." }
                                onReviewCheatReport(
                                    report.id,
                                    reviewDecisionStatus,
                                    notes,
                                    reviewPenaltyAction,
                                    suspensionHours
                                )
                                Toast.makeText(context, "Incident #${report.id} updated & logged", Toast.LENGTH_SHORT).show()
                                reviewReportTarget = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.3f).testTag("submit_cheat_review_btn")
                        ) {
                            Text("Save Decision", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AdminStatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(GamingSurfaceVariant)
            .padding(10.dp)
    ) {
        Column {
            Text(text = label, fontSize = 10.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun AdminMatchCard(
    tournament: Tournament,
    language: AppLanguage,
    isOwner: Boolean,
    onManageRoom: () -> Unit,
    onEditSchedule: () -> Unit,
    onToggleRegistration: () -> Unit,
    onUpdateStatus: (TournamentStatus) -> Unit,
    onCancelAndRefund: () -> Unit,
    onVerifyTournament: () -> Unit,
    onAuditRoster: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("admin_match_card_${tournament.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (tournament.status == TournamentStatus.CANCELLED || tournament.status == TournamentStatus.UNSUCCESSFUL)
                GamingRed.copy(alpha = 0.5f)
            else GamingBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Title, Mode badge & Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (tournament.creationType == MatchCreationType.SCHEDULED) GamingGold.copy(alpha = 0.2f) else GamingPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tournament.creationType.displayName.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (tournament.creationType == MatchCreationType.SCHEDULED) GamingGold else GamingPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "#${tournament.id}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = tournament.getTitle(language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Match Status Badge
                val statusColor = when (tournament.status) {
                    TournamentStatus.LIVE -> GamingGreen
                    TournamentStatus.CONFIRMED, TournamentStatus.COUNTDOWN -> GamingGold
                    TournamentStatus.ROOM_PUBLISHED -> GamingSecondary
                    TournamentStatus.CANCELLED, TournamentStatus.UNSUCCESSFUL -> GamingRed
                    TournamentStatus.COMPLETED -> TextSecondary
                    else -> GamingPrimary
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(0.8.dp, statusColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tournament.status.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timing, Date & Slot info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(GamingSurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = GamingGold, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tournament.matchDate} • ${tournament.matchTimeIst}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GamingGold
                    )
                }

                Text(
                    text = "Slots: ${tournament.filledSlots}/${tournament.totalSlots} • ₹${tournament.entryFee} Fee",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Custom Room Status & Credentials Preview
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(GamingDarkBackground)
                    .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GamingSecondary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Column {
                        val roomSummary = if (tournament.roomStatus == RoomPublicationStatus.PUBLISHED) {
                            "Room: ${tournament.customRoomId ?: "Set"} • Pass: ${tournament.customRoomPassword ?: "Set"}"
                        } else {
                            "Room: Hidden / Not Published"
                        }
                        Text(
                            text = roomSummary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (tournament.roomStatus == RoomPublicationStatus.PUBLISHED) GamingGreen else TextSecondary
                        )
                        Text(
                            text = "Status: ${tournament.roomStatus.displayName} • Reg: ${if (tournament.isRegistrationOpen) "OPEN" else "CLOSED"}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                // Manage Room Button
                Button(
                    onClick = onManageRoom,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary.copy(alpha = 0.25f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingPrimary),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("admin_manage_room_btn_${tournament.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Manage Room", tint = GamingPrimary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Manage Room", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingPrimary)
                }
            }

            // Action Buttons Row: Schedule/Reg Toggle, Status, Cancel & Refund
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (tournament.creationType == MatchCreationType.SCHEDULED) {
                    OutlinedButton(
                        onClick = onEditSchedule,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("admin_edit_schedule_btn_${tournament.id}")
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = "Edit Schedule", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Schedule", fontSize = 10.sp)
                    }
                }

                Button(
                    onClick = onToggleRegistration,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tournament.isRegistrationOpen) GamingGold.copy(alpha = 0.2f) else GamingGreen.copy(alpha = 0.2f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (tournament.isRegistrationOpen) GamingGold else GamingGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("admin_toggle_reg_btn_${tournament.id}")
                ) {
                    Text(
                        text = if (tournament.isRegistrationOpen) "Close Reg" else "Open Reg",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tournament.isRegistrationOpen) GamingGold else GamingGreen
                    )
                }

                Button(
                    onClick = onVerifyTournament,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("admin_verify_match_btn_${tournament.id}")
                ) {
                    Icon(Icons.Default.Verified, contentDescription = "Verify", tint = Color.Black, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Verify", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                if (tournament.status != TournamentStatus.CANCELLED && tournament.status != TournamentStatus.UNSUCCESSFUL && tournament.status != TournamentStatus.COMPLETED) {
                    Button(
                        onClick = onCancelAndRefund,
                        colors = ButtonDefaults.buttonColors(containerColor = GamingRed.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("admin_cancel_match_btn_${tournament.id}")
                    ) {
                        Text("Cancel", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GamingRed)
                    }
                }
            }

            // Authorized Players Anti-Free Entry button
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = onAuditRoster,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GamingGreen.copy(alpha = 0.12f)),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, GamingGreen.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_authorized_players_btn_${tournament.id}")
            ) {
                Icon(Icons.Default.Security, contentDescription = "Authorized Players", tint = GamingGreen, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Authorized Players (${tournament.participants.count { it.isPaid && it.verificationStatus != PlayerVerificationStatus.KICKED }}/${tournament.totalSlots}) & Anti-Free Entry",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingGreen
                )
            }
        }
    }
}
