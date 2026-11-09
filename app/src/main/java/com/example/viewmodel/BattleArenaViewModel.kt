package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BattleArenaRepository
import com.example.model.AddMoneyRequest
import com.example.model.AdminAuditLog
import com.example.model.AppLanguage
import com.example.model.AppNotification
import com.example.model.GameMode
import com.example.model.IncidentReport
import com.example.model.LeaderboardEntry
import com.example.model.MatchRefundItem
import com.example.model.PaymentApprovalStatus
import com.example.model.PlatformCommissionRecord
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.model.Transaction
import com.example.model.User
import com.example.model.Wallet
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BattleArenaViewModel(
    val repository: BattleArenaRepository = BattleArenaRepository()
) : ViewModel() {

    val authRepository = repository.authRepository
    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
    val currentUser: StateFlow<User?> = repository.currentUser
    val currentWallet: StateFlow<Wallet> = repository.currentWallet
    val tournaments: StateFlow<List<Tournament>> = repository.tournaments
    val transactions: StateFlow<List<Transaction>> = repository.transactions
    val withdrawals: StateFlow<List<WithdrawalRequest>> = repository.withdrawals
    val addMoneyRequests: StateFlow<List<AddMoneyRequest>> = repository.addMoneyRequests
    val commissions: StateFlow<List<PlatformCommissionRecord>> = repository.commissions
    val leaderboard: StateFlow<List<LeaderboardEntry>> = repository.leaderboard
    val notifications: StateFlow<List<AppNotification>> = repository.notifications
    val incidentReports: StateFlow<List<IncidentReport>> = repository.incidentReports
    val refundRecords: StateFlow<List<MatchRefundItem>> = repository.refundRecords
    val adminAuditLogs: StateFlow<List<AdminAuditLog>> = repository.adminAuditLogs
    val adminWallet: StateFlow<com.example.model.AdminWallet> = repository.adminWallet
    val adminWithdrawals: StateFlow<List<com.example.model.AdminWithdrawalRecord>> = repository.adminWithdrawals
    val adminLedger: StateFlow<List<com.example.model.AdminLedgerEntry>> = repository.adminLedger
    val matchResults: StateFlow<List<com.example.model.MatchResultRecord>> = repository.matchResults
    val allPlayers: StateFlow<List<User>> = repository.allPlayers
    val fcmToken: StateFlow<String> = repository.fcmToken
    val tournamentRemindersEnabled: StateFlow<Boolean> = repository.tournamentRemindersEnabled
    val matchStartsEnabled: StateFlow<Boolean> = repository.matchStartsEnabled
    val prizeAlertsEnabled: StateFlow<Boolean> = repository.prizeAlertsEnabled

    private val _showNotifications = MutableStateFlow(false)
    val showNotifications: StateFlow<Boolean> = _showNotifications.asStateFlow()

    fun setShowNotifications(show: Boolean) {
        _showNotifications.value = show
    }

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedModeFilter = MutableStateFlow<GameMode?>(null)
    val selectedModeFilter: StateFlow<GameMode?> = _selectedModeFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<TournamentStatus?>(null)
    val selectedStatusFilter: StateFlow<TournamentStatus?> = _selectedStatusFilter.asStateFlow()

    private val _selectedTournament = MutableStateFlow<Tournament?>(null)
    val selectedTournament: StateFlow<Tournament?> = _selectedTournament.asStateFlow()

    private val _showAddMoney = MutableStateFlow(false)
    val showAddMoney: StateFlow<Boolean> = _showAddMoney.asStateFlow()

    private val _showWithdraw = MutableStateFlow(false)
    val showWithdraw: StateFlow<Boolean> = _showWithdraw.asStateFlow()

    private val _showReferModal = MutableStateFlow(false)
    val showReferModal: StateFlow<Boolean> = _showReferModal.asStateFlow()

    private val _showCreateTournament = MutableStateFlow(false)
    val showCreateTournament: StateFlow<Boolean> = _showCreateTournament.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    private val _verifyingTournament = MutableStateFlow<Tournament?>(null)
    val verifyingTournament: StateFlow<Tournament?> = _verifyingTournament.asStateFlow()

    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    // Filtered tournaments flow
    val filteredTournaments: StateFlow<List<Tournament>> = combine(
        tournaments,
        selectedModeFilter,
        selectedStatusFilter
    ) { list, mode, status ->
        list.filter { t ->
            val matchMode = (mode == null || t.mode == mode)
            val matchStatus = (status == null || t.status == status)
            matchMode && matchStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun selectModeFilter(mode: GameMode?) {
        _selectedModeFilter.value = mode
    }

    fun selectStatusFilter(status: TournamentStatus?) {
        _selectedStatusFilter.value = status
    }

    fun openTournamentDetails(tournament: Tournament) {
        tickCountdowns()
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournament.id } ?: tournament
    }

    fun closeTournamentDetails() {
        _selectedTournament.value = null
    }

    init {
        viewModelScope.launch {
            authRepository.currentFirebaseUser?.let { fbUser ->
                repository.restoreUserFromCloud(fbUser.uid)
            }
        }
    }

    fun setShowAddMoney(show: Boolean) {
        _showAddMoney.value = show
    }

    fun setShowWithdraw(show: Boolean) {
        _showWithdraw.value = show
    }

    fun setShowReferModal(show: Boolean) {
        _showReferModal.value = show
    }

    fun setShowCreateTournament(show: Boolean) {
        _showCreateTournament.value = show
    }

    fun setShowAuthDialog(show: Boolean) {
        _showAuthDialog.value = show
    }

    fun onAuthSuccess(user: User) {
        val restoredWallet = authRepository.lastRestoredWallet.value
        val restoredTxs = authRepository.lastRestoredTransactions.value
        repository.restorePlayerSession(user, restoredWallet, restoredTxs)
        _userFeedback.value = "Welcome back, ${user.name}! Player ID: ${user.id} restored from cloud."
    }

    fun signOut() {
        repository.signOut()
        _userFeedback.value = "Logged out successfully."
    }

    fun openVerifyTournament(tournament: Tournament?) {
        _verifyingTournament.value = tournament
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }

    fun initLanguage(context: android.content.Context) {
        repository.initLanguagePreference(context)
    }

    fun toggleLanguage(context: android.content.Context? = null) {
        repository.toggleLanguage(context)
    }

    fun setLanguage(lang: AppLanguage, context: android.content.Context? = null) {
        repository.setLanguage(lang, context)
    }

    fun toggleAdminMode() {
        repository.toggleAdminMode()
        val isAdmin = repository.currentUser.value?.isAdmin == true
        _userFeedback.value = if (isAdmin) "Admin Mode Activated" else "Admin Mode Deactivated"
    }

    fun updateProfile(name: String, ffName: String, ffUid: String, phone: String, avatarId: Int) {
        repository.updateProfile(name, ffName, ffUid, phone, avatarId)
        _userFeedback.value = "Profile updated successfully!"
    }

    fun addMoney(amount: Int, method: String) {
        val success = repository.addMoney(amount, method)
        if (success) {
            _showAddMoney.value = false
            _userFeedback.value = "₹$amount successfully added to your wallet!"
        }
    }

    fun requestWithdrawal(amount: Int, method: String, destination: String) {
        val (success, message) = repository.requestWithdrawal(amount, method, destination)
        _userFeedback.value = message
        if (success) {
            _showWithdraw.value = false
        }
    }

    fun applyReferralCode(code: String) {
        val (success, message) = repository.applyReferralCode(code)
        _userFeedback.value = message
        if (success) {
            _showReferModal.value = false
        }
    }

    fun joinTournament(tournamentId: String) {
        val (success, message) = repository.joinTournament(tournamentId)
        _userFeedback.value = message
        // Keep selected tournament in sync
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun createTournament(
        titleEn: String,
        titleBn: String,
        mode: GameMode,
        entryFee: Int,
        startTime: String,
        map: String
    ) {
        val t = repository.createTournament(titleEn, titleBn, mode, entryFee, startTime, map)
        _showCreateTournament.value = false
        _userFeedback.value = "Tournament ${t.titleEn} created successfully!"
    }

    fun updateRoomCredentials(tournamentId: String, roomId: String, roomPass: String) {
        repository.updateRoomCredentials(tournamentId, roomId, roomPass)
        _userFeedback.value = "Room ID & Password updated for match!"
    }

    fun createScheduledMatch(
        titleEn: String,
        titleBn: String,
        mode: GameMode,
        matchDate: String,
        matchTimeIst: String,
        playerLimit: Int,
        entryFee: Int,
        descriptionEn: String,
        descriptionBn: String,
        mapName: String = "Bermuda"
    ) {
        val t = repository.createScheduledMatch(
            titleEn, titleBn, mode, matchDate, matchTimeIst, playerLimit, entryFee, descriptionEn, descriptionBn, mapName
        )
        _userFeedback.value = "Scheduled match ${t.id} (${t.matchTimeIst}) created successfully!"
    }

    fun createBatchSchedule(
        mode: GameMode,
        matchDate: String,
        startTimes: List<String>,
        entryFee: Int
    ) {
        val list = repository.createBatchSchedule(mode, matchDate, startTimes, entryFee)
        _userFeedback.value = "Successfully generated ${list.size} scheduled ${mode.tagEn} matches!"
    }

    fun createManualMatch(
        titleEn: String,
        titleBn: String,
        mode: GameMode,
        playerLimit: Int,
        entryFee: Int,
        descriptionEn: String,
        descriptionBn: String,
        isRegistrationOpen: Boolean,
        mapName: String = "Bermuda"
    ) {
        val t = repository.createManualMatch(
            titleEn, titleBn, mode, playerLimit, entryFee, descriptionEn, descriptionBn, isRegistrationOpen, mapName
        )
        _userFeedback.value = "Manual match ${t.id} created successfully!"
    }

    fun editMatchSchedule(
        tournamentId: String,
        newDate: String?,
        newTimeIst: String?,
        newSlots: Int? = null,
        newTitleEn: String? = null
    ) {
        val (success, msg) = repository.editMatchSchedule(tournamentId, newDate, newTimeIst, newSlots, newTitleEn)
        _userFeedback.value = msg
    }

    fun updateRoomDetails(
        tournamentId: String,
        roomId: String,
        roomPassword: String,
        roomStatus: com.example.model.RoomPublicationStatus = com.example.model.RoomPublicationStatus.PUBLISHED,
        notes: String? = null
    ) {
        val (success, msg) = repository.updateRoomDetails(tournamentId, roomId, roomPassword, roomStatus, notes)
        _userFeedback.value = msg
    }

    fun hideRoomDetails(tournamentId: String) {
        val (success, msg) = repository.hideRoomDetails(tournamentId)
        _userFeedback.value = msg
    }

    fun closeRoomAccess(tournamentId: String) {
        val (success, msg) = repository.closeRoomAccess(tournamentId)
        _userFeedback.value = msg
    }

    fun toggleMatchRegistration(tournamentId: String, isOpen: Boolean) {
        val (success, msg) = repository.toggleMatchRegistration(tournamentId, isOpen)
        _userFeedback.value = msg
    }

    fun updateMatchStatus(tournamentId: String, newStatus: TournamentStatus) {
        val (success, msg) = repository.updateMatchStatus(tournamentId, newStatus)
        _userFeedback.value = msg
    }

    fun verifyLoneWolf(tournamentId: String, winnerId: String) {
        val (ok, msg) = repository.verifyAndDistributeLoneWolf(tournamentId, winnerId)
        _verifyingTournament.value = null
        _userFeedback.value = msg
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun verifyClashSquad(tournamentId: String, winningTeam: String) {
        val (ok, msg) = repository.verifyAndDistributeClashSquad(tournamentId, winningTeam)
        _verifyingTournament.value = null
        _userFeedback.value = msg
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun verifyBattleRoyale(tournamentId: String) {
        val (ok, msg) = repository.verifyAndDistributeBattleRoyale(tournamentId)
        _verifyingTournament.value = null
        _userFeedback.value = msg
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun getSecureRoomCredentials(tournamentId: String, userId: String): Pair<String?, String?> {
        return repository.getSecureRoomCredentials(tournamentId, userId)
    }

    fun updateWithdrawalStatus(requestId: String, newStatus: WithdrawalStatus, reason: String? = null) {
        val (ok, message) = repository.updateWithdrawalStatus(requestId, newStatus, reason)
        _userFeedback.value = message
    }

    fun approveWithdrawal(requestId: String) {
        val (ok, message) = repository.approveWithdrawal(requestId)
        _userFeedback.value = message
    }

    fun markWithdrawalPaid(requestId: String, paymentUtr: String) {
        val (ok, message) = repository.markWithdrawalPaid(requestId, paymentUtr)
        _userFeedback.value = message
    }

    fun rejectWithdrawal(requestId: String, reason: String) {
        val (ok, message) = repository.rejectWithdrawal(requestId, reason)
        _userFeedback.value = message
    }

    fun failWithdrawal(requestId: String, reason: String) {
        val (ok, message) = repository.failWithdrawal(requestId, reason)
        _userFeedback.value = message
    }

    fun submitAddMoneyRequest(
        amount: Int,
        paymentMethod: String,
        utrNumber: String,
        screenshotNote: String = "Screenshot Submitted"
    ) {
        val (success, message) = repository.submitAddMoneyRequest(amount, paymentMethod, utrNumber, screenshotNote)
        _userFeedback.value = message
        if (success) {
            _showAddMoney.value = false
        }
    }

    fun approveAddMoneyRequest(requestId: String) {
        val (success, message) = repository.approveAddMoneyRequest(requestId)
        _userFeedback.value = message
    }

    fun rejectAddMoneyRequest(requestId: String, reason: String) {
        val (success, message) = repository.rejectAddMoneyRequest(requestId, reason)
        _userFeedback.value = message
    }

    fun submitLevelVerification(level: Int, note: String = "ID Proof Uploaded") {
        val (success, message) = repository.submitLevelVerification(level, note)
        _userFeedback.value = message
    }

    fun banPlayer(userId: String, reason: String) {
        val (_, message) = repository.banPlayer(userId, reason)
        _userFeedback.value = message
    }

    fun permanentlyBanPlayer(
        targetPlayerId: String,
        reason: String,
        adminPerformer: String? = null
    ): Pair<Boolean, String> {
        val result = repository.permanentlyBanPlayer(targetPlayerId, reason, adminPerformer)
        _userFeedback.value = result.second
        return result
    }

    fun suspendPlayer(
        targetPlayerId: String,
        durationHours: Int,
        reason: String,
        adminPerformer: String? = null
    ): Pair<Boolean, String> {
        val result = repository.suspendPlayer(targetPlayerId, durationHours, reason, adminPerformer)
        _userFeedback.value = result.second
        return result
    }

    fun unbanPlayer(userId: String, adminPerformer: String? = null): Pair<Boolean, String> {
        val result = repository.unbanPlayer(userId, adminPerformer)
        _userFeedback.value = result.second
        return result
    }

    fun searchPlayers(query: String): List<User> {
        return repository.searchPlayers(query)
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }

    fun tickCountdowns() {
        repository.tickCountdowns()
    }

    fun cancelLoneWolfWaitingPlayer(tournamentId: String, refundFee: Boolean = true) {
        val (_, message) = repository.cancelLoneWolfWaitingPlayer(tournamentId, refundFee)
        _userFeedback.value = message
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun updateBattleRoyaleSchedule(tournamentId: String, newScheduledTime: String) {
        val (_, message) = repository.updateBattleRoyaleSchedule(tournamentId, newScheduledTime)
        _userFeedback.value = message
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun markTournamentUnsuccessful(tournamentId: String, reason: String = "Technical or matchmaking failure") {
        val (_, message) = repository.markTournamentUnsuccessful(tournamentId, reason)
        _userFeedback.value = message
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun approveAndProcessMatchRefund(tournamentId: String, refundItemId: String? = null) {
        val (_, message) = repository.approveAndProcessMatchRefund(tournamentId, refundItemId)
        _userFeedback.value = message
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun requestAdminUPIWithdrawal(amount: Int, upiId: String, autoProcessSuccess: Boolean = true) {
        val (_, message) = repository.requestAdminUPIWithdrawal(amount, upiId, autoProcessSuccess)
        _userFeedback.value = message
    }

    fun saveOwnerVerifiedUpiId(newUpiId: String) {
        val (_, message) = repository.saveOwnerVerifiedUpiId(newUpiId)
        _userFeedback.value = message
    }

    fun failAdminWithdrawal(withdrawalId: String, reason: String = "Declined by Payment Provider") {
        val (_, message) = repository.failAdminWithdrawal(withdrawalId, reason)
        _userFeedback.value = message
    }

    fun reverseAdminWithdrawal(withdrawalId: String, reason: String = "Settlement reversal") {
        val (_, message) = repository.reverseAdminWithdrawal(withdrawalId, reason)
        _userFeedback.value = message
    }

    fun updateBrPrizeChart(tournamentId: String, newTiers: List<com.example.model.BrPrizeTier>, killPrize: Int = 110) {
        val (_, message) = repository.updateBrPrizeChart(tournamentId, newTiers, killPrize)
        _userFeedback.value = message
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun verifyMatchResult(resultId: String, verifiedRank: Int? = null, verifiedKills: Int? = null, isApproved: Boolean = true) {
        val (_, message) = repository.verifyMatchResult(resultId, verifiedRank, verifiedKills, isApproved)
        _userFeedback.value = message
    }

    fun approvePrizePayment(resultId: String) {
        val (_, message) = repository.approveAndProcessPrizePayment(resultId)
        _userFeedback.value = message
    }

    fun updateTournamentSettings(tournamentId: String, scheduledTime: String?, entryFee: Int?, totalSlots: Int?, mapName: String?) {
        val (_, message) = repository.updateBattleRoyaleSchedule(
            tournamentId = tournamentId,
            newScheduledTime = scheduledTime ?: "08:00 PM IST",
            entryFee = entryFee,
            totalSlots = totalSlots,
            mapName = mapName
        )
        _userFeedback.value = message
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun verifyRoomPlayer(
        tournamentId: String,
        freeFireUid: String,
        newStatus: com.example.model.PlayerVerificationStatus,
        notes: String? = null
    ) {
        val (_, message) = repository.verifyRoomPlayer(tournamentId, freeFireUid, newStatus, notes)
        _userFeedback.value = message
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun removeUnauthorizedPlayer(
        tournamentId: String,
        freeFireUid: String,
        reason: String,
        shouldRefund: Boolean = false
    ) {
        val (_, message) = repository.removeUnauthorizedPlayer(tournamentId, freeFireUid, reason, shouldRefund)
        _userFeedback.value = message
        _selectedTournament.value = repository.tournaments.value.find { it.id == tournamentId }
    }

    fun auditRoomRoster(
        tournamentId: String,
        roomPlayerUids: List<String>
    ): com.example.model.RoomRosterAuditReport {
        return repository.auditRoomRoster(tournamentId, roomPlayerUids)
    }

    fun initNotificationPreferences(context: android.content.Context) {
        repository.initNotificationPreferences(context)
    }

    fun updateNotificationPreferences(
        tournamentReminders: Boolean,
        matchStarts: Boolean,
        prizeAlerts: Boolean,
        context: android.content.Context
    ) {
        repository.updateNotificationPreferences(tournamentReminders, matchStarts, prizeAlerts, context)
    }

    fun triggerTournamentReminder(context: android.content.Context, tournament: Tournament) {
        com.example.data.BattleArenaNotificationManager.dispatchTournamentReminder(
            context = context,
            tournamentTitle = tournament.titleEn,
            tournamentId = tournament.id,
            startsIn = "15 mins",
            language = repository.currentLanguage.value
        )
        _userFeedback.value = "Push Notification sent: Tournament Registration Reminder"
    }

    fun triggerMatchStartAlert(context: android.content.Context, tournament: Tournament) {
        com.example.data.BattleArenaNotificationManager.dispatchMatchStartAlert(
            context = context,
            tournamentTitle = tournament.titleEn,
            tournamentId = tournament.id,
            roomId = tournament.roomId ?: "892019",
            roomPassword = tournament.roomPassword ?: "5566",
            language = repository.currentLanguage.value
        )
        _userFeedback.value = "Push Notification sent: Match Starting Now!"
    }

    fun triggerPrizeDistributionAlert(context: android.content.Context, tournament: Tournament, prizeAmount: Int = 100, rank: Int = 1) {
        com.example.data.BattleArenaNotificationManager.dispatchPrizeDistributionAlert(
            context = context,
            tournamentTitle = tournament.titleEn,
            prizeAmount = prizeAmount,
            rank = rank,
            language = repository.currentLanguage.value
        )
        _userFeedback.value = "Push Notification sent: Prize ₹$prizeAmount Credited!"
    }

    // ==========================================
    // FAIR PLAY & ANTI-CHEAT MONITORING
    // ==========================================

    fun reportPlayerCheat(
        matchId: String,
        targetPlayerId: String,
        category: com.example.model.CheatCategory,
        reason: String,
        evidenceText: String? = null,
        evidenceUrl: String? = null
    ): Pair<Boolean, String> {
        val result = repository.reportPlayerCheat(matchId, targetPlayerId, category, reason, evidenceText, evidenceUrl)
        _userFeedback.value = result.second
        return result
    }

    fun reviewCheatReport(
        reportId: String,
        newStatus: com.example.model.IncidentReportStatus,
        adminNotes: String,
        actionType: String? = null,
        suspensionHours: Int = 24
    ): Pair<Boolean, String> {
        val result = repository.reviewCheatReport(reportId, newStatus, adminNotes, actionType, suspensionHours)
        _userFeedback.value = result.second
        return result
    }

    fun submitPlayerAppeal(
        reportId: String,
        explanation: String,
        proofUrl: String? = null
    ): Pair<Boolean, String> {
        val result = repository.submitPlayerAppeal(reportId, explanation, proofUrl)
        _userFeedback.value = result.second
        return result
    }

    fun reviewPlayerAppeal(
        reportId: String,
        acceptAppeal: Boolean,
        reviewNotes: String
    ): Pair<Boolean, String> {
        val result = repository.reviewPlayerAppeal(reportId, acceptAppeal, reviewNotes)
        _userFeedback.value = result.second
        return result
    }

    fun flagInconsistentResult(
        matchId: String,
        userId: String,
        reason: String,
        details: String
    ) {
        repository.flagInconsistentResult(matchId, userId, reason, details)
        _userFeedback.value = "Result discrepancy flagged for Admin investigation."
    }
}

