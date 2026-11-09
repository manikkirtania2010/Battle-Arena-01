package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.data.BattleArenaNotificationManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.dialogs.LanguageSelectionDialog
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.components.AppTopBar
import com.example.ui.dialogs.AddMoneyDialog
import com.example.ui.dialogs.AuthDialog
import com.example.ui.dialogs.CreateTournamentDialog
import com.example.ui.dialogs.NotificationsDialog
import com.example.ui.dialogs.ReferModal
import com.example.ui.dialogs.ReportCheatingDialog
import com.example.ui.dialogs.SubmitAppealDialog
import com.example.ui.dialogs.VerifyResultDialog
import com.example.ui.dialogs.WithdrawDialog
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TournamentDetailsSheet
import com.example.ui.screens.TournamentsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.BattleArenaTheme
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.GamingPrimary
import com.example.ui.theme.GamingSecondary
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BattleArenaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BattleArenaTheme {
                BattleArenaApp()
            }
        }
    }
}

@Composable
fun BattleArenaApp(viewModel: BattleArenaViewModel = viewModel()) {
    val language by viewModel.currentLanguage.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val wallet by viewModel.currentWallet.collectAsState()
    val tournaments by viewModel.filteredTournaments.collectAsState()
    val allTournaments by viewModel.tournaments.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()
    val commissions by viewModel.commissions.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val addMoneyRequests by viewModel.addMoneyRequests.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val incidentReports by viewModel.incidentReports.collectAsState()
    val refundRecords by viewModel.refundRecords.collectAsState()
    val adminAuditLogs by viewModel.adminAuditLogs.collectAsState()
    val adminWallet by viewModel.adminWallet.collectAsState()
    val adminWithdrawals by viewModel.adminWithdrawals.collectAsState()
    val adminLedger by viewModel.adminLedger.collectAsState()
    val matchResults by viewModel.matchResults.collectAsState()
    val allPlayers by viewModel.allPlayers.collectAsState()
    val fcmToken by viewModel.fcmToken.collectAsState()
    val tournamentRemindersEnabled by viewModel.tournamentRemindersEnabled.collectAsState()
    val matchStartsEnabled by viewModel.matchStartsEnabled.collectAsState()
    val prizeAlertsEnabled by viewModel.prizeAlertsEnabled.collectAsState()

    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedMode by viewModel.selectedModeFilter.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
    val selectedTournament by viewModel.selectedTournament.collectAsState()

    val showAddMoney by viewModel.showAddMoney.collectAsState()
    val showWithdraw by viewModel.showWithdraw.collectAsState()
    val showReferModal by viewModel.showReferModal.collectAsState()
    val showCreateTournament by viewModel.showCreateTournament.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()
    val showNotifications by viewModel.showNotifications.collectAsState()
    val verifyingTournament by viewModel.verifyingTournament.collectAsState()
    val userFeedback by viewModel.userFeedback.collectAsState()

    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var reportCheatingTargetTournament by remember { mutableStateOf<com.example.model.Tournament?>(null) }
    var reportCheatingPrefilledPlayerId by remember { mutableStateOf<String?>(null) }
    var showAppealDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    // Initialize persisted language preference, notification preferences, and FCM
    LaunchedEffect(Unit) {
        viewModel.initLanguage(context)
        viewModel.initNotificationPreferences(context)
        BattleArenaNotificationManager.initFcm(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Periodic ticker for 45s countdown synchronization and match status updates
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000L)
            viewModel.tickCountdowns()
        }
    }

    // Reconcile countdowns and match status immediately when app resumes from background or phone unlock
    androidx.lifecycle.compose.LifecycleEventEffect(androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
        viewModel.tickCountdowns()
    }

    // Handle user feedback message
    LaunchedEffect(userFeedback) {
        userFeedback?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    // Predictive Back Handling
    BackHandler(enabled = selectedTournament != null || selectedTab != 0) {
        if (selectedTournament != null) {
            viewModel.closeTournamentDetails()
        } else if (selectedTab != 0) {
            viewModel.selectTab(0)
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(GamingDarkBackground),
            topBar = {
                AppTopBar(
                    user = user,
                    wallet = wallet,
                    language = language,
                    onLanguageToggle = { showLanguageDialog = true },
                onWalletClick = { viewModel.selectTab(1) },
                onAdminToggle = {
                    if (user?.isAdmin == true) {
                        viewModel.selectTab(4)
                    } else {
                        viewModel.toggleAdminMode()
                    }
                },
                unreadNotificationCount = notifications.count { !it.isRead },
                onNotificationsClick = { viewModel.setShowNotifications(true) }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .border(androidx.compose.foundation.BorderStroke(1.dp, GamingBorder))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = GamingSurface,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Tournaments
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.SportsEsports else Icons.Outlined.SportsEsports,
                            contentDescription = AppStrings.navTournaments(language),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.navTournaments(language),
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GamingPrimary,
                        selectedTextColor = GamingPrimary,
                        indicatorColor = GamingPrimary.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tournaments")
                )

                // Tab 1: Wallet
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = AppStrings.navWallet(language),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.navWallet(language),
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GamingGold,
                        selectedTextColor = GamingGold,
                        indicatorColor = GamingGold.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_wallet")
                )

                // Tab 2: Leaderboard
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                            contentDescription = AppStrings.navLeaderboard(language),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.navLeaderboard(language),
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GamingSecondary,
                        selectedTextColor = GamingSecondary,
                        indicatorColor = GamingSecondary.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_leaderboard")
                )

                // Tab 3: Profile
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = AppStrings.navProfile(language),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.navProfile(language),
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GamingGreen,
                        selectedTextColor = GamingGreen,
                        indicatorColor = GamingGreen.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_profile")
                )

                // Tab 4: Admin (Available when user is admin or enabled)
                if (user?.isAdmin == true) {
                    val pendingCount = withdrawals.count { it.status == com.example.model.WithdrawalStatus.PENDING }
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { viewModel.selectTab(4) },
                        icon = {
                            if (pendingCount > 0) {
                                BadgedBox(badge = { Badge { Text("$pendingCount") } }) {
                                    Icon(
                                        imageVector = if (selectedTab == 4) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                        contentDescription = AppStrings.navAdmin(language),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (selectedTab == 4) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                    contentDescription = AppStrings.navAdmin(language),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = AppStrings.navAdmin(language),
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GamingPrimary,
                            selectedTextColor = GamingPrimary,
                            indicatorColor = GamingPrimary.copy(alpha = 0.2f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_admin")
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = GamingSurface,
                    contentColor = TextPrimary,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TournamentsScreen(
                    tournaments = tournaments,
                    currentUserId = user?.id,
                    language = language,
                    selectedMode = selectedMode,
                    selectedStatus = selectedStatus,
                    onSelectMode = { viewModel.selectModeFilter(it) },
                    onSelectStatus = { viewModel.selectStatusFilter(it) },
                    onTournamentClick = { viewModel.openTournamentDetails(it) },
                    onJoinTournament = { viewModel.joinTournament(it) },
                    onReferClick = { viewModel.setShowReferModal(true) },
                    onTick = { viewModel.tickCountdowns() }
                )

                1 -> WalletScreen(
                    wallet = wallet,
                    transactions = transactions,
                    withdrawals = withdrawals,
                    language = language,
                    onAddMoneyClick = { viewModel.setShowAddMoney(true) },
                    onWithdrawClick = { viewModel.setShowWithdraw(true) }
                )

                2 -> LeaderboardScreen(
                    leaderboard = leaderboard,
                    currentUserId = user?.id,
                    language = language
                )

                3 -> ProfileScreen(
                    user = user,
                    language = language,
                    onLanguageToggle = { showLanguageDialog = true },
                    onReferClick = { viewModel.setShowReferModal(true) },
                    onAdminToggle = { viewModel.toggleAdminMode() },
                    onLoginClick = { viewModel.setShowAuthDialog(true) },
                    onSignOutClick = { viewModel.signOut() },
                    onUpdateProfile = { name, ffName, ffUid, phone, avatarId ->
                        viewModel.updateProfile(name, ffName, ffUid, phone, avatarId)
                    },
                    onSubmitLevelVerification = { level, note ->
                        viewModel.submitLevelVerification(level, note)
                    },
                    tournamentRemindersEnabled = tournamentRemindersEnabled,
                    matchStartsEnabled = matchStartsEnabled,
                    prizeAlertsEnabled = prizeAlertsEnabled,
                    fcmToken = fcmToken,
                    onUpdateNotificationPreferences = { rem, match, prize ->
                        viewModel.updateNotificationPreferences(rem, match, prize, context)
                    },
                    onSendTestPushNotification = { type ->
                        val tourney = allTournaments.firstOrNull()
                        if (tourney != null) {
                            when (type) {
                                "REMINDER" -> viewModel.triggerTournamentReminder(context, tourney)
                                "MATCH_START" -> viewModel.triggerMatchStartAlert(context, tourney)
                                "PRIZE" -> viewModel.triggerPrizeDistributionAlert(context, tourney, 50, 1)
                            }
                        }
                    },
                    onAppealClick = { showAppealDialog = true }
                )

                4 -> AdminScreen(
                    tournaments = allTournaments,
                    withdrawals = withdrawals,
                    commissions = commissions,
                    addMoneyRequests = addMoneyRequests,
                    incidentReports = incidentReports,
                    refundRecords = refundRecords,
                    adminAuditLogs = adminAuditLogs,
                    adminWallet = adminWallet,
                    adminWithdrawals = adminWithdrawals,
                    adminLedger = adminLedger,
                    matchResults = matchResults,
                    isOwner = user?.isOwner == true,
                    language = language,
                    onCreateTournamentClick = { viewModel.setShowCreateTournament(true) },
                    onVerifyTournamentClick = { viewModel.openVerifyTournament(it) },
                    onUpdateRoomCredentials = { id, room, pass ->
                        viewModel.updateRoomCredentials(id, room, pass)
                    },
                    onUpdateWithdrawalStatus = { reqId, status, reason ->
                        viewModel.updateWithdrawalStatus(reqId, status, reason)
                    },
                    onApproveAddMoney = { reqId ->
                        viewModel.approveAddMoneyRequest(reqId)
                    },
                    onRejectAddMoney = { reqId, reason ->
                        viewModel.rejectAddMoneyRequest(reqId, reason)
                    },
                    allPlayers = allPlayers,
                    onBanPlayer = { userId, reason ->
                        viewModel.banPlayer(userId, reason)
                    },
                    onSuspendPlayer = { id, hours, reason ->
                        viewModel.suspendPlayer(id, hours, reason)
                    },
                    onPermanentlyBanPlayer = { id, reason ->
                        viewModel.permanentlyBanPlayer(id, reason)
                    },
                    onUnbanPlayer = { userId ->
                        viewModel.unbanPlayer(userId)
                    },
                    onMarkTournamentUnsuccessful = { id, reason ->
                        viewModel.markTournamentUnsuccessful(id, reason)
                    },
                    onApproveMatchRefund = { tId, rId ->
                        viewModel.approveAndProcessMatchRefund(tId, rId)
                    },
                    onUpdateBattleRoyaleSchedule = { id, time ->
                        viewModel.updateBattleRoyaleSchedule(id, time)
                    },
                    onRequestAdminUPIWithdrawal = { amount, upi, autoSuccess ->
                        viewModel.requestAdminUPIWithdrawal(amount, upi, autoSuccess)
                    },
                    onSaveOwnerVerifiedUpiId = { upi ->
                        viewModel.saveOwnerVerifiedUpiId(upi)
                    },
                    onFailAdminWithdrawal = { id, reason ->
                        viewModel.failAdminWithdrawal(id, reason)
                    },
                    onReverseAdminWithdrawal = { id, reason ->
                        viewModel.reverseAdminWithdrawal(id, reason)
                    },
                    onUpdateBrPrizeChart = { id, tiers, killPool ->
                        viewModel.updateBrPrizeChart(id, tiers, killPool)
                    },
                    onVerifyMatchResult = { id, rank, kills, approve ->
                        viewModel.verifyMatchResult(id, rank, kills, approve)
                    },
                    onApprovePrizePayment = { id ->
                        viewModel.approvePrizePayment(id)
                    },
                    onUpdateTournamentSettings = { id, time, fee, slots, map ->
                        viewModel.updateTournamentSettings(id, time, fee, slots, map)
                    },
                    onVerifyRoomPlayer = { tId, ffUid, status, notes ->
                        viewModel.verifyRoomPlayer(tId, ffUid, status, notes)
                    },
                    onRemoveUnauthorizedPlayer = { tId, ffUid, reason, refund ->
                        viewModel.removeUnauthorizedPlayer(tId, ffUid, reason, refund)
                    },
                    onAuditRoomRoster = { tId, uids ->
                        viewModel.auditRoomRoster(tId, uids)
                    },
                    onCreateScheduledMatch = { titleEn, titleBn, mode, date, time, slots, fee, descEn, descBn, map ->
                        viewModel.createScheduledMatch(titleEn, titleBn, mode, date, time, slots, fee, descEn, descBn, map)
                    },
                    onGenerateBatchSchedule = { mode, date, times, fee ->
                        viewModel.createBatchSchedule(mode, date, times, fee)
                    },
                    onCreateManualMatch = { titleEn, titleBn, mode, slots, fee, descEn, descBn, isRegOpen, map ->
                        viewModel.createManualMatch(titleEn, titleBn, mode, slots, fee, descEn, descBn, isRegOpen, map)
                    },
                    onEditMatchSchedule = { tId, newDate, newTime, newSlots, newTitle ->
                        viewModel.editMatchSchedule(tId, newDate, newTime, newSlots, newTitle)
                    },
                    onPublishRoomDetails = { tId, roomId, roomPass, notes ->
                        viewModel.updateRoomDetails(tId, roomId, roomPass, com.example.model.RoomPublicationStatus.PUBLISHED, notes)
                    },
                    onHideRoomDetails = { tId ->
                        viewModel.hideRoomDetails(tId)
                    },
                    onCloseRoomAccess = { tId ->
                        viewModel.closeRoomAccess(tId)
                    },
                    onToggleMatchRegistration = { tId, isOpen ->
                        viewModel.toggleMatchRegistration(tId, isOpen)
                    },
                    onUpdateMatchStatus = { tId, status ->
                        viewModel.updateMatchStatus(tId, status)
                    },
                    onReviewCheatReport = { reportId, status, notes, action, hours ->
                        viewModel.reviewCheatReport(reportId, status, notes, action, hours)
                    },
                    onReviewPlayerAppeal = { reportId, accept, notes ->
                        viewModel.reviewPlayerAppeal(reportId, accept, notes)
                    }
                )
            }
        }
    }
    }

    // Modal: Tournament Details & Room Password Unlocking
    selectedTournament?.let { tournament ->
        val isJoined = user != null && tournament.isUserJoined(user!!.id)
        TournamentDetailsSheet(
            tournament = tournament,
            isJoined = isJoined,
            currentUserId = user?.id,
            language = language,
            onDismiss = { viewModel.closeTournamentDetails() },
            onJoin = { viewModel.joinTournament(tournament.id) },
            onCancelWaitingPlayer = { viewModel.cancelLoneWolfWaitingPlayer(it) },
            onReportCheater = { tourney, targetId ->
                reportCheatingTargetTournament = tourney
                reportCheatingPrefilledPlayerId = targetId
            },
            onFetchSecureRoomCredentials = { tourneyId, userId ->
                viewModel.getSecureRoomCredentials(tourneyId, userId)
            }
        )
    }

    // Modal: Report Suspected Cheating / Fair Play Violation
    reportCheatingTargetTournament?.let { tournament ->
        ReportCheatingDialog(
            tournament = tournament,
            language = language,
            prefilledTargetPlayerId = reportCheatingPrefilledPlayerId,
            onDismiss = {
                reportCheatingTargetTournament = null
                reportCheatingPrefilledPlayerId = null
            },
            onSubmitReport = { matchId, targetId, category, reason, evidenceText, evidenceUrl ->
                viewModel.reportPlayerCheat(
                    matchId = matchId,
                    targetPlayerId = targetId,
                    category = category,
                    reason = reason,
                    evidenceText = evidenceText,
                    evidenceUrl = evidenceUrl
                )
            }
        )
    }

    // Modal: Submit Player Appeal
    if (showAppealDialog) {
        SubmitAppealDialog(
            user = user,
            language = language,
            onDismiss = { showAppealDialog = false },
            onSubmitAppeal = { reportId, explanation, proofUrl ->
                viewModel.submitPlayerAppeal(reportId, explanation, proofUrl)
            }
        )
    }

    // Modal: Add Money Dialog (With UTR verification)
    if (showAddMoney) {
        AddMoneyDialog(
            language = language,
            onDismiss = { viewModel.setShowAddMoney(false) },
            onSubmitRequest = { amount, method, utr, note ->
                viewModel.submitAddMoneyRequest(amount, method, utr, note)
            }
        )
    }

    // Modal: Withdraw Dialog
    if (showWithdraw) {
        WithdrawDialog(
            wallet = wallet,
            language = language,
            onDismiss = { viewModel.setShowWithdraw(false) },
            onWithdraw = { amount, method, dest ->
                viewModel.requestWithdrawal(amount, method, dest)
            }
        )
    }

    // Modal: Refer & Earn
    if (showReferModal) {
        ReferModal(
            referralCode = user?.referralCode ?: "ARENA99",
            referredBy = user?.referredBy,
            language = language,
            onDismiss = { viewModel.setShowReferModal(false) },
            onApplyCode = { code ->
                viewModel.applyReferralCode(code)
            }
        )
    }

    // Modal: Admin Create Tournament
    if (showCreateTournament) {
        CreateTournamentDialog(
            language = language,
            onDismiss = { viewModel.setShowCreateTournament(false) },
            onCreate = { titleEn, titleBn, mode, entryFee, startTime, map ->
                viewModel.createTournament(titleEn, titleBn, mode, entryFee, startTime, map)
            }
        )
    }

    // Modal: Admin Result Verification & Automatic Prize Distribution
    verifyingTournament?.let { tournament ->
        VerifyResultDialog(
            tournament = tournament,
            language = language,
            onDismiss = { viewModel.openVerifyTournament(null) },
            onVerifyLoneWolf = { winnerId ->
                viewModel.verifyLoneWolf(tournament.id, winnerId)
            },
            onVerifyClashSquad = { winningTeam ->
                viewModel.verifyClashSquad(tournament.id, winningTeam)
            },
            onVerifyBattleRoyale = {
                viewModel.verifyBattleRoyale(tournament.id)
            }
        )
    }

    // Modal: Gaming Login Screen Component (Google & Email/Password Auth)
    if (showAuthDialog) {
        Dialog(
            onDismissRequest = { viewModel.setShowAuthDialog(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            LoginScreen(
                authRepository = viewModel.authRepository,
                language = language,
                onLanguageToggle = { showLanguageDialog = true },
                onAuthSuccess = { loggedInUser ->
                    viewModel.onAuthSuccess(loggedInUser)
                    viewModel.setShowAuthDialog(false)
                },
                onContinueAsGuest = {
                    viewModel.setShowAuthDialog(false)
                },
                onDismiss = { viewModel.setShowAuthDialog(false) }
            )
        }
    }

    // Modal: Real-Time Notifications
    if (showNotifications) {
        NotificationsDialog(
            notifications = notifications,
            language = language,
            onDismiss = { viewModel.setShowNotifications(false) },
            onMarkRead = { viewModel.markNotificationRead(it) }
        )
    }

    // Modal: 13 Indian Languages Selection Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = language,
            onSelectLanguage = { newLang ->
                viewModel.setLanguage(newLang, context)
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}
