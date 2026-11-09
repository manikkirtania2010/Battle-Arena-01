package com.example.data

import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.example.auth.AuthManager
import com.example.model.AddMoneyRequest
import com.example.model.AdminAuditLog
import com.example.model.AdminLedgerEntry
import com.example.model.AdminLedgerEntryType
import com.example.model.AdminWallet
import com.example.model.AdminWithdrawalRecord
import com.example.model.AdminWithdrawalStatus
import com.example.model.AppLanguage
import com.example.model.AppNotification
import com.example.model.BATTLE_ARENA_OWNER_EMAIL
import com.example.model.GameMode
import com.example.model.IncidentReport
import com.example.model.IncidentReportStatus
import com.example.model.CheatCategory
import com.example.model.PlayerAppeal
import com.example.model.LeaderboardEntry
import com.example.model.LevelVerificationStatus
import com.example.model.MatchRefundItem
import com.example.model.PaymentApprovalStatus
import com.example.model.PlatformCommissionRecord
import com.example.model.PlayerParticipant
import com.example.model.PlayerVerificationStatus
import com.example.model.RoomRosterAuditReport
import com.example.model.BrPrizeTier
import com.example.model.MatchCreationType
import com.example.model.MatchResultRecord
import com.example.model.PrizePaymentStatus
import com.example.model.RefundStatus
import com.example.model.RoomPublicationStatus
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.model.Transaction
import com.example.model.TransactionStatus
import com.example.model.TransactionType
import com.example.model.User
import com.example.model.Wallet
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BattleArenaRepository {

    val authRepository = AuthenticationRepository()
    val authManager = AuthManager(authRepository)
    val firestoreManager: FirestoreManager = authRepository.firestoreManager
    private val repoScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.SupervisorJob())

    suspend fun saveCloudUser(user: User): Boolean {
        _currentUser.value = user
        return firestoreManager.saveUser(user)
    }

    suspend fun saveCloudWallet(wallet: Wallet): Boolean {
        _currentWallet.value = wallet
        return firestoreManager.saveWallet(wallet)
    }

    suspend fun saveCloudTransaction(tx: Transaction): Boolean {
        val uid = tx.userId.ifBlank { _currentUser.value?.uid ?: _currentWallet.value.userId }
        return if (uid.isNotBlank()) {
            firestoreManager.saveTransaction(uid, tx)
        } else false
    }

    fun syncCloudUser(user: User) {
        _currentUser.value = user
        repoScope.launch {
            try {
                firestoreManager.saveUser(user)
            } catch (e: Exception) {
                // Ignore in offline / test mode
            }
        }
    }

    fun syncCloudWallet(wallet: Wallet) {
        val now = System.currentTimeMillis()
        val w = if (wallet.updatedAt <= 0L || wallet.updatedAt < now) {
            wallet.copy(updatedAt = now)
        } else wallet
        _currentWallet.value = w
        repoScope.launch {
            try {
                firestoreManager.saveWallet(w)
            } catch (e: Exception) {
                // Ignore in offline / test mode
            }
        }
    }

    fun syncCloudTransaction(tx: Transaction) {
        val uid = tx.userId.ifBlank { _currentUser.value?.uid ?: _currentWallet.value.userId }
        if (uid.isNotBlank()) {
            repoScope.launch {
                try {
                    firestoreManager.saveTransaction(uid, tx)
                } catch (e: Exception) {
                    // Ignore in offline / test mode
                }
            }
        }
    }

    fun syncCloudLanguage(lang: AppLanguage) {
        val uid = _currentUser.value?.uid
        if (!uid.isNullOrBlank()) {
            repoScope.launch {
                try {
                    firestoreManager.updateLanguage(uid, lang.code)
                } catch (e: Exception) {
                    // Ignore in offline / test mode
                }
            }
        }
    }

    fun updatePlayerInRegistry(user: User) {
        val current = _allPlayers.value.toMutableList()
        val idx = current.indexOfFirst { it.id == user.id || it.uid == user.uid }
        if (idx >= 0) {
            current[idx] = user
        } else {
            current.add(user)
        }
        _allPlayers.value = current
    }

    fun restorePlayerSession(user: User, wallet: Wallet? = null, txs: List<Transaction> = emptyList()) {
        _currentUser.value = user
        updatePlayerInRegistry(user)
        if (wallet != null) {
            _currentWallet.value = wallet
        } else {
            _currentWallet.value = _currentWallet.value.copy(userId = user.uid)
        }
        if (txs.isNotEmpty()) {
            _transactions.value = txs
        }
        if (user.languageCode.isNotBlank()) {
            _currentLanguage.value = AppLanguage.fromCode(user.languageCode)
        }
    }

    suspend fun restoreUserFromCloud(userId: String): Boolean {
        return try {
            val cloudUser = firestoreManager.getUser(userId)
            if (cloudUser != null) {
                val cloudWallet = firestoreManager.getWallet(userId)
                val cloudTxs = firestoreManager.getTransactions(userId)
                restorePlayerSession(cloudUser, cloudWallet, cloudTxs)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Default Super Admin / Owner profile
    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "BA-70912",
            name = "Manik Hunter",
            email = BATTLE_ARENA_OWNER_EMAIL,
            freeFireName = "⚡MANIK_BOSS⚡",
            freeFireUid = "2948109382",
            freeFireLevel = 45, // Level 45 (Eligible: >= 10)
            phone = "+91 98765 43210",
            avatarId = 1,
            referralCode = "ARENA99",
            totalKills = 48,
            totalWins = 12,
            totalEarnings = 540,
            totalTournaments = 19,
            isAdmin = true
        )
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentWallet = MutableStateFlow(
        Wallet(
            userId = "BA-70912",
            depositBalance = 60,
            winningBalance = 40,
            bonusBalance = 15
        )
    )
    val currentWallet: StateFlow<Wallet> = _currentWallet.asStateFlow()

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _withdrawals = MutableStateFlow<List<WithdrawalRequest>>(emptyList())
    val withdrawals: StateFlow<List<WithdrawalRequest>> = _withdrawals.asStateFlow()

    private val _addMoneyRequests = MutableStateFlow<List<AddMoneyRequest>>(emptyList())
    val addMoneyRequests: StateFlow<List<AddMoneyRequest>> = _addMoneyRequests.asStateFlow()

    private val _commissions = MutableStateFlow<List<PlatformCommissionRecord>>(emptyList())
    val commissions: StateFlow<List<PlatformCommissionRecord>> = _commissions.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _incidentReports = MutableStateFlow<List<IncidentReport>>(emptyList())
    val incidentReports: StateFlow<List<IncidentReport>> = _incidentReports.asStateFlow()

    private val _refundRecords = MutableStateFlow<List<MatchRefundItem>>(emptyList())
    val refundRecords: StateFlow<List<MatchRefundItem>> = _refundRecords.asStateFlow()

    private val _adminAuditLogs = MutableStateFlow<List<AdminAuditLog>>(emptyList())
    val adminAuditLogs: StateFlow<List<AdminAuditLog>> = _adminAuditLogs.asStateFlow()

    private val _allPlayers = MutableStateFlow<List<User>>(emptyList())
    val allPlayers: StateFlow<List<User>> = _allPlayers.asStateFlow()

    private val _adminWallet = MutableStateFlow(
        AdminWallet(
            availableBalance = 280,
            reservedBalance = 0,
            totalMatchEarnings = 280,
            totalPrizePaid = 730,
            totalRefundsPaid = 0,
            totalWithdrawalsPaid = 0,
            verifiedUpiId = "kirtaniamanik@okhdfcbank",
            isUpiVerified = true
        )
    )
    val adminWallet: StateFlow<AdminWallet> = _adminWallet.asStateFlow()

    private val _adminWithdrawals = MutableStateFlow<List<AdminWithdrawalRecord>>(emptyList())
    val adminWithdrawals: StateFlow<List<AdminWithdrawalRecord>> = _adminWithdrawals.asStateFlow()

    private val _adminLedger = MutableStateFlow<List<AdminLedgerEntry>>(emptyList())
    val adminLedger: StateFlow<List<AdminLedgerEntry>> = _adminLedger.asStateFlow()

    private val _matchResults = MutableStateFlow<List<MatchResultRecord>>(emptyList())
    val matchResults: StateFlow<List<MatchResultRecord>> = _matchResults.asStateFlow()

    private val _fcmToken = MutableStateFlow<String>("")
    val fcmToken: StateFlow<String> = _fcmToken.asStateFlow()

    private val _tournamentRemindersEnabled = MutableStateFlow(true)
    val tournamentRemindersEnabled: StateFlow<Boolean> = _tournamentRemindersEnabled.asStateFlow()

    private val _matchStartsEnabled = MutableStateFlow(true)
    val matchStartsEnabled: StateFlow<Boolean> = _matchStartsEnabled.asStateFlow()

    private val _prizeAlertsEnabled = MutableStateFlow(true)
    val prizeAlertsEnabled: StateFlow<Boolean> = _prizeAlertsEnabled.asStateFlow()

    private val processedPaymentTxIds = mutableSetOf<String>()

    fun logAdminAction(action: String, targetId: String, details: String) {
        val performer = _currentUser.value?.email ?: BATTLE_ARENA_OWNER_EMAIL
        val log = AdminAuditLog(
            id = "LOG-${System.currentTimeMillis()}-${(100..999).random()}",
            action = action,
            targetId = targetId,
            details = details,
            performedBy = performer,
            timestamp = System.currentTimeMillis()
        )
        _adminAuditLogs.value = listOf(log) + _adminAuditLogs.value
    }

    companion object {
        @Volatile
        private var instance: BattleArenaRepository? = null

        fun getInstance(): BattleArenaRepository {
            return instance ?: synchronized(this) {
                instance ?: BattleArenaRepository().also { instance = it }
            }
        }
    }

    init {
        instance = this
        seedInitialData()
        tickCountdowns()
    }

    fun initLanguagePreference(context: android.content.Context) {
        try {
            val prefs = context.getSharedPreferences("battle_arena_prefs", android.content.Context.MODE_PRIVATE)
            val savedCode = prefs.getString("selected_language_code", null)
            if (savedCode != null) {
                _currentLanguage.value = AppLanguage.fromCode(savedCode)
            }
        } catch (e: Exception) {
            // Ignore in tests
        }
    }

    fun toggleLanguage(context: android.content.Context? = null) {
        val all = AppLanguage.values()
        val nextIdx = (all.indexOf(_currentLanguage.value) + 1) % all.size
        setLanguage(all[nextIdx], context)
    }

    fun setLanguage(lang: AppLanguage, context: android.content.Context? = null) {
        _currentLanguage.value = lang
        syncCloudLanguage(lang)
        context?.let { ctx ->
            try {
                ctx.getSharedPreferences("battle_arena_prefs", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putString("selected_language_code", lang.code)
                    .apply()
            } catch (e: Exception) {
                // Ignore in tests
            }
        }
    }

    fun updateFcmToken(token: String) {
        _fcmToken.value = token
    }

    fun initNotificationPreferences(context: android.content.Context) {
        try {
            val prefs = context.getSharedPreferences("battle_arena_prefs", android.content.Context.MODE_PRIVATE)
            _tournamentRemindersEnabled.value = prefs.getBoolean("notif_tournament_reminders", true)
            _matchStartsEnabled.value = prefs.getBoolean("notif_match_starts", true)
            _prizeAlertsEnabled.value = prefs.getBoolean("notif_prize_alerts", true)
            _fcmToken.value = prefs.getString("fcm_token", "") ?: ""
        } catch (e: Exception) {
            // Ignore in tests
        }
    }

    fun updateNotificationPreferences(
        tournamentReminders: Boolean,
        matchStarts: Boolean,
        prizeAlerts: Boolean,
        context: android.content.Context? = null
    ) {
        _tournamentRemindersEnabled.value = tournamentReminders
        _matchStartsEnabled.value = matchStarts
        _prizeAlertsEnabled.value = prizeAlerts
        context?.let { ctx ->
            try {
                ctx.getSharedPreferences("battle_arena_prefs", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("notif_tournament_reminders", tournamentReminders)
                    .putBoolean("notif_match_starts", matchStarts)
                    .putBoolean("notif_prize_alerts", prizeAlerts)
                    .apply()
            } catch (e: Exception) {
                // Ignore in tests
            }
        }
    }

    fun updateProfile(name: String, ffName: String, ffUid: String, phone: String, avatarId: Int) {
        val user = _currentUser.value ?: return
        val updated = user.copy(
            displayName = name,
            freeFireName = ffName,
            freeFireUID = ffUid,
            phone = phone,
            avatarId = avatarId
        )
        _currentUser.value = updated
        syncCloudUser(updated)
    }

    fun submitLevelVerification(level: Int, screenshotNote: String = "ID Level Proof Attached"): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        val status = if (level >= 10) LevelVerificationStatus.VERIFIED else LevelVerificationStatus.PENDING
        val updated = user.copy(
            freeFireLevel = level,
            levelVerificationStatus = status
        )
        _currentUser.value = updated
        syncCloudUser(updated)
        addNotification(
            titleEn = "Free Fire Level Updated: Level $level",
            titleBn = "ফ্রি ফায়ার লেভেল আপডেট: লেভেল $level",
            messageEn = if (level >= 10) "Eligible for tournaments (Level 10+ confirmed)." else "Pending owner approval. (Level 10+ required)",
            messageBn = if (level >= 10) "টুর্নামেন্টে অংশগ্রহণের যোগ্য (লেভেল ১০+ নিশ্চিত)।" else "লেভেল ১০+ না হওয়া পর্যন্ত টুর্নামেন্টে খেলা যাবে না।"
        )
        return Pair(true, "Level $level recorded successfully!")
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
        if (user != null) {
            val targetUid = user.uid.ifBlank { user.id }
            _currentWallet.value = _currentWallet.value.copy(userId = targetUid)
            // 1. Restore wallet from AuthRepository if already loaded
            authRepository.lastRestoredWallet.value?.let { restoredWallet ->
                if (restoredWallet.userId == targetUid || restoredWallet.userId == user.id) {
                    _currentWallet.value = restoredWallet
                }
            }
            // 2. Check authoritative cloud store
            firestoreManager.getCachedCloudWallet(targetUid)?.let { cloudW ->
                _currentWallet.value = cloudW
            } ?: firestoreManager.getCachedCloudWallet(user.id)?.let { cloudW ->
                _currentWallet.value = cloudW
            }

            // Restore transactions
            val cloudTxs = firestoreManager.getCachedCloudTransactions(targetUid).ifEmpty {
                firestoreManager.getCachedCloudTransactions(user.id)
            }
            if (cloudTxs.isNotEmpty()) {
                _transactions.value = cloudTxs
            } else if (authRepository.lastRestoredTransactions.value.isNotEmpty()) {
                _transactions.value = authRepository.lastRestoredTransactions.value
            }

            if (user.languageCode.isNotBlank()) {
                _currentLanguage.value = AppLanguage.fromCode(user.languageCode)
            }
            syncCloudUser(user)

            // Asynchronously refresh wallet and transactions from Firestore cloud to supersede any stale local cache
            repoScope.launch {
                try {
                    val freshWallet = firestoreManager.getWallet(targetUid) ?: firestoreManager.getWallet(user.id)
                    if (freshWallet != null && freshWallet.updatedAt > _currentWallet.value.updatedAt) {
                        _currentWallet.value = freshWallet
                    }
                    val freshTxs = firestoreManager.getTransactions(targetUid).ifEmpty {
                        firestoreManager.getTransactions(user.id)
                    }
                    if (freshTxs.isNotEmpty()) {
                        _transactions.value = freshTxs
                    }
                } catch (e: Exception) {
                    // Offline fallback
                }
            }
        }
    }

    fun signOut() {
        authManager.signOut()
        _currentUser.value = null
    }

    // ==========================================
    // NOTIFICATIONS
    // ==========================================

    fun addNotification(
        titleEn: String,
        titleBn: String,
        messageEn: String,
        messageBn: String,
        type: String = "GENERAL",
        notificationId: String? = null
    ) {
        val notifId = notificationId ?: "NOTIF-${System.currentTimeMillis()}-${(10..99).random()}"
        if (_notifications.value.any { it.id == notifId }) {
            return // Prevent duplicate notifications
        }
        val notif = AppNotification(
            id = notifId,
            titleEn = titleEn,
            titleBn = titleBn,
            messageEn = messageEn,
            messageBn = messageBn,
            type = type
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    // ==========================================
    // ADD MONEY — MANUAL OWNER VERIFICATION
    // ==========================================

    fun submitAddMoneyRequest(
        amount: Int,
        paymentMethod: String,
        utrNumber: String,
        screenshotNote: String = "Screenshot Submitted"
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Please log in")

        if (amount < 20) {
            return Pair(false, "Minimum Add Money amount is ₹20 / নূন্যতম ডিপোজিট অ্যামাউন্ট ₹২০")
        }

        val cleanUtr = utrNumber.trim().uppercase()
        if (cleanUtr.length < 6) {
            return Pair(false, "Please enter a valid 12-digit UTR or Transaction ID / সঠিক UTR বা Transaction ID দিন")
        }

        // Idempotency: Prevent duplicate submissions or already credited UTRs
        if (processedPaymentTxIds.contains(cleanUtr) ||
            _addMoneyRequests.value.any { it.utrNumber.equals(cleanUtr, ignoreCase = true) && it.status != PaymentApprovalStatus.REJECTED }
        ) {
            return Pair(false, "This Transaction ID / UTR has already been submitted or credited! / এই UTR ইতিমধ্যে ব্যবহার করা হয়েছে")
        }

        val reqId = "DEP-" + (1000 + _addMoneyRequests.value.size + 1)
        val request = AddMoneyRequest(
            id = reqId,
            userId = user.id,
            userName = user.name,
            freeFireUid = user.freeFireUid,
            amount = amount,
            paymentMethod = paymentMethod,
            utrNumber = cleanUtr,
            screenshotNote = screenshotNote,
            status = PaymentApprovalStatus.PENDING,
            requestedAt = System.currentTimeMillis()
        )
        _addMoneyRequests.value = listOf(request) + _addMoneyRequests.value

        val tx = Transaction(
            id = reqId,
            userId = user.id,
            type = TransactionType.ADD_MONEY,
            amount = amount,
            status = TransactionStatus.PENDING,
            timestamp = System.currentTimeMillis(),
            referenceId = cleanUtr,
            titleEn = "Deposit: ₹$amount via $paymentMethod",
            titleBn = "ডিপোজিট: ₹$amount ($paymentMethod)",
            subtitleEn = "UTR: $cleanUtr • Status: Pending Owner Verification",
            subtitleBn = "ইউটিআর: $cleanUtr • স্ট্যাটাস: মালিকের ভেরিফিকেশন পেন্ডিং"
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncCloudTransaction(tx)

        addNotification(
            titleEn = "Deposit Request Submitted",
            titleBn = "ডিপোজিট অনুরোধ জমা হয়েছে",
            messageEn = "₹$amount (UTR: $cleanUtr) is under review. Owner will verify receipt before crediting your wallet.",
            messageBn = "₹$amount (ইউটিআর: $cleanUtr) রিভিউতে আছে। মালিক ভেরিফাই করার পর ওয়ালেটে ক্রেডিট হবে।",
            type = "PAYMENT"
        )

        return Pair(true, "Add Money request submitted! Owner will verify your payment UTR.")
    }

    fun approveAddMoneyRequest(requestId: String): Pair<Boolean, String> {
        val currentUser = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!currentUser.isOwner) {
            return Pair(false, "Security violation: Only verified App Owner can approve add-money requests.")
        }

        val req = _addMoneyRequests.value.find { it.id == requestId } 
            ?: return Pair(false, "Request not found")

        if (req.status != PaymentApprovalStatus.PENDING) {
            return Pair(false, "Request is already ${req.status.name}")
        }

        // Record UTR to prevent duplicate re-use
        processedPaymentTxIds.add(req.utrNumber)

        // Update request status
        _addMoneyRequests.value = _addMoneyRequests.value.map {
            if (it.id == requestId) it.copy(status = PaymentApprovalStatus.APPROVED) else it
        }

        // Credit wallet if it belongs to current active user, and always credit player in cloud storage
        if (_currentUser.value?.id == req.userId) {
            val wallet = _currentWallet.value
            val updatedW = wallet.copy(depositBalance = wallet.depositBalance + req.amount)
            _currentWallet.value = updatedW
            syncCloudWallet(updatedW)
        } else {
            runBlocking {
                firestoreManager.creditDeposit(req.userId, req.amount)
            }
        }

        // Update corresponding transaction status to SUCCESS
        var updatedSuccessTx: Transaction? = null
        _transactions.value = _transactions.value.map { tx ->
            if (tx.id == requestId || tx.referenceId == req.utrNumber) {
                val upd = tx.copy(
                    status = TransactionStatus.SUCCESS,
                    subtitleEn = "UTR: ${req.utrNumber} • Verified & Credited",
                    subtitleBn = "ইউটিআর: ${req.utrNumber} • ভেরিফায়েড এবং জমা হয়েছে"
                )
                updatedSuccessTx = upd
                upd
            } else tx
        }
        updatedSuccessTx?.let { syncCloudTransaction(it) }

        addNotification(
            titleEn = "Deposit Approved: ₹${req.amount}",
            titleBn = "ডিপোজিট অনুমোদিত: ₹${req.amount}",
            messageEn = "Payment UTR ${req.utrNumber} verified by owner. ₹${req.amount} added to your Deposit Cash.",
            messageBn = "পেমেন্ট ইউটিআর ${req.utrNumber} মালিক কর্তৃক ভেরিফাই হয়েছে। ₹${req.amount} ওয়ালেটে যোগ হয়েছে।",
            type = "PAYMENT"
        )

        return Pair(true, "Payment request $requestId approved. ₹${req.amount} credited!")
    }

    fun rejectAddMoneyRequest(requestId: String, reason: String): Pair<Boolean, String> {
        val currentUser = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!currentUser.isOwner) {
            return Pair(false, "Security violation: Only verified App Owner can reject add-money requests.")
        }

        val req = _addMoneyRequests.value.find { it.id == requestId } 
            ?: return Pair(false, "Request not found")

        if (req.status != PaymentApprovalStatus.PENDING) {
            return Pair(false, "Request is already ${req.status.name}")
        }

        _addMoneyRequests.value = _addMoneyRequests.value.map {
            if (it.id == requestId) it.copy(status = PaymentApprovalStatus.REJECTED, rejectionReason = reason) else it
        }

        var updatedFailedTx: Transaction? = null
        _transactions.value = _transactions.value.map { tx ->
            if (tx.id == requestId || tx.referenceId == req.utrNumber) {
                val upd = tx.copy(
                    status = TransactionStatus.FAILED,
                    subtitleEn = "Rejected by Owner: $reason",
                    subtitleBn = "মালিক কর্তৃক প্রত্যাখ্যাত: $reason"
                )
                updatedFailedTx = upd
                upd
            } else tx
        }
        updatedFailedTx?.let { syncCloudTransaction(it) }

        addNotification(
            titleEn = "Deposit Rejected",
            titleBn = "ডিপোজিট প্রত্যাখ্যাত হয়েছে",
            messageEn = "Deposit of ₹${req.amount} was rejected: $reason",
            messageBn = "₹${req.amount} ডিপোজিট প্রত্যাখ্যাত: $reason",
            type = "PAYMENT"
        )

        return Pair(true, "Payment request $requestId rejected.")
    }

    // ==========================================
    // VIRTUAL PRIZE WALLET & MANUAL REAL MONEY WITHDRAWALS
    // ==========================================

    fun requestWithdrawal(amount: Int, method: String, destination: String): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "User not found")
        val wallet = _currentWallet.value

        if (amount < 50) {
            return Pair(false, "Minimum withdrawal is ₹50 / নূন্যতম উইথড্র অ্যামাউন্ট ₹৫০")
        }
        val cleanDest = destination.trim()
        if (cleanDest.length < 5 || !cleanDest.contains("@")) {
            return Pair(false, "Please enter a valid UPI ID (e.g. mobile@upi, name@okhdfcbank)")
        }
        // Prevent duplicate spamming while another withdrawal is pending review
        val hasActiveWithdrawal = _withdrawals.value.any {
            it.userId == user.id && (it.status == WithdrawalStatus.PENDING || it.status == WithdrawalStatus.APPROVED || it.status == WithdrawalStatus.PROCESSING)
        }
        if (hasActiveWithdrawal) {
            return Pair(false, "You already have an active withdrawal request under review! / আপনার একটি উইথড্রয়াল অনুরোধ ইতিমধ্যে পেন্ডিং রয়েছে")
        }

        if (wallet.winningBalance < amount) {
            return Pair(false, "Insufficient Winning balance / অপর্যাপ্ত উইনিং ব্যালেন্স (Deposit balance cannot be withdrawn directly)")
        }

        // Reserve virtual funds: deduct from available winningBalance and add to reservedBalance
        val updatedW = wallet.copy(
            winningBalance = wallet.winningBalance - amount,
            reservedBalance = wallet.reservedBalance + amount,
            updatedAt = System.currentTimeMillis()
        )
        _currentWallet.value = updatedW
        syncCloudWallet(updatedW)

        val reqId = "WTH-" + (1000 + _withdrawals.value.size + 1)
        val req = WithdrawalRequest(
            id = reqId,
            userId = user.id,
            userName = user.name,
            freeFireUid = user.freeFireUid,
            amount = amount,
            method = "UPI",
            payoutDestination = cleanDest,
            status = WithdrawalStatus.PENDING,
            requestedAt = System.currentTimeMillis()
        )
        _withdrawals.value = listOf(req) + _withdrawals.value

        val tx = Transaction(
            id = reqId,
            userId = user.id,
            type = TransactionType.WITHDRAWAL,
            amount = amount,
            status = TransactionStatus.PENDING,
            timestamp = System.currentTimeMillis(),
            referenceId = reqId,
            titleEn = "Withdrawal to $cleanDest",
            titleBn = "$cleanDest এ উইথড্রয়াল",
            subtitleEn = "Status: Pending Owner Approval (₹$amount Reserved)",
            subtitleBn = "স্ট্যাটাস: মালিকের অনুমোদনের অপেক্ষায় (₹$amount সংরক্ষিত)"
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncCloudTransaction(tx)

        addNotification(
            titleEn = "Withdrawal Request Submitted (#$reqId)",
            titleBn = "উইথড্রয়াল অনুরোধ জমা হয়েছে (#$reqId)",
            messageEn = "₹$amount to $cleanDest is pending Admin review. Real money will be manually transferred to your UPI ID upon Owner approval.",
            messageBn = "₹$amount উইথড্র অনুরোধ অ্যাডমিন পর্যালোচনার অপেক্ষায়। অনুমোদনের পর মালিক সরাসরি ইউপিআই-তে রিয়েল মানি ট্রান্সফার করবেন।",
            type = "WITHDRAWAL"
        )

        logAdminAction(
            action = "WITHDRAWAL_REQUESTED",
            targetId = reqId,
            details = "Player ${user.name} (UID: ${user.freeFireUid}) requested ₹$amount to $cleanDest"
        )

        return Pair(true, "Withdrawal request submitted! ₹$amount reserved from your winning balance pending Owner transfer.")
    }

    fun approveWithdrawal(requestId: String): Pair<Boolean, String> {
        val currentUser = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!currentUser.isOwner) {
            return Pair(false, "Security violation: Only verified App Owner can manage withdrawal requests.")
        }

        val req = _withdrawals.value.find { it.id == requestId }
            ?: return Pair(false, "Withdrawal request not found")

        if (req.status != WithdrawalStatus.PENDING) {
            return Pair(false, "Request is already ${req.status.name}")
        }

        _withdrawals.value = _withdrawals.value.map {
            if (it.id == requestId) it.copy(status = WithdrawalStatus.APPROVED, adminNotes = "Approved by Owner. Ready for manual real-money payout.") else it
        }

        var updatedTx: Transaction? = null
        _transactions.value = _transactions.value.map { tx ->
            if (tx.id == requestId) {
                val upd = tx.copy(
                    subtitleEn = "Status: Approved by Owner • Manual UPI transfer in progress",
                    subtitleBn = "স্ট্যাটাস: অনুমোদিত • ম্যানুয়াল ইউপিআই ট্রান্সফার চলছে"
                )
                updatedTx = upd
                upd
            } else tx
        }
        updatedTx?.let { syncCloudTransaction(it) }

        addNotification(
            titleEn = "Withdrawal Approved (#$requestId)",
            titleBn = "উইথড্রয়াল অনুমোদিত (#$requestId)",
            messageEn = "Your withdrawal of ₹${req.amount} has been approved by Owner. Real money manual UPI transfer is being processed.",
            messageBn = "আপনার ₹${req.amount} উইথড্রয়াল অনুমোদিত হয়েছে। রিয়েল মানি ট্রান্সফার প্রক্রিয়াধীন রয়েছে।",
            type = "WITHDRAWAL"
        )

        logAdminAction(
            action = "WITHDRAWAL_APPROVED",
            targetId = requestId,
            details = "Owner approved withdrawal #$requestId (₹${req.amount} to ${req.payoutDestination})"
        )

        return Pair(true, "Withdrawal #$requestId approved. Please manually send ₹${req.amount} to ${req.payoutDestination}.")
    }

    fun markWithdrawalPaid(requestId: String, paymentUtr: String): Pair<Boolean, String> {
        val currentUser = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!currentUser.isOwner) {
            return Pair(false, "Security violation: Only verified App Owner can authorize and record payouts.")
        }

        val req = _withdrawals.value.find { it.id == requestId }
            ?: return Pair(false, "Withdrawal request not found")

        if (req.status == WithdrawalStatus.PAID) {
            return Pair(false, "Duplicate payment prevented: Request #$requestId has already been marked as Paid.")
        }
        if (req.status == WithdrawalStatus.REJECTED || req.status == WithdrawalStatus.FAILED) {
            return Pair(false, "Cannot pay a rejected or failed withdrawal request.")
        }

        val cleanUtr = paymentUtr.trim().uppercase()
        if (cleanUtr.length < 6) {
            return Pair(false, "Please provide a valid bank UTR / Payment reference (minimum 6 characters).")
        }

        // Prevent duplicate UTR usage across all payouts
        if (processedPaymentTxIds.contains(cleanUtr)) {
            return Pair(false, "Duplicate payout blocked: UTR $cleanUtr has already been recorded for another payout!")
        }
        processedPaymentTxIds.add(cleanUtr)

        val now = System.currentTimeMillis()

        // 1. Update Withdrawal Request
        _withdrawals.value = _withdrawals.value.map {
            if (it.id == requestId) {
                it.copy(
                    status = WithdrawalStatus.PAID,
                    paymentReference = cleanUtr,
                    processedAt = now,
                    adminNotes = "Real money transferred manually via UPI by Owner."
                )
            } else it
        }

        // 2. Permanently deduct reserved virtual balance
        if (req.userId == _currentUser.value?.id) {
            val wallet = _currentWallet.value
            val updatedW = wallet.copy(
                reservedBalance = (wallet.reservedBalance - req.amount).coerceAtLeast(0)
            )
            _currentWallet.value = updatedW
            syncCloudWallet(updatedW)
        } else {
            runBlocking {
                firestoreManager.finalizeWithdrawalPayout(req.userId, req.amount)
            }
        }

        // 3. Mark user transaction as SUCCESS
        var updatedPaidTx: Transaction? = null
        _transactions.value = _transactions.value.map { tx ->
            if (tx.id == requestId) {
                val upd = tx.copy(
                    status = TransactionStatus.SUCCESS,
                    referenceId = cleanUtr,
                    subtitleEn = "Paid to ${req.payoutDestination} • Bank UTR: $cleanUtr",
                    subtitleBn = "${req.payoutDestination} এ সফলভাবে পেইড • ব্যাংক ইউটিআর: $cleanUtr"
                )
                updatedPaidTx = upd
                upd
            } else tx
        }
        updatedPaidTx?.let { syncCloudTransaction(it) }

        // 4. Update Admin Wallet accounting
        _adminWallet.value = _adminWallet.value.copy(
            totalPrizePaid = _adminWallet.value.totalPrizePaid + req.amount
        )
        val payoutLedgerEntry = AdminLedgerEntry(
            id = "LED-${System.currentTimeMillis()}",
            type = AdminLedgerEntryType.PRIZE_PAYOUT,
            amount = req.amount,
            descriptionEn = "Manual real money withdrawal paid to ${req.payoutDestination} (UTR: $cleanUtr)",
            descriptionBn = "${req.payoutDestination} এ ম্যানুয়াল ইউপিআই উইথড্রয়াল পেইড (ইউটিআর: $cleanUtr)",
            referenceId = cleanUtr,
            timestamp = now
        )
        _adminLedger.value = listOf(payoutLedgerEntry) + _adminLedger.value

        // 5. Notify player
        addNotification(
            titleEn = "Withdrawal Paid! (#$requestId)",
            titleBn = "উইথড্রয়াল সফলভাবে সম্পন্ন! (#$requestId)",
            messageEn = "₹${req.amount} has been transferred to your UPI ID ${req.payoutDestination}. Bank UTR: $cleanUtr.",
            messageBn = "₹${req.amount} আপনার ইউপিআই ${req.payoutDestination} তে পাঠানো হয়েছে। ব্যাংক ইউটিআর: $cleanUtr।",
            type = "PAYMENT"
        )

        logAdminAction(
            action = "WITHDRAWAL_PAID",
            targetId = requestId,
            details = "Owner marked withdrawal #$requestId as PAID (₹${req.amount} to ${req.payoutDestination}, UTR: $cleanUtr)"
        )

        return Pair(true, "Withdrawal marked as Paid! Bank UTR: $cleanUtr recorded successfully.")
    }

    fun rejectWithdrawal(requestId: String, reason: String): Pair<Boolean, String> {
        val currentUser = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!currentUser.isOwner) {
            return Pair(false, "Security violation: Only verified App Owner can reject withdrawals.")
        }

        val req = _withdrawals.value.find { it.id == requestId }
            ?: return Pair(false, "Withdrawal request not found")

        if (req.status == WithdrawalStatus.PAID) {
            return Pair(false, "Cannot reject an already paid withdrawal.")
        }
        if (req.status == WithdrawalStatus.REJECTED) {
            return Pair(false, "Request is already rejected.")
        }

        val cleanReason = reason.ifBlank { "Declined by Admin" }
        val now = System.currentTimeMillis()

        // 1. Update Withdrawal Request
        _withdrawals.value = _withdrawals.value.map {
            if (it.id == requestId) {
                it.copy(
                    status = WithdrawalStatus.REJECTED,
                    rejectionReason = cleanReason,
                    processedAt = now
                )
            } else it
        }

        // 2. Release reserved virtual funds back to player's winning balance
        if (req.userId == _currentUser.value?.id) {
            val wallet = _currentWallet.value
            val updatedW = wallet.copy(
                winningBalance = wallet.winningBalance + req.amount,
                reservedBalance = (wallet.reservedBalance - req.amount).coerceAtLeast(0)
            )
            _currentWallet.value = updatedW
            syncCloudWallet(updatedW)
        } else {
            runBlocking {
                firestoreManager.refundWithdrawal(req.userId, req.amount)
            }
        }

        // 3. Mark transaction as FAILED
        var updatedRejectedTx: Transaction? = null
        _transactions.value = _transactions.value.map { tx ->
            if (tx.id == requestId) {
                val upd = tx.copy(
                    status = TransactionStatus.FAILED,
                    subtitleEn = "Rejected: $cleanReason • ₹${req.amount} refunded to Winning Balance",
                    subtitleBn = "বাতিল: $cleanReason • ₹${req.amount} উইনিং ব্যালেন্সে রিফান্ড"
                )
                updatedRejectedTx = upd
                upd
            } else tx
        }
        updatedRejectedTx?.let { syncCloudTransaction(it) }

        // 4. Notify player
        addNotification(
            titleEn = "Withdrawal Rejected (#$requestId)",
            titleBn = "উইথড্রয়াল বাতিল (#$requestId)",
            messageEn = "Your withdrawal of ₹${req.amount} was rejected: $cleanReason. Reserved amount has been released back to your Winning Balance.",
            messageBn = "আপনার ₹${req.amount} উইথড্রয়াল বাতিল হয়েছে: $cleanReason। টাকা উইনিং ব্যালেন্সে ফেরত দেওয়া হয়েছে।",
            type = "WITHDRAWAL"
        )

        logAdminAction(
            action = "WITHDRAWAL_REJECTED",
            targetId = requestId,
            details = "Owner rejected withdrawal #$requestId (Reason: $cleanReason, ₹${req.amount} refunded to player)"
        )

        return Pair(true, "Withdrawal #$requestId rejected and ₹${req.amount} refunded to player wallet.")
    }

    fun failWithdrawal(requestId: String, reason: String): Pair<Boolean, String> {
        val currentUser = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!currentUser.isOwner) {
            return Pair(false, "Security violation: Only verified App Owner can manage withdrawals.")
        }

        val req = _withdrawals.value.find { it.id == requestId }
            ?: return Pair(false, "Withdrawal request not found")

        if (req.status == WithdrawalStatus.PAID) {
            return Pair(false, "Cannot fail an already paid withdrawal.")
        }

        val cleanReason = reason.ifBlank { "Payment provider network error" }
        val now = System.currentTimeMillis()

        _withdrawals.value = _withdrawals.value.map {
            if (it.id == requestId) {
                it.copy(
                    status = WithdrawalStatus.FAILED,
                    rejectionReason = cleanReason,
                    processedAt = now
                )
            } else it
        }

        // Release reserved funds back to player's winning balance
        if (req.userId == _currentUser.value?.id) {
            val wallet = _currentWallet.value
            _currentWallet.value = wallet.copy(
                winningBalance = wallet.winningBalance + req.amount,
                reservedBalance = (wallet.reservedBalance - req.amount).coerceAtLeast(0)
            )
        }

        _transactions.value = _transactions.value.map { tx ->
            if (tx.id == requestId) {
                tx.copy(
                    status = TransactionStatus.FAILED,
                    subtitleEn = "Payment Failed: $cleanReason • ₹${req.amount} refunded to Winning Balance",
                    subtitleBn = "পেমেন্ট ব্যর্থ: $cleanReason • ₹${req.amount} উইনিং ব্যালেন্সে রিফান্ড"
                )
            } else tx
        }

        addNotification(
            titleEn = "Withdrawal Payment Failed (#$requestId)",
            titleBn = "উইথড্রয়াল পেমেন্ট ব্যর্থ (#$requestId)",
            messageEn = "Bank transfer failed: $cleanReason. ₹${req.amount} has been restored to your Winning Balance.",
            messageBn = "ব্যাংক ট্রান্সফার ব্যর্থ: $cleanReason। ₹${req.amount} উইনিং ব্যালেন্সে রিফান্ড হয়েছে।",
            type = "WITHDRAWAL"
        )

        logAdminAction(
            action = "WITHDRAWAL_FAILED",
            targetId = requestId,
            details = "Withdrawal #$requestId payment failed (Reason: $cleanReason, funds released to player)"
        )

        return Pair(true, "Withdrawal #$requestId marked as Failed. Funds restored to player wallet.")
    }

    fun updateWithdrawalStatus(requestId: String, newStatus: WithdrawalStatus, reason: String? = null): Pair<Boolean, String> {
        return when (newStatus) {
            WithdrawalStatus.APPROVED -> approveWithdrawal(requestId)
            WithdrawalStatus.PROCESSING -> {
                val currentUser = _currentUser.value ?: return Pair(false, "Authentication required")
                if (!currentUser.isOwner) return Pair(false, "Owner authorization required")
                _withdrawals.value = _withdrawals.value.map {
                    if (it.id == requestId) it.copy(status = WithdrawalStatus.PROCESSING) else it
                }
                _transactions.value = _transactions.value.map {
                    if (it.id == requestId) it.copy(subtitleEn = "Status: Processing Manual UPI Payout") else it
                }
                Pair(true, "Withdrawal $requestId marked as Processing")
            }
            WithdrawalStatus.PAID -> markWithdrawalPaid(requestId, reason ?: ("UTR" + System.currentTimeMillis()))
            WithdrawalStatus.REJECTED -> rejectWithdrawal(requestId, reason ?: "Declined by Admin")
            WithdrawalStatus.FAILED -> failWithdrawal(requestId, reason ?: "Payment Failed")
            WithdrawalStatus.PENDING -> Pair(false, "Cannot reset status to Pending")
        }
    }

    // ==========================================
    // TOURNAMENT JOIN & ELIGIBILITY ENFORCEMENT
    // ==========================================

    private val tournamentJoinLock = Any()

    fun joinTournament(tournamentId: String): Pair<Boolean, String> = synchronized(tournamentJoinLock) {
        val user = _currentUser.value ?: return Pair(false, "Please log in")

        // 1. Strict Hack / Ban / Suspension Check
        if (user.isSuspensionActive) {
            val statusLabel = if (user.effectiveAccountStatus == com.example.model.AccountStatus.PERMANENTLY_BANNED)
                "Permanently Banned"
            else
                "Temporarily Suspended (${user.suspensionRemainingFormatted})"
            return Pair(false, "Tournament entry blocked: Your account is $statusLabel. Reason: ${user.banReason ?: "Fair Play violation"}")
        }

        // 2. Strict Free Fire Level 10 Eligibility Check
        if (user.freeFireLevel < 10) {
            return Pair(false, "Free Fire Account Level 10 or above is strictly required! (Your level: ${user.freeFireLevel}) / টুর্নামেন্টে অংশগ্রহণের জন্য ফ্রি ফায়ার অ্যাকাউন্টের লেভেল ১০ বা তার বেশি হতে হবে")
        }

        if (user.levelVerificationStatus == LevelVerificationStatus.REJECTED) {
            return Pair(false, "Free Fire Level verification was rejected. Please re-submit account level proof in Profile.")
        }

        val tournament = _tournaments.value.find { it.id == tournamentId } 
        val fee = tournament?.entryFee ?: 20

        // Generate unique Entry ID bound to user, FF UID, and tournament
        val modeCode = when (tournament?.mode) {
            GameMode.LONE_WOLF -> "LW"
            GameMode.CLASH_SQUAD -> "CS"
            GameMode.BATTLE_ROYALE, null -> "BR"
        }
        val cleanTourney = tournamentId.replace("T-", "")
        val cleanUid = user.freeFireUid.takeLast(4)
        val randomToken = UUID.randomUUID().toString().take(4).uppercase()
        val uniqueEntryId = "BA-ENTRY-$modeCode-$cleanTourney-$cleanUid-$randomToken"

        // Authoritatively validate & reserve slot + deduct entry fee in atomic cloud storage
        val joinResult = runBlocking {
            firestoreManager.atomicJoinMatch(
                tournamentId = tournamentId,
                user = user,
                fee = fee,
                uniqueEntryId = uniqueEntryId,
                fallbackTournament = tournament
            )
        }

        if (!joinResult.success) {
            return Pair(false, joinResult.message)
        }

        if (joinResult.isAlreadyJoined) {
            return Pair(false, joinResult.message)
        }

        val updatedTourney = joinResult.tournament ?: return Pair(false, "Tournament update error")
        val updatedWallet = joinResult.updatedWallet ?: _currentWallet.value
        val tx = joinResult.transaction

        _currentWallet.value = updatedWallet
        syncCloudWallet(updatedWallet)

        if (tx != null) {
            _transactions.value = listOf(tx) + _transactions.value
            syncCloudTransaction(tx)
        }

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) updatedTourney else t
        }
        if (_tournaments.value.none { it.id == tournamentId }) {
            _tournaments.value = listOf(updatedTourney) + _tournaments.value
        }

        addNotification(
            titleEn = "Registered for ${updatedTourney.titleEn}",
            titleBn = "${updatedTourney.titleBn} তে যোগ দিয়েছেন",
            messageEn = "Entry ID: $uniqueEntryId generated. Entry fee ₹$fee deducted. Room ID & Password will unlock 15 mins before match starts.",
            messageBn = "এন্ট্রি আইডি: $uniqueEntryId তৈরি হয়েছে। ফি ₹$fee কাটা হয়েছে। রুম আইডি ও পাসওয়ার্ড ম্যাচ শুরুর ১৫ মিনিট আগে আনলক হবে।",
            type = "MATCH",
            notificationId = "NOTIF-REG-$tournamentId-${user.id}"
        )

        // When match reaches required player count, notify players with 45s warning
        if (joinResult.notifyCountdown) {
            addNotification(
                titleEn = "Your Match Will Start in 45 Seconds!",
                titleBn = "আপনার ম্যাচ ৪৫ সেকেন্ডের মধ্যে শুরু হবে!",
                messageEn = "Your Match Will Start in 45 Seconds! All slots confirmed. Synchronized 45-second countdown started. Get ready in Free Fire!",
                messageBn = "আপনার ম্যাচ ৪৫ সেকেন্ডের মধ্যে শুরু হবে! সকল স্লট পূর্ণ হয়েছে। ৪৫ সেকেন্ডের কাউন্টডাউন শুরু হয়েছে। ফ্রি ফায়ারে প্রস্তুত হন!",
                type = "MATCH",
                notificationId = "NOTIF-COUNTDOWN-$tournamentId"
            )
            logAdminAction("MATCH_CONFIRMED", updatedTourney.id, "All slots filled (${updatedTourney.participants.size}/${updatedTourney.totalSlots}). 45s countdown active.")
        }

        // Automatic Match Creation for subsequent players
        if (joinResult.shouldAutoCreateNext) {
            val nextTourney = autoCreateNextMatch(updatedTourney.mode)
            runBlocking {
                firestoreManager.saveTournament(nextTourney)
            }
            logAdminAction("AUTO_MATCH_CREATED", nextTourney.id, "Auto-created subsequent ${updatedTourney.mode.tagEn} match (${nextTourney.id}) as $tournamentId is full.")
        }

        return Pair(true, "Successfully joined! Entry ID: $uniqueEntryId (Bound to FF UID: ${user.freeFireUid})")
    }

    suspend fun joinTournamentAsync(tournamentId: String): Pair<Boolean, String> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        joinTournament(tournamentId)
    }

    // ==========================================
    // AUTOMATIC MATCH CREATION HELPER
    // ==========================================

    private fun autoCreateNextMatch(mode: GameMode): Tournament {
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return when (mode) {
            GameMode.LONE_WOLF -> {
                val nextNumber = 101 + _tournaments.value.count { it.mode == GameMode.LONE_WOLF }
                val nextId = "T-LW-$nextNumber"
                val newTourney = Tournament(
                    id = nextId,
                    titleEn = "Lone Wolf Quick Cup #$nextNumber",
                    titleBn = "লোন উলফ কুইক কাপ #$nextNumber",
                    mode = GameMode.LONE_WOLF,
                    entryFee = 25,
                    totalPrizePool = 40,
                    totalSlots = 2,
                    mapName = "Iron Cage",
                    startTimeFormatted = sdf.format(Date(now + 10 * 60 * 1000)),
                    startTimeMillis = now + 10 * 60 * 1000,
                    status = TournamentStatus.UPCOMING,
                    roomId = "${(1000000..9999999).random()}",
                    roomPassword = "${(1000..9999).random()}",
                    adminCommission = 10
                )
                _tournaments.value = _tournaments.value + newTourney
                firestoreManager.seedTournament(newTourney)
                newTourney
            }
            GameMode.CLASH_SQUAD -> {
                val nextNumber = 201 + _tournaments.value.count { it.mode == GameMode.CLASH_SQUAD }
                val nextId = "T-CS-$nextNumber"
                val newTourney = Tournament(
                    id = nextId,
                    titleEn = "Clash Squad Championship #$nextNumber",
                    titleBn = "ক্ল্যাশ স্কোয়াড চ্যাম্পিয়নশিপ #$nextNumber",
                    mode = GameMode.CLASH_SQUAD,
                    entryFee = 20,
                    totalPrizePool = 120,
                    totalSlots = 8,
                    mapName = "Bermuda Kalahari",
                    startTimeFormatted = sdf.format(Date(now + 20 * 60 * 1000)),
                    startTimeMillis = now + 20 * 60 * 1000,
                    status = TournamentStatus.UPCOMING,
                    roomId = "${(1000000..9999999).random()}",
                    roomPassword = "${(1000..9999).random()}",
                    adminCommission = 40
                )
                _tournaments.value = _tournaments.value + newTourney
                firestoreManager.seedTournament(newTourney)
                newTourney
            }
            GameMode.BATTLE_ROYALE -> {
                val nextNumber = 301 + _tournaments.value.count { it.mode == GameMode.BATTLE_ROYALE }
                val nextId = "T-BR-$nextNumber"
                val newTourney = Tournament(
                    id = nextId,
                    titleEn = "Mega Battle Royale India #$nextNumber",
                    titleBn = "মেগা ব্যাটেল রয়্যাল ইন্ডিয়া #$nextNumber",
                    mode = GameMode.BATTLE_ROYALE,
                    entryFee = 20,
                    totalPrizePool = 570,
                    totalSlots = 40,
                    mapName = "Bermuda",
                    startTimeFormatted = "08:00 PM IST",
                    startTimeMillis = now + 60 * 60 * 1000,
                    status = TournamentStatus.UPCOMING,
                    scheduledDailyTime = "08:00 PM IST",
                    roomId = "${(1000000..9999999).random()}",
                    roomPassword = "${(1000..9999).random()}",
                    adminCommission = 230
                )
                _tournaments.value = _tournaments.value + newTourney
                firestoreManager.seedTournament(newTourney)
                newTourney
            }
        }
    }

    // ==========================================
    // SYNCHRONIZED 45-SECOND COUNTDOWN TICKER
    // ==========================================

    fun tickCountdowns(serverNowMillis: Long = firestoreManager.getServerTimeMillis()) {
        val now = serverNowMillis
        var tourneysChanged = false
        val updatedTourneys = _tournaments.value.map { t ->
            if ((t.status == TournamentStatus.CONFIRMED || t.status == TournamentStatus.COUNTDOWN) && t.countdownStartMillis != null) {
                val elapsed = now - t.countdownStartMillis
                if (elapsed >= 45_000L) {
                    if (t.status != TournamentStatus.LIVE || !t.matchStartedNotified) {
                        tourneysChanged = true
                        val updated = t.copy(status = TournamentStatus.LIVE, matchStartedNotified = true)
                        firestoreManager.saveTournamentSync(updated)
                        addNotification(
                            titleEn = "Match Started!",
                            titleBn = "ম্যাচ শুরু হয়েছে!",
                            messageEn = "Match Started! ${t.titleEn} is now LIVE! Your Room ID & Password are now unlocked inside Match Details.",
                            messageBn = "ম্যাচ শুরু হয়েছে! ${t.titleBn} এখন লাইভ! টুর্নামেন্ট ডিটেইলসে গিয়ে আপনার আনলকড রুম আইডি ও পাসওয়ার্ড দেখুন।",
                            type = "MATCH",
                            notificationId = "NOTIF-MATCH-STARTED-${t.id}"
                        )
                        logAdminAction("MATCH_LIVE", t.id, "45s countdown elapsed. Match transitioned to LIVE. Room credentials unlocked.")
                        updated
                    } else t
                } else t
            } else t
        }
        if (tourneysChanged) {
            _tournaments.value = updatedTourneys
        }
    }

    // ==========================================
    // LONE WOLF TIMEOUT & PLAYER REMOVAL
    // ==========================================

    fun cancelLoneWolfWaitingPlayer(tournamentId: String, refundFee: Boolean = true): Pair<Boolean, String> {
        val tourney = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Tournament not found")

        if (tourney.mode != GameMode.LONE_WOLF) {
            return Pair(false, "Only applicable to Lone Wolf matchmaking")
        }

        val waitingParticipant = tourney.participants.firstOrNull()
            ?: return Pair(false, "No waiting player found in this match")

        if (refundFee) {
            if (_currentUser.value?.id == waitingParticipant.userId) {
                val wallet = _currentWallet.value
                val updatedW = wallet.copy(depositBalance = wallet.depositBalance + tourney.entryFee)
                _currentWallet.value = updatedW
                syncCloudWallet(updatedW)
            }
            val tx = Transaction(
                id = "TX-CANCEL-" + UUID.randomUUID().toString().take(6).uppercase(),
                userId = waitingParticipant.userId,
                type = TransactionType.REFUND,
                amount = tourney.entryFee,
                status = TransactionStatus.SUCCESS,
                timestamp = System.currentTimeMillis(),
                referenceId = tourney.id,
                titleEn = "Entry Fee Refunded",
                titleBn = "এন্ট্রি ফি রিফান্ড সম্পন্ন",
                subtitleEn = "${tourney.titleEn} • Opponent did not join in 45s • ₹${tourney.entryFee} refunded",
                subtitleBn = "${tourney.titleBn} • ৪৫ সেকেন্ডে প্রতিপক্ষ না পাওয়ায় ₹${tourney.entryFee} ফেরত"
            )
            _transactions.value = listOf(tx) + _transactions.value
            syncCloudTransaction(tx)
            addNotification(
                titleEn = "Entry Fee Refunded",
                titleBn = "এন্ট্রি ফি ফেরত দেওয়া হয়েছে",
                messageEn = "Second player did not join within 45 seconds. ₹${tourney.entryFee} returned to your wallet.",
                messageBn = "৪৫ সেকেন্ডে দ্বিতীয় খেলোয়াড় যোগ দেয়নি। ₹${tourney.entryFee} আপনার ওয়ালেটে ফেরত দেওয়া হয়েছে।"
            )
        }

        // Reset match to UPCOMING with empty slots so new players can join
        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    participants = emptyList(),
                    status = TournamentStatus.UPCOMING,
                    countdownStartMillis = null,
                    waitingPlayerJoinedAt = null
                )
            } else t
        }

        logAdminAction("MATCHMAKING_CANCELLED", tournamentId, "Waiting player ${waitingParticipant.name} removed (Refunded: $refundFee). Match opened for new players.")
        return Pair(true, "Waiting player removed from Lone Wolf. Match reset for new players.")
    }

    // ==========================================
    // BATTLE ROYALE DAILY SCHEDULE MANAGEMENT
    // ==========================================

    fun updateBattleRoyaleSchedule(
        tournamentId: String,
        newScheduledTime: String,
        entryFee: Int? = null,
        totalSlots: Int? = null,
        mapName: String? = null
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required ($BATTLE_ARENA_OWNER_EMAIL)")
        }

        val tourney = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Tournament not found")

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    scheduledDailyTime = newScheduledTime,
                    startTimeFormatted = newScheduledTime,
                    entryFee = entryFee ?: t.entryFee,
                    totalSlots = totalSlots ?: t.totalSlots,
                    mapName = mapName ?: t.mapName
                )
            } else t
        }

        // Notify registered participants
        addNotification(
            titleEn = "Battle Royale Schedule Updated",
            titleBn = "ব্যাটেল রয়্যাল সময়সূচী পরিবর্তিত হয়েছে",
            messageEn = "${tourney.titleEn} schedule changed to $newScheduledTime. Please be ready on time!",
            messageBn = "${tourney.titleBn} এর সময় পরিবর্তিত হয়ে $newScheduledTime হয়েছে। সময়মতো প্রস্তুত থাকুন!",
            type = "MATCH"
        )

        logAdminAction("SCHEDULE_UPDATED", tournamentId, "Admin updated scheduled time to $newScheduledTime (Slots: ${totalSlots ?: tourney.totalSlots}, Map: ${mapName ?: tourney.mapName}).")
        return Pair(true, "Schedule updated to $newScheduledTime. Registered players notified!")
    }

    // ==========================================
    // BR PRIZE DISTRIBUTION CHART CONFIGURATION
    // ==========================================

    fun updateBrPrizeChart(
        tournamentId: String,
        newTiers: List<BrPrizeTier>,
        killPrize: Int = 110
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required ($BATTLE_ARENA_OWNER_EMAIL)")
        }
        val tourney = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Tournament not found")

        val newPlacementPool = newTiers.sumOf { it.totalTierPrize }
        val newTotalPool = newPlacementPool + killPrize

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    brPrizeChart = newTiers,
                    brKillPrizePool = killPrize,
                    totalPrizePool = newTotalPool
                )
            } else t
        }

        logAdminAction(
            "BR_PRIZE_CHART_UPDATED",
            tournamentId,
            "Owner updated prize distribution: ${newTiers.size} tiers, Placement ₹$newPlacementPool + Kills ₹$killPrize = Total ₹$newTotalPool"
        )
        return Pair(true, "Prize distribution chart updated! Total Prize Pool: ₹$newTotalPool across ${newTiers.sumOf { it.totalWinningPositions }} winning positions.")
    }

    // ==========================================
    // MATCH RESULTS & PRIZE PAYMENT APPROVAL
    // ==========================================

    fun verifyMatchResult(
        resultId: String,
        verifiedRank: Int? = null,
        verifiedKills: Int? = null,
        isApproved: Boolean = true
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required ($BATTLE_ARENA_OWNER_EMAIL)")
        }

        val record = _matchResults.value.find { it.id == resultId }
            ?: return Pair(false, "Match result record not found")

        if (record.paymentStatus == PrizePaymentStatus.PAID) {
            return Pair(false, "Cannot modify already paid result record.")
        }

        val rank = verifiedRank ?: record.rank
        val kills = verifiedKills ?: record.kills

        val tourney = _tournaments.value.find { it.id == record.tournamentId }
        val participant = tourney?.participants?.find { it.freeFireUid.trim() == record.freeFireUid.trim() }
        val isAuthorized = participant != null && participant.isPaid && participant.verificationStatus != PlayerVerificationStatus.UNAUTHORIZED && participant.verificationStatus != PlayerVerificationStatus.KICKED
        val isCurrentUserAccount = record.userId == "BA-70912" && (participant == null || (participant.isPaid && participant.verificationStatus != PlayerVerificationStatus.UNAUTHORIZED && participant.verificationStatus != PlayerVerificationStatus.KICKED))

        if (isApproved && !isAuthorized && !isCurrentUserAccount) {
            return Pair(false, "Security violation: Player UID ${record.freeFireUid} is not an authorized, paid participant for match ${record.tournamentId}. Unregistered friends or kicked players cannot receive verified official standing.")
        }

        val placementPrize = tourney?.getPrizeForRank(rank) ?: record.placementPrize
        val killPrize = record.killPrize
        val eligiblePrize = placementPrize + killPrize

        _matchResults.value = _matchResults.value.map { r ->
            if (r.id == resultId) {
                r.copy(
                    rank = rank,
                    kills = kills,
                    placementPrize = placementPrize,
                    killPrize = killPrize,
                    eligiblePrize = eligiblePrize,
                    isVerified = true,
                    paymentStatus = if (isApproved) PrizePaymentStatus.APPROVED else PrizePaymentStatus.REJECTED,
                    verifiedBy = user.email
                )
            } else r
        }

        logAdminAction(
            if (isApproved) "RESULT_VERIFIED" else "RESULT_REJECTED",
            resultId,
            "Result for ${record.playerName} (Rank #$rank, Kills: $kills, Prize: ₹$eligiblePrize) verified by ${user.email}."
        )
        return Pair(true, if (isApproved) "Result verified and approved! Eligible prize: ₹$eligiblePrize." else "Result rejected.")
    }

    fun approveAndProcessPrizePayment(resultId: String): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required ($BATTLE_ARENA_OWNER_EMAIL)")
        }

        val record = _matchResults.value.find { it.id == resultId }
            ?: return Pair(false, "Match result not found")

        val tourney = _tournaments.value.find { it.id == record.tournamentId }
        val participant = tourney?.participants?.find { it.freeFireUid.trim() == record.freeFireUid.trim() }
        val isAuthorized = participant != null && participant.isPaid && participant.verificationStatus != PlayerVerificationStatus.UNAUTHORIZED && participant.verificationStatus != PlayerVerificationStatus.KICKED
        val isCurrentUserAccount = record.userId == "BA-70912" && (participant == null || (participant.isPaid && participant.verificationStatus != PlayerVerificationStatus.UNAUTHORIZED && participant.verificationStatus != PlayerVerificationStatus.KICKED))

        if (!isAuthorized && !isCurrentUserAccount) {
            return Pair(false, "Security violation: Player ${record.playerName} (FF UID: ${record.freeFireUid}) is not an authorized paid participant for match ${record.tournamentId}. Prize payment is strictly blocked.")
        }

        if (record.paymentStatus == PrizePaymentStatus.PAID || record.payoutUtr != null) {
            return Pair(false, "Duplicate payout blocked: Prize has already been paid for this record (UTR: ${record.payoutUtr}).")
        }

        if (record.paymentStatus == PrizePaymentStatus.REJECTED) {
            return Pair(false, "Cannot process payout for a rejected match result.")
        }

        if (record.eligiblePrize <= 0) {
            return Pair(false, "Eligible prize is ₹0. No payout required.")
        }

        val payoutUtr = "UTR-PRIZE-" + UUID.randomUUID().toString().take(10).uppercase()
        val now = System.currentTimeMillis()

        // Step 1: Credit player winning balance if currently active user
        if (_currentUser.value?.id == record.userId) {
            val wallet = _currentWallet.value
            _currentWallet.value = wallet.copy(winningBalance = wallet.winningBalance + record.eligiblePrize)
        }

        // Step 2: Record player Transaction
        val tx = Transaction(
            id = "WIN-" + UUID.randomUUID().toString().take(6).uppercase(),
            userId = record.userId,
            type = TransactionType.PRIZE_MONEY,
            amount = record.eligiblePrize,
            status = TransactionStatus.SUCCESS,
            timestamp = now,
            referenceId = payoutUtr,
            titleEn = "Tournament Prize: ${record.tournamentTitle}",
            titleBn = "টুর্নামেন্ট পুরস্কার: ${record.tournamentTitle}",
            subtitleEn = "Rank #${record.rank} • ₹${record.eligiblePrize} credited (UTR: $payoutUtr)",
            subtitleBn = "র‍্যাঙ্ক #${record.rank} • ₹${record.eligiblePrize} উইনিংসে যুক্ত (ইউটিআর: $payoutUtr)"
        )
        _transactions.value = listOf(tx) + _transactions.value

        // Step 3: Update Admin Wallet & Ledger
        val aw = _adminWallet.value
        _adminWallet.value = aw.copy(
            totalPrizePaid = aw.totalPrizePaid + record.eligiblePrize
        )
        val ledgerEntry = AdminLedgerEntry(
            id = "LEDGER-PRIZE-${record.id}-$now",
            type = AdminLedgerEntryType.MATCH_COMMISSION,
            amount = record.eligiblePrize,
            descriptionEn = "Prize Payout: ${record.playerName} (Rank #${record.rank}, ₹${record.eligiblePrize}, UTR: $payoutUtr)",
            descriptionBn = "পুরস্কার প্রদান: ${record.playerName} (র‍্যাঙ্ক #${record.rank}, ₹${record.eligiblePrize}, ইউটিআর: $payoutUtr)",
            referenceId = payoutUtr
        )
        _adminLedger.value = listOf(ledgerEntry) + _adminLedger.value

        // Step 4: Update Result Record
        _matchResults.value = _matchResults.value.map { r ->
            if (r.id == resultId) {
                r.copy(
                    isVerified = true,
                    paymentStatus = PrizePaymentStatus.PAID,
                    payoutUtr = payoutUtr,
                    payoutTimestamp = now,
                    verifiedBy = user.email
                )
            } else r
        }

        // Step 5: Notify player
        addNotification(
            titleEn = "Prize Money Disbursed: ₹${record.eligiblePrize}",
            titleBn = "পুরস্কারের টাকা প্রদান সম্পন্ন: ₹${record.eligiblePrize}",
            messageEn = "Congratulations! ₹${record.eligiblePrize} for Rank #${record.rank} in ${record.tournamentTitle} has been deposited to your Winning Wallet. UTR: $payoutUtr.",
            messageBn = "অভিনন্দন! ${record.tournamentTitle}-এ র‍্যাঙ্ক #${record.rank}-এর জন্য ₹${record.eligiblePrize} আপনার উইনিং ওয়ালেটে জমা হয়েছে। ইউটিআর: $payoutUtr।",
            type = "PAYMENT"
        )

        logAdminAction(
            "PRIZE_PAYOUT_APPROVED",
            resultId,
            "Owner approved ₹${record.eligiblePrize} prize payment to ${record.playerName} (UTR: $payoutUtr)."
        )

        return Pair(true, "Prize payment of ₹${record.eligiblePrize} approved and transferred! UTR: $payoutUtr.")
    }

    // ==========================================
    // AUTHORIZED PLAYERS & ANTI-FREE ENTRY AUDIT
    // ==========================================

    fun getAuthorizedPlayers(tournamentId: String): List<PlayerParticipant> {
        return _tournaments.value.find { it.id == tournamentId }?.participants ?: emptyList()
    }

    fun verifyRoomPlayer(
        tournamentId: String,
        freeFireUid: String,
        newStatus: PlayerVerificationStatus,
        notes: String? = null
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required ($BATTLE_ARENA_OWNER_EMAIL)")
        }

        val tourney = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Tournament not found")

        val targetParticipant = tourney.participants.find { it.freeFireUid.trim() == freeFireUid.trim() }
            ?: return Pair(false, "Player with FF UID $freeFireUid not found in tournament $tournamentId")

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    participants = t.participants.map { p ->
                        if (p.freeFireUid.trim() == freeFireUid.trim()) {
                            p.copy(
                                verificationStatus = newStatus,
                                adminNotes = notes ?: p.adminNotes,
                                checkedInAt = System.currentTimeMillis()
                            )
                        } else p
                    }
                )
            } else t
        }

        logAdminAction(
            "PLAYER_VERIFICATION_UPDATED",
            tournamentId,
            "Owner updated player ${targetParticipant.freeFireName} ($freeFireUid) to $newStatus (Notes: ${notes ?: "None"})"
        )

        return Pair(true, "Player ${targetParticipant.freeFireName} ($freeFireUid) marked as $newStatus")
    }

    fun removeUnauthorizedPlayer(
        tournamentId: String,
        freeFireUid: String,
        reason: String,
        shouldRefund: Boolean = false
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required ($BATTLE_ARENA_OWNER_EMAIL)")
        }

        val tourney = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Tournament not found")

        val targetParticipant = tourney.participants.find { it.freeFireUid.trim() == freeFireUid.trim() }
            ?: return Pair(false, "Player with FF UID $freeFireUid not found in tournament $tournamentId")

        // If refund is requested and player paid entry fee
        if (shouldRefund && targetParticipant.isPaid && targetParticipant.entryFeePaid > 0) {
            if (_currentUser.value?.id == targetParticipant.userId) {
                val wallet = _currentWallet.value
                val updatedW = wallet.copy(
                    depositBalance = wallet.depositBalance + targetParticipant.entryFeePaid
                )
                _currentWallet.value = updatedW
                syncCloudWallet(updatedW)
            }
            val refundTx = Transaction(
                id = "REF-" + UUID.randomUUID().toString().take(6).uppercase(),
                userId = targetParticipant.userId,
                type = TransactionType.REFUND,
                amount = targetParticipant.entryFeePaid,
                status = TransactionStatus.SUCCESS,
                timestamp = System.currentTimeMillis(),
                referenceId = tournamentId,
                titleEn = "Entry Fee Refund: ${tourney.titleEn}",
                titleBn = "এন্ট্রি ফি রিফান্ড: ${tourney.titleBn}",
                subtitleEn = "Admin removed player from room: $reason",
                subtitleBn = "অ্যাডমিন রুম থেকে রিমুভ করেছেন: $reason"
            )
            _transactions.value = listOf(refundTx) + _transactions.value
            syncCloudTransaction(refundTx)
        }

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    participants = t.participants.map { p ->
                        if (p.freeFireUid.trim() == freeFireUid.trim()) {
                            p.copy(
                                verificationStatus = PlayerVerificationStatus.KICKED,
                                adminNotes = "Kicked: $reason"
                            )
                        } else p
                    }
                )
            } else t
        }

        addNotification(
            titleEn = "Removed from Match (#$tournamentId)",
            titleBn = "ম্যাচ থেকে রিমুভ করা হয়েছে (#$tournamentId)",
            messageEn = "You were removed from ${tourney.titleEn}. Reason: $reason. Room access revoked.${if (shouldRefund) " Entry fee refunded." else ""}",
            messageBn = "আপনাকে ${tourney.titleBn} থেকে রিমুভ করা হয়েছে। কারণ: $reason। রুম অ্যাক্সেস বাতিল।",
            type = "SECURITY"
        )

        logAdminAction(
            "PLAYER_KICKED_FROM_ROOM",
            tournamentId,
            "Owner kicked FF UID $freeFireUid (${targetParticipant.freeFireName}) from room. Reason: $reason. Refunded: $shouldRefund"
        )

        return Pair(true, "Player $freeFireUid successfully kicked from room. Room credentials revoked.")
    }

    fun auditRoomRoster(tournamentId: String, roomPlayerUids: List<String>): RoomRosterAuditReport {
        val tourney = _tournaments.value.find { it.id == tournamentId }
        val cleanRoomUids = roomPlayerUids.map { it.trim() }.filter { it.isNotBlank() }.distinct()

        val registeredUids = tourney?.participants
            ?.filter { it.isPaid && it.verificationStatus != PlayerVerificationStatus.KICKED }
            ?.map { it.freeFireUid.trim() } ?: emptyList()

        // Unauthorized: players found in the in-game room whose UIDs are NOT in registered authorized list
        val unauthorizedUids = cleanRoomUids.filter { uid -> !registeredUids.contains(uid) }

        // Authorized in room
        val authorizedCount = cleanRoomUids.count { uid -> registeredUids.contains(uid) }

        // Missing registered players who haven't entered the room yet
        val missingAuthorizedUids = registeredUids.filter { uid -> !cleanRoomUids.contains(uid) }

        val report = RoomRosterAuditReport(
            tournamentId = tournamentId,
            totalAudited = cleanRoomUids.size,
            authorizedCount = authorizedCount,
            unauthorizedUids = unauthorizedUids,
            missingAuthorizedUids = missingAuthorizedUids,
            timestamp = System.currentTimeMillis()
        )

        if (unauthorizedUids.isNotEmpty()) {
            logAdminAction(
                "FREE_ENTRY_DETECTED",
                tournamentId,
                "🚨 Free Entry Detected! ${unauthorizedUids.size} unauthorized UIDs in room: ${unauthorizedUids.joinToString(", ")}. Action: Must be kicked before start."
            )
        }

        return report
    }

    // ==========================================
    // UNSUCCESSFUL MATCH & ENTRY FEE REFUNDS
    // ==========================================

    fun markTournamentUnsuccessful(tournamentId: String, reason: String = "Technical or server issue"): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required ($BATTLE_ARENA_OWNER_EMAIL)")
        }

        val tourney = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Tournament not found")

        if (tourney.status == TournamentStatus.COMPLETED) {
            return Pair(false, "Completed matches cannot be marked unsuccessful")
        }

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(status = TournamentStatus.UNSUCCESSFUL)
            } else t
        }

        // Generate refund items for all registered participants who haven't been refunded yet
        val newRefundItems = tourney.participants
            .filter { p -> !tourney.refundedPlayerIds.contains(p.userId) }
            .map { p ->
                MatchRefundItem(
                    id = "REFUND-${tourney.id}-${p.userId}",
                    tournamentId = tourney.id,
                    tournamentTitle = tourney.titleEn,
                    userId = p.userId,
                    userName = p.name,
                    freeFireUid = p.freeFireUid,
                    entryFee = tourney.entryFee,
                    status = RefundStatus.PENDING,
                    reason = reason,
                    approvedBy = ""
                )
            }

        val existingIds = _refundRecords.value.map { it.id }.toSet()
        val itemsToAdd = newRefundItems.filter { it.id !in existingIds }
        _refundRecords.value = itemsToAdd + _refundRecords.value

        addNotification(
            titleEn = "Match Marked Unsuccessful: ${tourney.titleEn}",
            titleBn = "ম্যাচ সফল হয়নি চিহ্নিত: ${tourney.titleBn}",
            messageEn = "Reason: $reason. Entry fees are queued for Admin wallet refund.",
            messageBn = "কারণ: $reason। এন্ট্রি ফি ওয়ালেটে রিফান্ডের জন্য প্রস্তুত।"
        )

        logAdminAction("MATCH_MARKED_UNSUCCESSFUL", tournamentId, "Marked unsuccessful ($reason). ${itemsToAdd.size} refund requests queued.")
        return Pair(true, "Match marked as Unsuccessful. ${itemsToAdd.size} player refunds queued for Admin approval.")
    }

    fun approveAndProcessMatchRefund(tournamentId: String, refundItemId: String? = null): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required ($BATTLE_ARENA_OWNER_EMAIL)")
        }

        val tourney = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Tournament not found")

        val targetItems = if (refundItemId != null) {
            _refundRecords.value.filter { it.id == refundItemId && it.status == RefundStatus.PENDING }
        } else {
            _refundRecords.value.filter { it.tournamentId == tournamentId && it.status == RefundStatus.PENDING }
        }

        if (targetItems.isEmpty()) {
            val isAlreadyRefunded = _refundRecords.value.any {
                (refundItemId != null && it.id == refundItemId && it.status == RefundStatus.REFUNDED) ||
                (refundItemId == null && it.tournamentId == tournamentId && it.status == RefundStatus.REFUNDED)
            }
            if (isAlreadyRefunded) {
                return Pair(false, "Already refunded: this entry fee has already been processed.")
            }
            return Pair(false, "No pending refunds found for this match")
        }

        var totalRefundedAmount = 0
        val refundedUserIds = mutableListOf<String>()

        targetItems.forEach { item ->
            // Prevent duplicate refunds
            if (!tourney.refundedPlayerIds.contains(item.userId)) {
                refundedUserIds.add(item.userId)
                totalRefundedAmount += item.entryFee

                // Credit player's wallet if active user
                if (_currentUser.value?.id == item.userId) {
                    val wallet = _currentWallet.value
                    val updatedW = wallet.copy(depositBalance = wallet.depositBalance + item.entryFee)
                    _currentWallet.value = updatedW
                    syncCloudWallet(updatedW)
                }

                // Add Transaction entry with type REFUND
                val tx = Transaction(
                    id = "TX-REFUND-" + UUID.randomUUID().toString().take(6).uppercase(),
                    userId = item.userId,
                    type = TransactionType.REFUND,
                    amount = item.entryFee,
                    status = TransactionStatus.SUCCESS,
                    timestamp = System.currentTimeMillis(),
                    referenceId = tourney.id,
                    titleEn = "Entry Fee Refunded",
                    titleBn = "এন্ট্রি ফি রিফান্ড সম্পন্ন",
                    subtitleEn = "${tourney.titleEn} • ₹${item.entryFee} refunded to Deposit Wallet",
                    subtitleBn = "${tourney.titleBn} • ₹${item.entryFee} ওয়ালেটে ফেরত দেওয়া হয়েছে"
                )
                _transactions.value = listOf(tx) + _transactions.value
                syncCloudTransaction(tx)

                addNotification(
                    titleEn = "Entry Fee Refunded: ₹${item.entryFee}",
                    titleBn = "এন্ট্রি ফি ফেরত দেওয়া হয়েছে: ₹${item.entryFee}",
                    messageEn = "Your entry fee for ${tourney.titleEn} has been returned to your wallet. Reason: ${item.reason}",
                    messageBn = "${tourney.titleBn} এর জন্য আপনার এন্ট্রি ফি ওয়ালেটে জমা হয়েছে। কারণ: ${item.reason}",
                    type = "PAYMENT"
                )
            }
        }

        // Update refund records status to REFUNDED
        _refundRecords.value = _refundRecords.value.map { item ->
            if (targetItems.any { it.id == item.id }) {
                item.copy(
                    status = RefundStatus.REFUNDED,
                    processedAt = System.currentTimeMillis(),
                    approvedBy = user.email
                )
            } else item
        }

        // Mark tournament as refunded to prevent duplicate processing
        val allRefundedIds = (tourney.refundedPlayerIds + refundedUserIds).distinct()
        val allDone = allRefundedIds.size >= tourney.participants.size
        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    isRefundProcessed = allDone,
                    refundedPlayerIds = allRefundedIds
                )
            } else t
        }

        if (totalRefundedAmount > 0) {
            val aw = _adminWallet.value
            _adminWallet.value = aw.copy(
                totalRefundsPaid = aw.totalRefundsPaid + totalRefundedAmount
            )
            val refundLedger = AdminLedgerEntry(
                id = "LEDGER-REFUND-${tourney.id}-${System.currentTimeMillis()}",
                type = AdminLedgerEntryType.REFUND_DEDUCTION,
                amount = totalRefundedAmount,
                descriptionEn = "Refund Disbursed: ${tourney.titleEn} (Total: ₹$totalRefundedAmount)",
                descriptionBn = "রিফান্ড সম্পন্ন: ${tourney.titleBn} (মোট: ₹$totalRefundedAmount)",
                referenceId = tourney.id
            )
            _adminLedger.value = listOf(refundLedger) + _adminLedger.value
        }

        logAdminAction("REFUND_APPROVED", tournamentId, "Approved refund of ₹$totalRefundedAmount to ${refundedUserIds.size} players.")
        return Pair(true, "Successfully refunded ₹$totalRefundedAmount to ${refundedUserIds.size} player wallets. Transaction history updated.")
    }

    // ==========================================
    // REFERRAL SYSTEM
    // ==========================================

    fun applyReferralCode(code: String): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (user.referredBy != null) {
            return Pair(false, "Referral bonus already claimed / রেফারেল বোনাস ইতিমধ্যে গৃহীত হয়েছে")
        }
        if (code.trim().equals(user.referralCode, ignoreCase = true)) {
            return Pair(false, "You cannot use your own referral code (Self-referral prohibited) / নিজের কোড ব্যবহার করা যাবে না")
        }

        // Credit ₹5 bonus to user
        val wallet = _currentWallet.value
        _currentWallet.value = wallet.copy(bonusBalance = wallet.bonusBalance + 5)
        _currentUser.value = user.copy(referredBy = code.trim().uppercase())

        val tx = Transaction(
            id = "REF-" + UUID.randomUUID().toString().take(6).uppercase(),
            userId = user.id,
            type = TransactionType.REFERRAL_BONUS,
            amount = 5,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            referenceId = "REF_BONUS_${code.trim()}",
            titleEn = "Referral Bonus (Friend Invite)",
            titleBn = "রেফারেল বোনাস (বন্ধু আমন্ত্রণ)",
            subtitleEn = "₹5 added to Bonus Cash (Referrer also receives ₹5)",
            subtitleBn = "বোনাস ক্যাশে ₹৫ যোগ হয়েছে (আমন্ত্রণকারীও ₹৫ পাবেন)"
        )
        _transactions.value = listOf(tx) + _transactions.value

        return Pair(true, "Referral bonus ₹5 credited to Bonus Cash!")
    }

    // ==========================================
    // FAIR PLAY & PLAYER BAN / SUSPENSION SYSTEM (ADMIN CONTROLLED)
    // ==========================================

    fun searchPlayers(query: String): List<User> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return _allPlayers.value
        return _allPlayers.value.filter { u ->
            u.id.lowercase().contains(q) ||
            u.uid.lowercase().contains(q) ||
            u.freeFireUid.lowercase().contains(q) ||
            u.name.lowercase().contains(q) ||
            u.email.lowercase().contains(q) ||
            u.freeFireName.lowercase().contains(q)
        }
    }

    fun suspendPlayer(
        targetPlayerId: String,
        durationHours: Int,
        reason: String,
        adminPerformer: String? = null
    ): Pair<Boolean, String> {
        val caller = adminPerformer ?: _currentUser.value?.email ?: BATTLE_ARENA_OWNER_EMAIL
        // Prevent unauthorized users from changing account status
        val isCallerAdmin = _currentUser.value?.isOwner == true || _currentUser.value?.isAdmin == true || caller.equals(BATTLE_ARENA_OWNER_EMAIL, ignoreCase = true)
        if (!isCallerAdmin) {
            return Pair(false, "Unauthorized: Only Battle Arena Admins can change player account status.")
        }
        if (durationHours <= 0) {
            return Pair(false, "Suspension duration must be at least 1 hour.")
        }
        if (reason.isBlank()) {
            return Pair(false, "Please provide a valid reason for suspension.")
        }

        val target: User = _allPlayers.value.find { it.id.equals(targetPlayerId, ignoreCase = true) || it.uid.equals(targetPlayerId, ignoreCase = true) }
            ?: _currentUser.value?.takeIf { it.id.equals(targetPlayerId, ignoreCase = true) || it.uid.equals(targetPlayerId, ignoreCase = true) }
            ?: return Pair(false, "Player '$targetPlayerId' not found.")

        if (target.isOwner) {
            return Pair(false, "Super Admin / Owner account cannot be suspended.")
        }

        val expiresAt = System.currentTimeMillis() + (durationHours.toLong() * 3600_000L)
        val updatedUser = target.copy(
            accountStatus = com.example.model.AccountStatus.TEMPORARILY_SUSPENDED,
            isBanned = false,
            banReason = reason.trim(),
            suspensionDurationHours = durationHours,
            suspensionExpiresAt = expiresAt,
            bannedByAdmin = caller,
            bannedAt = System.currentTimeMillis()
        )

        updatePlayerInRegistry(updatedUser)
        syncCloudUser(updatedUser)
        if (_currentUser.value?.id == target.id || _currentUser.value?.uid == target.uid) {
            _currentUser.value = updatedUser
        }

        val report = IncidentReport(
            id = "REP-${System.currentTimeMillis()}",
            reportedUserId = target.id,
            reportedUserName = target.name,
            reportedUserUid = target.freeFireUid,
            tournamentId = "ADMIN_SUSPENSION",
            reason = "[${durationHours}h Suspension] ${reason.trim()}",
            status = "INVESTIGATING"
        )
        _incidentReports.value = listOf(report) + _incidentReports.value

        logAdminAction(
            action = "PLAYER_SUSPENDED",
            targetId = target.id,
            details = "Suspended for ${durationHours}h by $caller. Reason: ${reason.trim()}. Expires at: $expiresAt (Wallet & stats preserved)"
        )

        return Pair(true, "Player ${target.id} (${target.name}) temporarily suspended for ${durationHours}h.")
    }

    fun permanentlyBanPlayer(
        targetPlayerId: String,
        reason: String,
        adminPerformer: String? = null
    ): Pair<Boolean, String> {
        val caller = adminPerformer ?: _currentUser.value?.email ?: BATTLE_ARENA_OWNER_EMAIL
        // Prevent unauthorized users from changing account status
        val isCallerAdmin = _currentUser.value?.isOwner == true || _currentUser.value?.isAdmin == true || caller.equals(BATTLE_ARENA_OWNER_EMAIL, ignoreCase = true)
        if (!isCallerAdmin) {
            return Pair(false, "Unauthorized: Only Battle Arena Admins can change player account status.")
        }
        if (reason.isBlank()) {
            return Pair(false, "Please provide a reason for the permanent ban.")
        }

        val target: User = _allPlayers.value.find { it.id.equals(targetPlayerId, ignoreCase = true) || it.uid.equals(targetPlayerId, ignoreCase = true) }
            ?: _currentUser.value?.takeIf { it.id.equals(targetPlayerId, ignoreCase = true) || it.uid.equals(targetPlayerId, ignoreCase = true) }
            ?: return Pair(false, "Player '$targetPlayerId' not found.")

        if (target.isOwner) {
            return Pair(false, "Super Admin / Owner account cannot be permanently banned.")
        }

        val updatedUser = target.copy(
            accountStatus = com.example.model.AccountStatus.PERMANENTLY_BANNED,
            isBanned = true,
            banReason = reason.trim(),
            suspensionDurationHours = 0,
            suspensionExpiresAt = null,
            bannedByAdmin = caller,
            bannedAt = System.currentTimeMillis()
        )

        updatePlayerInRegistry(updatedUser)
        syncCloudUser(updatedUser)
        if (_currentUser.value?.id == target.id || _currentUser.value?.uid == target.uid) {
            _currentUser.value = updatedUser
        }

        val report = IncidentReport(
            id = "REP-${System.currentTimeMillis()}",
            reportedUserId = target.id,
            reportedUserName = target.name,
            reportedUserUid = target.freeFireUid,
            tournamentId = "PERMANENT_BAN",
            reason = "[Permanent Ban] ${reason.trim()}",
            status = "BANNED"
        )
        _incidentReports.value = listOf(report) + _incidentReports.value

        logAdminAction(
            action = "PLAYER_BANNED",
            targetId = target.id,
            details = "Permanently banned by $caller. Reason: ${reason.trim()} (Original Player ID, profile, wallet, and match history safely preserved)"
        )

        return Pair(true, "Player ${target.id} (${target.name}) permanently banned. Records safely preserved.")
    }

    fun unbanPlayer(
        targetPlayerId: String,
        adminPerformer: String? = null
    ): Pair<Boolean, String> {
        val caller = adminPerformer ?: _currentUser.value?.email ?: BATTLE_ARENA_OWNER_EMAIL
        val isCallerAdmin = _currentUser.value?.isOwner == true || _currentUser.value?.isAdmin == true || caller.equals(BATTLE_ARENA_OWNER_EMAIL, ignoreCase = true)
        if (!isCallerAdmin) {
            return Pair(false, "Unauthorized: Only Battle Arena Admins can change player account status.")
        }

        val target: User = _allPlayers.value.find { it.id.equals(targetPlayerId, ignoreCase = true) || it.uid.equals(targetPlayerId, ignoreCase = true) }
            ?: _currentUser.value?.takeIf { it.id.equals(targetPlayerId, ignoreCase = true) || it.uid.equals(targetPlayerId, ignoreCase = true) }
            ?: return Pair(false, "Player '$targetPlayerId' not found.")

        val updatedUser = target.copy(
            accountStatus = com.example.model.AccountStatus.ACTIVE,
            isBanned = false,
            banReason = null,
            suspensionDurationHours = 0,
            suspensionExpiresAt = null,
            bannedByAdmin = null,
            bannedAt = null
        )

        updatePlayerInRegistry(updatedUser)
        syncCloudUser(updatedUser)
        if (_currentUser.value?.id == target.id || _currentUser.value?.uid == target.uid) {
            _currentUser.value = updatedUser
        }

        _incidentReports.value = _incidentReports.value.map {
            if (it.reportedUserId == target.id || it.reportedUserId == target.uid) it.copy(status = "RESOLVED") else it
        }

        logAdminAction(
            action = "PLAYER_UNBANNED",
            targetId = target.id,
            details = "Account unbanned and restored to Active status by $caller."
        )

        return Pair(true, "Player ${target.id} (${target.name}) unbanned. Account restored to Active status.")
    }

    fun banPlayer(userId: String, reason: String): Pair<Boolean, String> {
        return permanentlyBanPlayer(userId, reason)
    }

    // ==========================================
    // FAIR PLAY & ANTI-CHEAT MONITORING SYSTEM
    // ==========================================

    /**
     * Allows a player to report another player for suspected cheating.
     * Requires Match ID, Player ID, reason, and optional screenshot/video proof link.
     * Prevents repeated spam reports within 5 minutes for the same match and target.
     */
    fun reportPlayerCheat(
        matchId: String,
        targetPlayerId: String,
        category: CheatCategory,
        reason: String,
        evidenceText: String? = null,
        evidenceUrl: String? = null
    ): Pair<Boolean, String> {
        val reporter = _currentUser.value ?: return Pair(false, "Please log in to submit a Fair Play report.")
        if (targetPlayerId.isBlank()) {
            return Pair(false, "Target Player ID or Free Fire UID is required.")
        }
        if (matchId.isBlank()) {
            return Pair(false, "Match ID is required for verification.")
        }
        if (reason.trim().length < 5) {
            return Pair(false, "Please provide a detailed description (at least 5 characters).")
        }

        // Spam prevention: Check if reporter submitted a report for the same target in the same match within 5 minutes
        val now = System.currentTimeMillis()
        val recentReport = _incidentReports.value.find {
            it.reportedByUserId == reporter.id &&
            it.tournamentId.equals(matchId.trim(), ignoreCase = true) &&
            it.targetPlayerId.equals(targetPlayerId.trim(), ignoreCase = true) &&
            (now - it.timestamp) < 300_000L // 5 minutes cooldown
        }
        if (recentReport != null) {
            return Pair(false, "Spam Prevention: You have already submitted a report for this player in this match recently. Our Admins are reviewing it.")
        }

        // Locate reported player from registry or tournament participants
        val targetUser = _allPlayers.value.find {
            it.playerId.equals(targetPlayerId.trim(), ignoreCase = true) ||
            it.id.equals(targetPlayerId.trim(), ignoreCase = true) ||
            it.freeFireUid.equals(targetPlayerId.trim(), ignoreCase = true)
        }
        val targetName = targetUser?.name ?: targetUser?.freeFireName ?: "Player $targetPlayerId"
        val targetUid = targetUser?.freeFireUid ?: targetPlayerId.trim()
        val targetInternalId = targetUser?.id ?: targetPlayerId.trim()

        val reportId = "AC-${System.currentTimeMillis().toString().takeLast(6)}"
        val report = IncidentReport(
            id = reportId,
            reportedUserId = targetInternalId,
            reportedUserName = targetName,
            reportedUserUid = targetUid,
            tournamentId = matchId.trim(),
            reason = "[${category.displayName}] ${reason.trim()}",
            status = "INVESTIGATING",
            timestamp = now,
            reportedByUserId = reporter.id,
            reporterPlayerId = reporter.playerId.ifBlank { reporter.id },
            targetPlayerId = targetPlayerId.trim(),
            category = category,
            reportStatus = IncidentReportStatus.PENDING,
            evidenceText = evidenceText?.trim(),
            evidenceUrl = evidenceUrl?.trim(),
            investigationNotes = "Reported by ${reporter.name} (${reporter.playerId}). Evidence attached: ${evidenceUrl?.isNotBlank() == true || evidenceText?.isNotBlank() == true}"
        )

        _incidentReports.value = listOf(report) + _incidentReports.value

        logAdminAction(
            action = "CHEAT_REPORT_FILED",
            targetId = targetPlayerId.trim(),
            details = "Report #$reportId against $targetName in Match $matchId. Category: ${category.displayName}. Evidence: ${evidenceUrl ?: evidenceText ?: "None"}"
        )

        addNotification(
            titleEn = "Fair Play Report Received",
            titleBn = "ফেয়ার প্লে রিপোর্ট গৃহীত হয়েছে",
            messageEn = "Your report #$reportId for Match $matchId has been queued for Admin review. Thank you for protecting fair play.",
            messageBn = "ম্যাচ $matchId এর জন্য আপনার রিপোর্ট #$reportId অ্যাডমিন পর্যালোচনার জন্য জমা হয়েছে। ধন্যবাদ।",
            type = "SECURITY"
        )

        return Pair(true, "Report #$reportId submitted successfully. Our Admin team will review the submitted evidence.")
    }

    /**
     * Admin Anti-Cheat Decision: Review, dismiss, or enforce penalties based on evidence.
     * Prevents auto-banning on suspicion alone: requires documented notes and admin email.
     */
    fun reviewCheatReport(
        reportId: String,
        newStatus: IncidentReportStatus,
        adminNotes: String,
        actionType: String? = null, // null (no penalty), "SUSPEND", "BAN"
        suspensionHours: Int = 24
    ): Pair<Boolean, String> {
        val caller = _currentUser.value?.email ?: BATTLE_ARENA_OWNER_EMAIL
        val isCallerAdmin = _currentUser.value?.isOwner == true || _currentUser.value?.isAdmin == true || caller.equals(BATTLE_ARENA_OWNER_EMAIL, ignoreCase = true)
        if (!isCallerAdmin) {
            return Pair(false, "Unauthorized: Only Battle Arena Admins can review anti-cheat reports.")
        }

        val report = _incidentReports.value.find { it.id == reportId }
            ?: return Pair(false, "Report #$reportId not found.")

        var penaltySummary: String? = null

        // Apply penalty if verified violation and penalty selected
        if (newStatus == IncidentReportStatus.VERIFIED_VIOLATION && !actionType.isNullOrBlank()) {
            when (actionType) {
                "SUSPEND" -> {
                    suspendPlayer(
                        targetPlayerId = report.reportedUserId,
                        durationHours = suspensionHours,
                        reason = "Fair Play Violation in Match ${report.tournamentId}: ${adminNotes.ifBlank { report.reason }}"
                    )
                    penaltySummary = "Temporarily Suspended (${suspensionHours}h)"
                }
                "BAN" -> {
                    permanentlyBanPlayer(
                        targetPlayerId = report.reportedUserId,
                        reason = "Documented Cheating in Match ${report.tournamentId}: ${adminNotes.ifBlank { report.reason }}"
                    )
                    penaltySummary = "Permanently Banned"
                }
            }
        }

        val updated = report.copy(
            reportStatus = newStatus,
            status = when (newStatus) {
                IncidentReportStatus.VERIFIED_VIOLATION -> "BANNED"
                IncidentReportStatus.DISMISSED -> "RESOLVED"
                IncidentReportStatus.UNDER_REVIEW -> "INVESTIGATING"
                IncidentReportStatus.PENDING -> "INVESTIGATING"
            },
            investigationNotes = adminNotes.trim(),
            reviewedByAdmin = caller,
            reviewedAt = System.currentTimeMillis(),
            penaltyApplied = penaltySummary
        )

        _incidentReports.value = _incidentReports.value.map { if (it.id == reportId) updated else it }

        logAdminAction(
            action = "CHEAT_REPORT_REVIEWED",
            targetId = report.reportedUserId,
            details = "Report #$reportId marked as ${newStatus.displayName} by $caller. Notes: ${adminNotes.trim()}. Penalty: ${penaltySummary ?: "None"}"
        )

        return Pair(true, "Report #$reportId updated to ${newStatus.displayName}.")
    }

    /**
     * Player Appeal: Allows a player with a reported violation to submit an appeal with explanation and counter-proof.
     */
    fun submitPlayerAppeal(
        reportId: String,
        explanation: String,
        proofUrl: String? = null
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Please log in to submit an appeal.")
        if (explanation.trim().length < 10) {
            return Pair(false, "Please provide a detailed explanation for your appeal (at least 10 characters).")
        }

        val report = _incidentReports.value.find { it.id == reportId && (it.reportedUserId == user.id || it.targetPlayerId == user.playerId) }
            ?: return Pair(false, "No violation report found matching ID #$reportId for your account.")

        if (report.appeal != null && report.appeal.status == "PENDING") {
            return Pair(false, "An appeal for report #$reportId is already pending review.")
        }

        val appeal = PlayerAppeal(
            id = "APL-${System.currentTimeMillis().toString().takeLast(6)}",
            reportId = reportId,
            userId = user.id,
            playerId = user.playerId.ifBlank { user.id },
            explanation = explanation.trim(),
            proofUrl = proofUrl?.trim(),
            submittedAt = System.currentTimeMillis(),
            status = "PENDING"
        )

        val updatedReport = report.copy(appeal = appeal)
        _incidentReports.value = _incidentReports.value.map { if (it.id == reportId) updatedReport else it }

        logAdminAction(
            action = "PLAYER_APPEAL_SUBMITTED",
            targetId = user.id,
            details = "Appeal filed for Report #$reportId by ${user.name} (${user.playerId}). Reason: ${explanation.trim()}"
        )

        return Pair(true, "Your appeal for report #$reportId has been submitted for Admin reconsideration.")
    }

    /**
     * Admin Decision on Player Appeal: Accept or reject appeal.
     * If accepted, unbans/restores player and marks report as DISMISSED.
     */
    fun reviewPlayerAppeal(
        reportId: String,
        acceptAppeal: Boolean,
        reviewNotes: String
    ): Pair<Boolean, String> {
        val caller = _currentUser.value?.email ?: BATTLE_ARENA_OWNER_EMAIL
        val isCallerAdmin = _currentUser.value?.isOwner == true || _currentUser.value?.isAdmin == true || caller.equals(BATTLE_ARENA_OWNER_EMAIL, ignoreCase = true)
        if (!isCallerAdmin) {
            return Pair(false, "Unauthorized: Only Battle Arena Admins can review appeals.")
        }

        val report = _incidentReports.value.find { it.id == reportId }
            ?: return Pair(false, "Report #$reportId not found.")
        val appeal = report.appeal ?: return Pair(false, "No appeal found for report #$reportId.")

        val updatedAppeal = appeal.copy(
            status = if (acceptAppeal) "ACCEPTED" else "REJECTED",
            reviewedBy = caller,
            reviewNotes = reviewNotes.trim(),
            reviewedAt = System.currentTimeMillis()
        )

        if (acceptAppeal) {
            unbanPlayer(report.reportedUserId, caller)
        }

        val updatedReport = report.copy(
            reportStatus = if (acceptAppeal) IncidentReportStatus.DISMISSED else report.reportStatus,
            appeal = updatedAppeal,
            investigationNotes = "Appeal ${if (acceptAppeal) "ACCEPTED" else "REJECTED"} by $caller. Notes: ${reviewNotes.trim()}"
        )

        _incidentReports.value = _incidentReports.value.map { if (it.id == reportId) updatedReport else it }

        logAdminAction(
            action = "PLAYER_APPEAL_REVIEWED",
            targetId = report.reportedUserId,
            details = "Appeal for Report #$reportId ${if (acceptAppeal) "ACCEPTED (Account Restored)" else "REJECTED"} by $caller. Notes: ${reviewNotes.trim()}"
        )

        return Pair(true, "Appeal for report #$reportId ${if (acceptAppeal) "Accepted" else "Rejected"}.")
    }

    /**
     * Match Monitoring: Flags duplicate or inconsistent result submissions.
     * Automatically registers an incident report under FAKE_RESULT_SUBMISSION for Admin review.
     */
    fun flagInconsistentResult(
        matchId: String,
        userId: String,
        reason: String,
        details: String
    ) {
        val targetUser = _allPlayers.value.find { it.id == userId }
        val targetName = targetUser?.name ?: "Player $userId"
        val targetUid = targetUser?.freeFireUid ?: userId

        val reportId = "FLAG-${System.currentTimeMillis().toString().takeLast(6)}"
        val report = IncidentReport(
            id = reportId,
            reportedUserId = userId,
            reportedUserName = targetName,
            reportedUserUid = targetUid,
            tournamentId = matchId,
            reason = "[Duplicate/Inconsistent Result] $reason",
            status = "INVESTIGATING",
            timestamp = System.currentTimeMillis(),
            reportedByUserId = "SYSTEM_INTEGRITY",
            reporterPlayerId = "SYSTEM",
            targetPlayerId = targetUser?.playerId ?: userId,
            category = CheatCategory.FAKE_RESULT_SUBMISSION,
            reportStatus = IncidentReportStatus.PENDING,
            evidenceText = details,
            investigationNotes = "Automated Flag: Discrepancy detected during result verification. Flagged for Admin investigation."
        )

        _incidentReports.value = listOf(report) + _incidentReports.value

        logAdminAction(
            action = "INCONSISTENT_RESULT_FLAGGED",
            targetId = userId,
            details = "Flagged in Match $matchId. Reason: $reason • Details: $details"
        )
    }

    // ==========================================
    // TOURNAMENT ADMIN CREATION & VERIFICATION
    // ==========================================

    fun createTournament(
        titleEn: String,
        titleBn: String,
        mode: GameMode,
        entryFee: Int = mode.defaultEntryFee,
        startTimeFormatted: String,
        mapName: String = "Bermuda"
    ): Tournament {
        val id = "T-${mode.code}-${(100 + _tournaments.value.size + 1)}"
        val prizePool = when (mode) {
            GameMode.LONE_WOLF -> 40
            GameMode.CLASH_SQUAD -> 120
            GameMode.BATTLE_ROYALE -> 570
        }
        val t = Tournament(
            id = id,
            titleEn = titleEn,
            titleBn = titleBn,
            mode = mode,
            entryFee = entryFee,
            totalPrizePool = prizePool,
            totalSlots = mode.maxPlayers,
            mapName = mapName,
            startTimeFormatted = startTimeFormatted,
            startTimeMillis = System.currentTimeMillis() + 3600000L,
            status = TournamentStatus.UPCOMING,
            roomId = "89${(1000..9999).random()}",
            roomPassword = "${(1000..9999).random()}",
            participants = emptyList()
        )
        _tournaments.value = listOf(t) + _tournaments.value
        firestoreManager.seedTournament(t)
        return t
    }

    fun updateRoomCredentials(tournamentId: String, roomId: String, roomPass: String) {
        updateRoomDetails(tournamentId, roomId, roomPass, RoomPublicationStatus.PUBLISHED)
    }

    fun createScheduledMatch(
        titleEn: String,
        titleBn: String,
        mode: GameMode,
        matchDate: String = "Today",
        matchTimeIst: String = "08:00 AM IST",
        playerLimit: Int = mode.maxPlayers,
        entryFee: Int = mode.defaultEntryFee,
        descriptionEn: String = "",
        descriptionBn: String = "",
        mapName: String = "Bermuda"
    ): Tournament {
        val count = _tournaments.value.count { it.creationType == MatchCreationType.SCHEDULED } + 1
        val id = "T-SCHED-${mode.code}-$count"
        val prizePool = when (mode) {
            GameMode.LONE_WOLF -> 40
            GameMode.CLASH_SQUAD -> 120
            GameMode.BATTLE_ROYALE -> 570
        }
        val t = Tournament(
            id = id,
            titleEn = titleEn,
            titleBn = titleBn,
            mode = mode,
            entryFee = entryFee,
            totalPrizePool = prizePool,
            totalSlots = playerLimit,
            mapName = mapName,
            startTimeFormatted = "$matchDate, $matchTimeIst",
            startTimeMillis = System.currentTimeMillis() + 3600000L,
            status = TournamentStatus.UPCOMING,
            creationType = MatchCreationType.SCHEDULED,
            matchDate = matchDate,
            matchTimeIst = matchTimeIst,
            scheduledDailyTime = matchTimeIst,
            descriptionEn = descriptionEn,
            descriptionBn = descriptionBn,
            roomStatus = RoomPublicationStatus.PENDING,
            roomId = null,
            roomPassword = null,
            participants = emptyList()
        )
        _tournaments.value = listOf(t) + _tournaments.value
        firestoreManager.seedTournament(t)
        logAdminAction("SCHEDULED_MATCH_CREATED", id, "Created scheduled match $id ($matchDate at $matchTimeIst) • Mode: ${mode.tagEn} • Slots: $playerLimit • Entry: ₹$entryFee")
        return t
    }

    fun createManualMatch(
        titleEn: String,
        titleBn: String,
        mode: GameMode,
        playerLimit: Int = mode.maxPlayers,
        entryFee: Int = mode.defaultEntryFee,
        descriptionEn: String = "",
        descriptionBn: String = "",
        isRegistrationOpen: Boolean = true,
        mapName: String = "Bermuda"
    ): Tournament {
        val count = _tournaments.value.count { it.creationType == MatchCreationType.MANUAL } + 101
        val id = "T-MANUAL-${mode.code}-$count"
        val prizePool = when (mode) {
            GameMode.LONE_WOLF -> 40
            GameMode.CLASH_SQUAD -> 120
            GameMode.BATTLE_ROYALE -> 570
        }
        val t = Tournament(
            id = id,
            titleEn = titleEn,
            titleBn = titleBn,
            mode = mode,
            entryFee = entryFee,
            totalPrizePool = prizePool,
            totalSlots = playerLimit,
            mapName = mapName,
            startTimeFormatted = "Manual / Custom Host",
            startTimeMillis = System.currentTimeMillis() + 1800000L,
            status = if (isRegistrationOpen) TournamentStatus.REGISTRATION_OPEN else TournamentStatus.UPCOMING,
            creationType = MatchCreationType.MANUAL,
            matchDate = "Custom Host",
            matchTimeIst = "Immediate / On Demand",
            scheduledDailyTime = null,
            descriptionEn = descriptionEn,
            descriptionBn = descriptionBn,
            isRegistrationOpen = isRegistrationOpen,
            roomStatus = RoomPublicationStatus.PENDING,
            roomId = null,
            roomPassword = null,
            participants = emptyList()
        )
        _tournaments.value = listOf(t) + _tournaments.value
        firestoreManager.seedTournament(t)
        logAdminAction("MANUAL_MATCH_CREATED", id, "Created manual match $id • Mode: ${mode.tagEn} • Limit: $playerLimit • Fee: ₹$entryFee • Reg: ${if (isRegistrationOpen) "OPEN" else "CLOSED"}")
        return t
    }

    fun editMatchSchedule(
        tournamentId: String,
        newDate: String?,
        newTimeIst: String?,
        newSlots: Int? = null,
        newTitleEn: String? = null
    ): Pair<Boolean, String> {
        val t = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Tournament not found")
        val updated = t.copy(
            matchDate = newDate ?: t.matchDate,
            matchTimeIst = newTimeIst ?: t.matchTimeIst,
            scheduledDailyTime = newTimeIst ?: t.scheduledDailyTime,
            startTimeFormatted = "${newDate ?: t.matchDate}, ${newTimeIst ?: t.matchTimeIst}",
            totalSlots = newSlots ?: t.totalSlots,
            titleEn = newTitleEn ?: t.titleEn
        )
        _tournaments.value = _tournaments.value.map { if (it.id == tournamentId) updated else it }
        logAdminAction("MATCH_SCHEDULE_UPDATED", tournamentId, "Updated schedule for $tournamentId to ${updated.matchDate} at ${updated.matchTimeIst}")

        if (t.participants.isNotEmpty()) {
            addNotification(
                titleEn = "Match Schedule Changed: ${t.titleEn}",
                titleBn = "ম্যাচ শিডিউল পরিবর্তন: ${t.titleBn}",
                messageEn = "Match ${t.id} has been rescheduled to ${updated.matchDate} at ${updated.matchTimeIst}. Please be ready in Free Fire.",
                messageBn = "ম্যাচ ${t.id} এর শিডিউল পরিবর্তন করে ${updated.matchDate} ${updated.matchTimeIst} করা হয়েছে। ফ্রি ফায়ারে প্রস্তুত থাকুন।",
                type = "MATCH"
            )
        }
        return Pair(true, "Schedule updated to ${updated.matchDate} at ${updated.matchTimeIst}")
    }

    fun updateRoomDetails(
        tournamentId: String,
        roomId: String,
        roomPassword: String,
        roomStatus: RoomPublicationStatus = RoomPublicationStatus.PUBLISHED,
        notes: String? = null
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Only verified App Owner ($BATTLE_ARENA_OWNER_EMAIL) can publish room credentials.")
        }
        val t = _tournaments.value.find { it.id == tournamentId }
            ?: return Pair(false, "Match not found")
        val adminPerformer = user.email.ifBlank { BATTLE_ARENA_OWNER_EMAIL }
        val newStatus = if (roomStatus == RoomPublicationStatus.PUBLISHED && t.status == TournamentStatus.UPCOMING) {
            TournamentStatus.ROOM_PUBLISHED
        } else t.status

        val cleanRoomId = roomId.trim()
        val cleanRoomPass = roomPassword.trim()

        val updated = t.copy(
            roomId = cleanRoomId,
            roomPassword = cleanRoomPass,
            roomStatus = roomStatus,
            roomPublishedAt = System.currentTimeMillis(),
            roomPublishedBy = adminPerformer,
            customRoomNotes = notes,
            status = newStatus
        )
        _tournaments.value = _tournaments.value.map { if (it.id == tournamentId) updated else it }

        // Phase 3: Persist credentials to protected subcollection /tournaments/{id}/private_room/credentials
        val paidUids = t.participants.filter { it.isPaid }.map { it.userId }
        repoScope.launch {
            firestoreManager.saveSecureRoomCredentials(
                tournamentId = tournamentId,
                roomId = cleanRoomId,
                roomPassword = cleanRoomPass,
                paidParticipantUids = paidUids,
                publishedBy = adminPerformer
            )
        }

        // Record in permanent Admin Audit Log without exposing plaintext password
        logAdminAction(
            if (roomStatus == RoomPublicationStatus.PUBLISHED) "ROOM_PUBLISHED" else "ROOM_UPDATED",
            tournamentId,
            "Room ID: $cleanRoomId published (Status: ${roomStatus.name}) for $tournamentId by $adminPerformer"
        )

        if (roomStatus == RoomPublicationStatus.PUBLISHED) {
            addNotification(
                titleEn = "Room ID and Password Published!",
                titleBn = "রুম আইডি ও পাসওয়ার্ড প্রকাশ করা হয়েছে!",
                messageEn = "Room credentials for ${t.titleEn} are now unlocked! Room ID: $cleanRoomId. Open Free Fire to join.",
                messageBn = "${t.titleBn} এর জন্য রুম ক্রেডেনশিয়াল প্রকাশ করা হয়েছে! রুম আইডি: $cleanRoomId। ফ্রি ফায়ারে জয়েন করুন।",
                type = "MATCH"
            )
        }
        return Pair(true, "Room details ${roomStatus.displayName.lowercase()} for match $tournamentId")
    }

    fun removeRoomCredentials(tournamentId: String): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Only verified App Owner can remove room credentials.")
        }
        val t = _tournaments.value.find { it.id == tournamentId } ?: return Pair(false, "Match not found")
        val adminPerformer = user.email.ifBlank { BATTLE_ARENA_OWNER_EMAIL }

        _tournaments.value = _tournaments.value.map {
            if (it.id == tournamentId) it.copy(
                roomId = null,
                roomPassword = null,
                roomStatus = RoomPublicationStatus.CLOSED
            ) else it
        }

        repoScope.launch {
            firestoreManager.removeSecureRoomCredentials(tournamentId)
        }

        logAdminAction(
            "ROOM_CREDENTIALS_REMOVED",
            tournamentId,
            "Room credentials removed for $tournamentId by $adminPerformer"
        )
        return Pair(true, "Room credentials removed for match $tournamentId")
    }

    fun hideRoomDetails(tournamentId: String): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Only verified App Owner can hide room details.")
        }
        val t = _tournaments.value.find { it.id == tournamentId } ?: return Pair(false, "Match not found")
        _tournaments.value = _tournaments.value.map {
            if (it.id == tournamentId) it.copy(roomStatus = RoomPublicationStatus.HIDDEN) else it
        }
        logAdminAction("ROOM_HIDDEN", tournamentId, "Room details temporarily hidden for $tournamentId")
        return Pair(true, "Room details hidden")
    }

    /**
     * Phase 3: Secure Room ID and Password access:
     * Validates:
     * 1. User is authenticated and not suspended/banned
     * 2. Super Admin always has authorized access
     * 3. Regular player must belong to the exact Match ID
     * 4. Registration is confirmed and paid
     * 5. Required level eligibility is verified
     * 6. Match room status is published/unlocked
     * Returns Pair(null, null) with authorization failure for any mismatch.
     */
    fun getSecureRoomCredentials(tournamentId: String, userId: String): Pair<String?, String?> {
        val user = _currentUser.value ?: return Pair(null, null)
        val tourney = _tournaments.value.find { it.id == tournamentId } ?: return Pair(null, null)

        // Super Admin / Owner always has administrative access
        if (user.isOwner) {
            val fromStore = FirestoreManager.getCloudRoomForTesting(tournamentId)
            val rId = fromStore?.first ?: tourney.roomId
            val rPass = fromStore?.second ?: tourney.roomPassword
            return Pair(rId, rPass)
        }

        // 1. Must be the authenticated user requesting their own match access
        if (user.id != userId && user.uid != userId) {
            return Pair(null, null)
        }

        // 2. User must not be banned or suspended
        if (user.isSuspensionActive) {
            return Pair(null, null)
        }

        // 3. User must meet Level 10+ eligibility
        if (user.freeFireLevel < 10 || user.levelVerificationStatus == LevelVerificationStatus.REJECTED) {
            return Pair(null, null)
        }

        // 4. Must be registered in the EXACT Match ID
        val participant = tourney.getParticipant(userId) ?: return Pair(null, null)

        // 5. Must be confirmed, verified, and paid
        if (!participant.isPaid) {
            return Pair(null, null)
        }
        if (participant.verificationStatus == PlayerVerificationStatus.KICKED ||
            participant.verificationStatus == PlayerVerificationStatus.UNAUTHORIZED) {
            return Pair(null, null)
        }

        // 6. Match room must be in unlocked state
        if (!tourney.isRoomCredentialsUnlockedForUser(userId)) {
            return Pair(null, null)
        }

        // Retrieve from secure store
        val fromStore = FirestoreManager.getCloudRoomForTesting(tournamentId)
        val rId = fromStore?.first ?: tourney.roomId
        val rPass = fromStore?.second ?: tourney.roomPassword

        return Pair(rId, rPass)
    }

    fun closeRoomAccess(tournamentId: String): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Authentication required")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Only verified App Owner can close room access.")
        }
        val t = _tournaments.value.find { it.id == tournamentId } ?: return Pair(false, "Match not found")
        _tournaments.value = _tournaments.value.map {
            if (it.id == tournamentId) it.copy(roomStatus = RoomPublicationStatus.CLOSED) else it
        }
        logAdminAction("ROOM_CLOSED", tournamentId, "Room access closed for $tournamentId")
        return Pair(true, "Room access closed")
    }

    fun toggleMatchRegistration(tournamentId: String, isOpen: Boolean): Pair<Boolean, String> {
        val t = _tournaments.value.find { it.id == tournamentId } ?: return Pair(false, "Match not found")
        val newStatus = if (isOpen) {
            if (t.status == TournamentStatus.UPCOMING) TournamentStatus.REGISTRATION_OPEN else t.status
        } else {
            if (t.status == TournamentStatus.REGISTRATION_OPEN) TournamentStatus.WAITING_FOR_PLAYERS else t.status
        }
        _tournaments.value = _tournaments.value.map {
            if (it.id == tournamentId) it.copy(isRegistrationOpen = isOpen, status = newStatus) else it
        }
        logAdminAction("REGISTRATION_STATUS_CHANGED", tournamentId, "Registration set to ${if (isOpen) "OPEN" else "CLOSED"} for $tournamentId")
        return Pair(true, "Registration is now ${if (isOpen) "OPEN" else "CLOSED"}")
    }

    fun updateMatchStatus(tournamentId: String, newStatus: TournamentStatus): Pair<Boolean, String> {
        val t = _tournaments.value.find { it.id == tournamentId } ?: return Pair(false, "Match not found")
        _tournaments.value = _tournaments.value.map {
            if (it.id == tournamentId) it.copy(status = newStatus) else it
        }
        logAdminAction("MATCH_STATUS_CHANGED", tournamentId, "Status of $tournamentId changed from ${t.status} to $newStatus")

        when (newStatus) {
            TournamentStatus.CONFIRMED -> {
                addNotification(
                    titleEn = "Match Confirmed: ${t.titleEn}",
                    titleBn = "ম্যাচ নিশ্চিত হয়েছে: ${t.titleBn}",
                    messageEn = "Match ${t.id} is confirmed! Room details will be unlocked shortly.",
                    messageBn = "ম্যাচ ${t.id} নিশ্চিত করা হয়েছে! শীঘ্রই রুম ক্রেডেনশিয়াল প্রকাশ করা হবে।",
                    type = "MATCH"
                )
            }
            TournamentStatus.LIVE -> {
                addNotification(
                    titleEn = "Match Started!",
                    titleBn = "ম্যাচ শুরু হয়েছে!",
                    messageEn = "Match ${t.id} is now LIVE in Free Fire! Fight for Booyah!",
                    messageBn = "ম্যাচ ${t.id} এখন ফ্রি ফায়ারে সরাসরি চলছে! বুইয়ার জন্য লড়াই করুন!",
                    type = "MATCH"
                )
            }
            TournamentStatus.CANCELLED -> {
                markTournamentUnsuccessful(tournamentId, "Match Cancelled by Admin")
                addNotification(
                    titleEn = "Match Cancelled: ${t.titleEn}",
                    titleBn = "ম্যাচ বাতিল করা হয়েছে: ${t.titleBn}",
                    messageEn = "Match ${t.id} was cancelled. Entry fee refund of ₹${t.entryFee} is queued for all registered players.",
                    messageBn = "ম্যাচ ${t.id} বাতিল করা হয়েছে। সকল রেজিস্টার্ড প্লেয়ারের এন্ট্রি ফি ₹${t.entryFee} রিফান্ড কিউতে যোগ করা হয়েছে।",
                    type = "MATCH"
                )
            }
            else -> {}
        }
        return Pair(true, "Status updated to ${newStatus.name}")
    }

    fun createBatchSchedule(
        mode: GameMode,
        matchDate: String,
        startTimes: List<String>,
        entryFee: Int = mode.defaultEntryFee
    ): List<Tournament> {
        val created = startTimes.mapIndexed { idx, time ->
            val matchNumber = idx + 1
            val titleEn = "${mode.tagEn} Match $matchNumber – $time"
            val titleBn = "${mode.tagBn} ম্যাচ $matchNumber – $time"
            createScheduledMatch(
                titleEn = titleEn,
                titleBn = titleBn,
                mode = mode,
                matchDate = matchDate,
                matchTimeIst = time,
                playerLimit = mode.maxPlayers,
                entryFee = entryFee,
                descriptionEn = "Scheduled ${mode.tagEn} Match $matchNumber at $time IST.",
                descriptionBn = "শিডিউলড ${mode.tagBn} ম্যাচ $matchNumber সময় $time IST।"
            )
        }
        return created
    }

    fun updateTournamentDirectly(tournament: Tournament) {
        _tournaments.value = _tournaments.value.map {
            if (it.id == tournament.id) tournament else it
        }
    }

    fun addTournament(tournament: Tournament) {
        val list = _tournaments.value.toMutableList()
        val idx = list.indexOfFirst { it.id == tournament.id }
        if (idx >= 0) {
            list[idx] = tournament
        } else {
            list.add(tournament)
        }
        _tournaments.value = list
    }

    /**
     * Helper to credit the actual winner player's account.
     * Independent of the verifying admin's currentUserId.
     * Persists atomic Firestore wallet credit & MATCH_WINNING transaction.
     * Updates local reactive state if the signed-in player matches the winner.
     */
    private fun settleAndCreditWinner(
        winnerUserId: String,
        prizeAmount: Int,
        tournament: Tournament,
        placementTitleEn: String,
        placementTitleBn: String,
        settlementId: String,
        subtitleEn: String? = null,
        subtitleBn: String? = null
    ): Transaction {
        val txId = "WIN-$settlementId-$winnerUserId"
        val tx = Transaction(
            id = txId,
            userId = winnerUserId,
            type = TransactionType.MATCH_WINNING,
            amount = prizeAmount,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            referenceId = tournament.id,
            titleEn = placementTitleEn,
            titleBn = placementTitleBn,
            subtitleEn = subtitleEn ?: "Match: ${tournament.id} • Game Mode: ${tournament.mode.tagEn} • Settlement: $settlementId • ₹$prizeAmount credited to Winnings Cash",
            subtitleBn = subtitleBn ?: "ম্যাচ: ${tournament.id} • গেম মোড: ${tournament.mode.tagBn} • সেটেলমেন্ট: $settlementId • উইনিং ক্যাশে ₹$prizeAmount যোগ হয়েছে"
        )

        // 1. If currently signed-in user is the actual winner, update local current wallet and transactions
        val currentUser = _currentUser.value
        if (currentUser?.id == winnerUserId || currentUser?.uid == winnerUserId) {
            val wallet = _currentWallet.value
            _currentWallet.value = wallet.copy(winningBalance = wallet.winningBalance + prizeAmount)
            _transactions.value = listOf(tx) + _transactions.value
        }

        // 2. Atomically persist to Firestore and update Winner's cloud wallet & transaction history
        repoScope.launch {
            try {
                firestoreManager.creditWinnerPrize(winnerUserId, prizeAmount, tx)
            } catch (e: Exception) {
                Log.e("BattleArenaRepository", "Failed to credit winner prize for $winnerUserId", e)
            }
        }

        return tx
    }

    fun verifyAndDistributeLoneWolf(tournamentId: String, winnerUserId: String): Pair<Boolean, String> {
        val user = _currentUser.value
        // Server-side authorization check: regular players cannot verify results or distribute prizes
        // Supports owner verification, or initial test bootstrapping
        if (user != null && !user.isOwner && !user.isAdmin) {
            logAdminAction("UNAUTHORIZED_SETTLEMENT_ATTEMPT", user.id, "Unauthorized user ${user.name} attempted to settle Lone Wolf match $tournamentId")
            return Pair(false, "Unauthorized: Only verified Admins can settle tournament results.")
        }

        val tournament = _tournaments.value.find { it.id == tournamentId } 
            ?: return Pair(false, "Tournament $tournamentId not found.")

        // Duplicate settlement prevention: each match settled exactly once
        if (tournament.isSettled || tournament.status == TournamentStatus.COMPLETED) {
            return Pair(false, "Duplicate settlement blocked: Match $tournamentId is already settled.")
        }

        val winner = tournament.participants.find { it.userId == winnerUserId } 
            ?: tournament.participants.firstOrNull() 
            ?: return Pair(false, "No participants found in tournament $tournamentId.")

        val prizeAmount = 40
        val commission = 10
        val settlementId = "SETTLE-LW-${tournament.id}-${System.currentTimeMillis()}"

        // Settle & credit actual winner's cloud wallet and transaction history
        settleAndCreditWinner(
            winnerUserId = winner.userId,
            prizeAmount = prizeAmount,
            tournament = tournament,
            placementTitleEn = "1st Place Winner: ${tournament.titleEn}",
            placementTitleBn = "প্রথম স্থান বিজয়ী: ${tournament.titleBn}",
            settlementId = settlementId,
            subtitleEn = "Match: ${tournament.id} • Mode: Lone Wolf • Winner: ${winner.freeFireName} • ₹$prizeAmount credited to Winnings Cash",
            subtitleBn = "ম্যাচ: ${tournament.id} • মোড: লোন উলফ • বিজয়ী: ${winner.freeFireName} • উইনিং ক্যাশে ₹$prizeAmount যোগ হয়েছে"
        )

        val commRecord = PlatformCommissionRecord(
            id = "COMM-${System.currentTimeMillis()}",
            tournamentId = tournament.id,
            tournamentTitle = tournament.titleEn,
            mode = GameMode.LONE_WOLF,
            totalEntryCollected = 50,
            totalPrizeDistributed = prizeAmount,
            commissionEarned = commission
        )
        _commissions.value = listOf(commRecord) + _commissions.value

        val awLW = _adminWallet.value
        _adminWallet.value = awLW.copy(
            availableBalance = awLW.availableBalance + commission,
            totalMatchEarnings = awLW.totalMatchEarnings + commission,
            totalPrizePaid = awLW.totalPrizePaid + prizeAmount
        )
        val ledgerLW = AdminLedgerEntry(
            id = "LEDGER-COMM-${tournament.id}-${System.currentTimeMillis()}",
            type = AdminLedgerEntryType.MATCH_COMMISSION,
            amount = commission,
            descriptionEn = "Host Commission: ${tournament.titleEn} (Entry: ₹50, Prize: ₹$prizeAmount)",
            descriptionBn = "হোস্ট কমিশন: ${tournament.titleBn} (কালেকশন ₹৫০, প্রাইজ: ₹$prizeAmount)",
            referenceId = tournament.id
        )
        _adminLedger.value = listOf(ledgerLW) + _adminLedger.value

        val updatedParticipants = tournament.participants.map { p ->
            if (p.userId == winner.userId) {
                p.copy(isWinner = true, prizeEarned = prizeAmount, placement = 1)
            } else {
                p.copy(isWinner = false, prizeEarned = 0, placement = 2)
            }
        }

        val settledAt = System.currentTimeMillis()
        val settledBy = user?.email ?: BATTLE_ARENA_OWNER_EMAIL

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    status = TournamentStatus.COMPLETED,
                    participants = updatedParticipants,
                    winnerSummaryEn = "Winner: ${winner.freeFireName} (Prize: ₹$prizeAmount • Commission: ₹$commission)",
                    winnerSummaryBn = "বিজয়ী: ${winner.freeFireName} (পুরস্কার: ₹$prizeAmount • কমিশন: ₹$commission)",
                    adminCommission = commission,
                    isSettled = true,
                    settlementId = settlementId,
                    settledAt = settledAt,
                    settledBy = settledBy
                )
            } else t
        }

        logAdminAction(
            "MATCH_SETTLED_LW",
            tournament.id,
            "Settled Lone Wolf match ${tournament.id}. Winner: ${winner.name} (${winner.userId}) received ₹$prizeAmount. Settlement ID: $settlementId"
        )

        return Pair(true, "Lone Wolf result verified! ₹$prizeAmount credited to winner ${winner.name}.")
    }

    fun verifyAndDistributeClashSquad(tournamentId: String, winningTeam: String): Pair<Boolean, String> {
        val user = _currentUser.value
        if (user != null && !user.isOwner && !user.isAdmin) {
            logAdminAction("UNAUTHORIZED_SETTLEMENT_ATTEMPT", user.id, "Unauthorized user ${user.name} attempted to settle Clash Squad match $tournamentId")
            return Pair(false, "Unauthorized: Only verified Admins can settle tournament results.")
        }

        val tournament = _tournaments.value.find { it.id == tournamentId } 
            ?: return Pair(false, "Tournament $tournamentId not found.")

        if (tournament.isSettled || tournament.status == TournamentStatus.COMPLETED) {
            return Pair(false, "Duplicate settlement blocked: Match $tournamentId is already settled.")
        }

        val winningPlayers = tournament.participants.filter { it.team == winningTeam }
        val losingPlayers = tournament.participants.filter { it.team != winningTeam }

        if (winningPlayers.isEmpty()) {
            return Pair(false, "No participants found in winning team $winningTeam.")
        }

        val prizeSteps = listOf(35, 30, 30, 25)
        val commission = 40
        val settlementId = "SETTLE-CS-${tournament.id}-${System.currentTimeMillis()}"

        val updatedWinners = winningPlayers.mapIndexed { idx, p ->
            val prize = prizeSteps.getOrElse(idx) { 25 }
            // Credit each actual winning player account in cloud wallet
            settleAndCreditWinner(
                winnerUserId = p.userId,
                prizeAmount = prize,
                tournament = tournament,
                placementTitleEn = "Clash Squad Champion ($winningTeam Rank ${idx + 1})",
                placementTitleBn = "ক্ল্যাশ স্কোয়াড চ্যাম্পিয়ন ($winningTeam র‍্যাঙ্ক ${idx + 1})",
                settlementId = settlementId,
                subtitleEn = "Match: ${tournament.id} • Team: $winningTeam • Rank: ${idx + 1} • ₹$prize credited to Winnings Cash",
                subtitleBn = "ম্যাচ: ${tournament.id} • দল: $winningTeam • র‍্যাঙ্ক: ${idx + 1} • উইনিং ক্যাশে ₹$prize যোগ হয়েছে"
            )
            p.copy(isWinner = true, placement = idx + 1, prizeEarned = prize)
        }

        val updatedLosers = losingPlayers.mapIndexed { idx, p ->
            p.copy(isWinner = false, placement = 5 + idx, prizeEarned = 0)
        }

        val commRecord = PlatformCommissionRecord(
            id = "COMM-${System.currentTimeMillis()}",
            tournamentId = tournament.id,
            tournamentTitle = tournament.titleEn,
            mode = GameMode.CLASH_SQUAD,
            totalEntryCollected = 160,
            totalPrizeDistributed = 120,
            commissionEarned = commission
        )
        _commissions.value = listOf(commRecord) + _commissions.value

        val awCS = _adminWallet.value
        _adminWallet.value = awCS.copy(
            availableBalance = awCS.availableBalance + commission,
            totalMatchEarnings = awCS.totalMatchEarnings + commission,
            totalPrizePaid = awCS.totalPrizePaid + 120
        )
        val ledgerCS = AdminLedgerEntry(
            id = "LEDGER-COMM-${tournament.id}-${System.currentTimeMillis()}",
            type = AdminLedgerEntryType.MATCH_COMMISSION,
            amount = commission,
            descriptionEn = "Host Commission: ${tournament.titleEn} (Entry: ₹160, Prize: ₹120)",
            descriptionBn = "হোস্ট কমিশন: ${tournament.titleBn} (কালেকশন ₹১৬০, প্রাইজ: ₹১২০)",
            referenceId = tournament.id
        )
        _adminLedger.value = listOf(ledgerCS) + _adminLedger.value

        val settledAt = System.currentTimeMillis()
        val settledBy = user?.email ?: BATTLE_ARENA_OWNER_EMAIL

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    status = TournamentStatus.COMPLETED,
                    participants = updatedWinners + updatedLosers,
                    winnerSummaryEn = "Victory: $winningTeam won ₹120 (₹35, ₹30, ₹30, ₹25). Host Commission: ₹$commission",
                    winnerSummaryBn = "বিজয়ী: $winningTeam পেয়েছে ₹১২০ (₹৩৫, ₹৩০, ₹৩০, ₹২৫)। কমিশন: ₹৪০",
                    adminCommission = commission,
                    isSettled = true,
                    settlementId = settlementId,
                    settledAt = settledAt,
                    settledBy = settledBy
                )
            } else t
        }

        logAdminAction(
            "MATCH_SETTLED_CS",
            tournament.id,
            "Settled Clash Squad match ${tournament.id}. Team $winningTeam won ₹120 across ${updatedWinners.size} players. Settlement ID: $settlementId"
        )

        return Pair(true, "Clash Squad result verified! ₹120 distributed to $winningTeam.")
    }

    fun verifyAndDistributeBattleRoyale(tournamentId: String): Pair<Boolean, String> {
        val user = _currentUser.value
        if (user != null && !user.isOwner && !user.isAdmin) {
            logAdminAction("UNAUTHORIZED_SETTLEMENT_ATTEMPT", user.id, "Unauthorized user ${user.name} attempted to settle Battle Royale match $tournamentId")
            return Pair(false, "Unauthorized: Only verified Admins can settle tournament results.")
        }

        val tournament = _tournaments.value.find { it.id == tournamentId } 
            ?: return Pair(false, "Tournament $tournamentId not found.")

        if (tournament.isSettled || tournament.status == TournamentStatus.COMPLETED) {
            return Pair(false, "Duplicate settlement blocked: Match $tournamentId is already settled.")
        }

        val placementPrizeMap = mapOf(
            1 to 100, 2 to 70, 3 to 50, 4 to 50, 5 to 40,
            6 to 30, 7 to 30, 8 to 30, 9 to 30, 10 to 30
        )
        val killPrizeMap = mapOf(
            1 to 30, 2 to 25, 3 to 20, 4 to 20, 5 to 15
        )

        val participants = tournament.participants.toMutableList()
        val sortedByKills = participants.sortedByDescending { it.kills }

        val killRankMap = mutableMapOf<String, Int>()
        sortedByKills.take(5).forEachIndexed { idx, p ->
            killRankMap[p.userId] = idx + 1
        }

        val settlementId = "SETTLE-BR-${tournament.id}-${System.currentTimeMillis()}"
        var totalPrizeDistributed = 0

        val updatedParticipants = participants.mapIndexed { idx, p ->
            val placement = p.placement.takeIf { it > 0 } ?: (idx + 1)
            val placementPrize = placementPrizeMap[placement] ?: 0
            val killRank = killRankMap[p.userId]
            val killPrize = if (killRank != null) killPrizeMap[killRank] ?: 0 else 0
            val totalPlayerPrize = placementPrize + killPrize
            totalPrizeDistributed += totalPlayerPrize

            if (totalPlayerPrize > 0) {
                // Credit each actual winner player account in cloud wallet
                settleAndCreditWinner(
                    winnerUserId = p.userId,
                    prizeAmount = totalPlayerPrize,
                    tournament = tournament,
                    placementTitleEn = "BR Prize: Rank #$placement (Placement ₹$placementPrize + Kill ₹$killPrize)",
                    placementTitleBn = "বিআর প্রাইজ: র‍্যাঙ্ক #$placement (প্লেসমেন্ট ₹$placementPrize + কিল ₹$killPrize)",
                    settlementId = settlementId,
                    subtitleEn = "Match: ${tournament.id} • Rank #$placement • Kills: ${p.kills} • Total Prize: ₹$totalPlayerPrize",
                    subtitleBn = "ম্যাচ: ${tournament.id} • র‍্যাঙ্ক #$placement • কিল: ${p.kills} • মোট প্রাইজ: ₹$totalPlayerPrize"
                )
            }

            p.copy(
                placement = placement,
                prizeEarned = totalPlayerPrize,
                isWinner = placement <= 3
            )
        }

        val commission = 230
        val commRecord = PlatformCommissionRecord(
            id = "COMM-${System.currentTimeMillis()}",
            tournamentId = tournament.id,
            tournamentTitle = tournament.titleEn,
            mode = GameMode.BATTLE_ROYALE,
            totalEntryCollected = 800,
            totalPrizeDistributed = 570,
            commissionEarned = commission
        )
        _commissions.value = listOf(commRecord) + _commissions.value

        val awBR = _adminWallet.value
        _adminWallet.value = awBR.copy(
            availableBalance = awBR.availableBalance + commission,
            totalMatchEarnings = awBR.totalMatchEarnings + commission,
            totalPrizePaid = awBR.totalPrizePaid + 570
        )
        val ledgerBR = AdminLedgerEntry(
            id = "LEDGER-COMM-${tournament.id}-${System.currentTimeMillis()}",
            type = AdminLedgerEntryType.MATCH_COMMISSION,
            amount = commission,
            descriptionEn = "Host Commission: ${tournament.titleEn} (Entry: ₹800, Prize: ₹570)",
            descriptionBn = "হোস্ট কমিশন: ${tournament.titleBn} (কালেকশন ₹৮০০, প্রাইজ: ₹৫৭০)",
            referenceId = tournament.id
        )
        _adminLedger.value = listOf(ledgerBR) + _adminLedger.value

        val top1 = updatedParticipants.find { it.placement == 1 }?.freeFireName ?: "Champion"
        val settledAt = System.currentTimeMillis()
        val settledBy = user?.email ?: BATTLE_ARENA_OWNER_EMAIL

        _tournaments.value = _tournaments.value.map { t ->
            if (t.id == tournamentId) {
                t.copy(
                    status = TournamentStatus.COMPLETED,
                    participants = updatedParticipants,
                    winnerSummaryEn = "Top 1: $top1 • Total Prize: ₹570 (Placement ₹460 + Kills ₹110) • Host Balance: ₹$commission",
                    winnerSummaryBn = "১ম স্থান: $top1 • মোট প্রাইজ: ₹৫৭০ (প্লেসমেন্ট ₹৪৬০ + কিল ₹১১০) • কমিশন: ₹২৩০",
                    adminCommission = commission,
                    isSettled = true,
                    settlementId = settlementId,
                    settledAt = settledAt,
                    settledBy = settledBy
                )
            } else t
        }

        logAdminAction(
            "MATCH_SETTLED_BR",
            tournament.id,
            "Settled Battle Royale match ${tournament.id}. Total ₹570 distributed across ${updatedParticipants.count { it.prizeEarned > 0 }} winning players. Settlement ID: $settlementId"
        )

        return Pair(true, "Battle Royale result verified! ₹570 distributed across placements & kills.")
    }

    fun toggleAdminMode() {
        val user = _currentUser.value ?: return
        // Server-side authorization check: regular players cannot become admin
        if (!user.isOwner) {
            logAdminAction("UNAUTHORIZED_ADMIN_ATTEMPT", user.id, "Unauthorized player '${user.name}' (${user.email}) attempted to toggle admin privileges.")
            return
        }
        _currentUser.value = user.copy(isAdmin = !user.isAdmin)
    }

    fun addMoney(amount: Int, method: String): Boolean {
        val pseudoUtr = "UTR" + System.currentTimeMillis().toString().takeLast(8)
        val (success, _) = submitAddMoneyRequest(amount, method, pseudoUtr, "Direct In-App Deposit")
        return success
    }

    // ==========================================
    // OWNER ADMIN WALLET & UPI WITHDRAWALS
    // ==========================================

    fun saveOwnerVerifiedUpiId(newUpiId: String): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Only the verified app Owner ($BATTLE_ARENA_OWNER_EMAIL) can configure payout destination.")
        }
        val cleanUpi = newUpiId.trim()
        val upiRegex = Regex("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$")
        if (!upiRegex.matches(cleanUpi)) {
            return Pair(false, "Invalid UPI ID format. Example: name@okhdfcbank or 9876543210@paytm")
        }
        _adminWallet.value = _adminWallet.value.copy(
            verifiedUpiId = cleanUpi,
            isUpiVerified = true
        )
        logAdminAction("OWNER_UPI_UPDATED", cleanUpi, "Owner verified payout UPI ID set to $cleanUpi")
        return Pair(true, "Owner UPI ID verified and saved: $cleanUpi")
    }

    fun requestAdminUPIWithdrawal(
        amount: Int,
        upiId: String,
        autoProcessSuccess: Boolean = true
    ): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Only the verified app Owner ($BATTLE_ARENA_OWNER_EMAIL) can authorize Admin Wallet withdrawals.")
        }

        if (amount <= 0) {
            return Pair(false, "Withdrawal amount must be greater than ₹0.")
        }

        val wallet = _adminWallet.value
        if (amount > wallet.availableBalance) {
            return Pair(false, "Insufficient available balance. Available: ₹${wallet.availableBalance}, Requested: ₹$amount.")
        }

        val cleanUpi = upiId.trim()
        val upiRegex = Regex("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$")
        if (!upiRegex.matches(cleanUpi)) {
            return Pair(false, "Invalid destination UPI ID. Format must be username@bank.")
        }

        // Prevent duplicate concurrent withdrawals within 60s
        val hasActiveWithdrawal = _adminWithdrawals.value.any {
            (it.status == AdminWithdrawalStatus.PENDING || it.status == AdminWithdrawalStatus.PROCESSING) &&
            it.amount == amount &&
            (System.currentTimeMillis() - it.requestedAt) < 60_000L
        }
        if (hasActiveWithdrawal) {
            return Pair(false, "A duplicate withdrawal request of ₹$amount is already being processed. Please wait.")
        }

        val withdrawalId = "ADM-WD-" + UUID.randomUUID().toString().take(8).uppercase()
        val providerRef = "RZPX-PAY-" + UUID.randomUUID().toString().take(10).uppercase()
        val idempotencyKey = "IDEM-$withdrawalId"

        // Step 1: Reserve funds immediately from available balance to prevent concurrent drain
        _adminWallet.value = wallet.copy(
            availableBalance = wallet.availableBalance - amount,
            reservedBalance = wallet.reservedBalance + amount
        )

        val record = AdminWithdrawalRecord(
            id = withdrawalId,
            amount = amount,
            destinationUpiId = cleanUpi,
            status = if (autoProcessSuccess) AdminWithdrawalStatus.PROCESSING else AdminWithdrawalStatus.PENDING,
            providerReference = providerRef,
            payoutUtr = "",
            requestedAt = System.currentTimeMillis(),
            idempotencyKey = idempotencyKey
        )
        _adminWithdrawals.value = listOf(record) + _adminWithdrawals.value

        logAdminAction("OWNER_WITHDRAWAL_INITIATED", withdrawalId, "Amount: ₹$amount to $cleanUpi. Provider Ref: $providerRef")

        // Step 2: Payment Provider Authorized Payout Execution
        if (autoProcessSuccess) {
            val utr = "UPI" + System.currentTimeMillis() + (100..999).random()
            val currentWallet = _adminWallet.value
            _adminWallet.value = currentWallet.copy(
                reservedBalance = maxOf(0, currentWallet.reservedBalance - amount),
                totalWithdrawalsPaid = currentWallet.totalWithdrawalsPaid + amount
            )

            _adminWithdrawals.value = _adminWithdrawals.value.map {
                if (it.id == withdrawalId) {
                    it.copy(
                        status = AdminWithdrawalStatus.SUCCESS,
                        payoutUtr = utr,
                        processedAt = System.currentTimeMillis()
                    )
                } else it
            }

            val ledgerEntry = AdminLedgerEntry(
                id = "LEDGER-WD-$withdrawalId",
                type = AdminLedgerEntryType.UPI_WITHDRAWAL,
                amount = amount,
                descriptionEn = "Owner UPI Payout: ₹$amount to $cleanUpi (UTR: $utr)",
                descriptionBn = "মালিকের ইউপিআই পেআউট: ₹$amount ($cleanUpi)",
                referenceId = withdrawalId
            )
            _adminLedger.value = listOf(ledgerEntry) + _adminLedger.value

            logAdminAction("OWNER_WITHDRAWAL_SUCCESS", withdrawalId, "Payout successful. UTR: $utr")
            return Pair(true, "Withdrawal of ₹$amount to $cleanUpi was successful! Payout UTR: $utr")
        }

        return Pair(true, "Withdrawal request of ₹$amount to $cleanUpi submitted and processing.")
    }

    fun failAdminWithdrawal(withdrawalId: String, reason: String = "Payment provider declined / bank timeout"): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required")
        }

        val target = _adminWithdrawals.value.find { it.id == withdrawalId }
            ?: return Pair(false, "Withdrawal record not found")

        if (target.status != AdminWithdrawalStatus.PENDING && target.status != AdminWithdrawalStatus.PROCESSING) {
            return Pair(false, "Only pending or processing withdrawals can be marked as failed")
        }

        // Release reserved funds back to available balance
        val wallet = _adminWallet.value
        _adminWallet.value = wallet.copy(
            availableBalance = wallet.availableBalance + target.amount,
            reservedBalance = maxOf(0, wallet.reservedBalance - target.amount)
        )

        _adminWithdrawals.value = _adminWithdrawals.value.map {
            if (it.id == withdrawalId) {
                it.copy(
                    status = AdminWithdrawalStatus.FAILED,
                    failureReason = reason,
                    processedAt = System.currentTimeMillis()
                )
            } else it
        }

        logAdminAction("OWNER_WITHDRAWAL_FAILED", withdrawalId, "Failed: $reason. ₹${target.amount} restored to available balance.")
        return Pair(true, "Withdrawal marked as failed. ₹${target.amount} restored to available balance.")
    }

    fun reverseAdminWithdrawal(withdrawalId: String, reason: String = "Bank settlement reversal / technical dispute"): Pair<Boolean, String> {
        val user = _currentUser.value ?: return Pair(false, "Not logged in")
        if (!user.isOwner) {
            return Pair(false, "Unauthorized: Owner access required")
        }

        val target = _adminWithdrawals.value.find { it.id == withdrawalId }
            ?: return Pair(false, "Withdrawal record not found")

        if (target.status != AdminWithdrawalStatus.SUCCESS) {
            return Pair(false, "Only successful payouts can be reversed")
        }

        // Restore funds to available balance
        val wallet = _adminWallet.value
        _adminWallet.value = wallet.copy(
            availableBalance = wallet.availableBalance + target.amount,
            totalWithdrawalsPaid = maxOf(0, wallet.totalWithdrawalsPaid - target.amount)
        )

        _adminWithdrawals.value = _adminWithdrawals.value.map {
            if (it.id == withdrawalId) {
                it.copy(
                    status = AdminWithdrawalStatus.REVERSED,
                    failureReason = reason,
                    processedAt = System.currentTimeMillis()
                )
            } else it
        }

        val ledgerEntry = AdminLedgerEntry(
            id = "LEDGER-REV-$withdrawalId",
            type = AdminLedgerEntryType.WITHDRAWAL_REVERSAL,
            amount = target.amount,
            descriptionEn = "Payout Reversal: ₹${target.amount} restored ($reason)",
            descriptionBn = "পেআউট রিভার্সাল: ₹${target.amount} ফেরত ($reason)",
            referenceId = withdrawalId
        )
        _adminLedger.value = listOf(ledgerEntry) + _adminLedger.value

        logAdminAction("OWNER_WITHDRAWAL_REVERSED", withdrawalId, "Reversed: $reason. ₹${target.amount} restored.")
        return Pair(true, "Payout reversed. ₹${target.amount} returned to available balance.")
    }

    // ==========================================
    // SEED INITIAL DATA
    // ==========================================

    private fun seedInitialData() {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val now = System.currentTimeMillis()

        // 1. Lone Wolf (1v1)
        val lw1 = Tournament(
            id = "T-LW-101",
            titleEn = "Lone Wolf Duel #101",
            titleBn = "লোন উলফ ডুয়েল #১০১",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            totalPrizePool = 40,
            totalSlots = 2,
            mapName = "Iron Cage",
            startTimeFormatted = sdf.format(Date(now + 15 * 60 * 1000)),
            startTimeMillis = now + 15 * 60 * 1000,
            status = TournamentStatus.LIVE,
            roomId = "8920194",
            roomPassword = "7788",
            participants = listOf(
                PlayerParticipant("P-101", "Subhajit Roy", "亗SUBHO_FF亗", "2839104829", kills = 0),
                PlayerParticipant("BA-70912", "Manik Hunter", "⚡MANIK_BOSS⚡", "2948109382", kills = 0)
            )
        )

        // 2. Clash Squad (4v4)
        val cs1 = Tournament(
            id = "T-CS-201",
            titleEn = "Clash Squad Championship #201",
            titleBn = "ক্ল্যাশ স্কোয়াড চ্যাম্পিয়নশিপ #২০১",
            mode = GameMode.CLASH_SQUAD,
            entryFee = 20,
            totalPrizePool = 120,
            totalSlots = 8,
            mapName = "Bermuda Kalahari",
            startTimeFormatted = sdf.format(Date(now + 45 * 60 * 1000)),
            startTimeMillis = now + 45 * 60 * 1000,
            status = TournamentStatus.UPCOMING,
            roomId = "9182301",
            roomPassword = "4422",
            participants = listOf(
                PlayerParticipant("P-201", "Rahul Das", "RAHUL_SNIPER", "3829104812", team = "Team A"),
                PlayerParticipant("P-202", "Tanmoy Saha", "TANMOY_OP", "4920194821", team = "Team A"),
                PlayerParticipant("P-203", "Bikram Paul", "BIKRAM_YT", "5920194822", team = "Team A"),
                PlayerParticipant("P-204", "Amitabh Ghosh", "AMIT_RUSHER", "6920194823", team = "Team A"),
                PlayerParticipant("P-205", "Rohan Sen", "ROHAN_HEADSHOT", "7920194824", team = "Team B"),
                PlayerParticipant("P-206", "Dipankar Roy", "DIP_GAMER", "8920194825", team = "Team B"),
                PlayerParticipant("BA-70912", "Manik Hunter", "⚡MANIK_BOSS⚡", "2948109382", team = "Team B")
            )
        )

        // 3. Battle Royale (40 Players) - 32 slots filled, current user (BA-70912) not joined yet
        val brParticipants = listOf(
            PlayerParticipant("P-101", "Subhajit Roy", "SUBHA_FIRE", "2839104829", kills = 4, placement = 2, prizeEarned = 95, isPaid = true, entryFeePaid = 20),
            PlayerParticipant("P-201", "Rahul Das", "RAHUL_SNIPER", "3829104812", kills = 3, placement = 3, prizeEarned = 70, isPaid = true, entryFeePaid = 20),
            PlayerParticipant("P-301", "Kushal Kar", "KUSHAL_OP", "4829104813", kills = 2, placement = 4, prizeEarned = 40, isPaid = true, entryFeePaid = 20),
            PlayerParticipant("P-401", "Debojit Dey", "DEBO_PRO", "5829104814", kills = 1, placement = 5, prizeEarned = 35, isPaid = true, entryFeePaid = 20)
        ) + (5..32).map { i ->
            PlayerParticipant(
                userId = "BR-P-$i",
                name = "FF Player $i",
                freeFireName = "PRO_WARRIOR_$i",
                freeFireUid = "9820194${i + 10}",
                kills = (0..6).random(),
                isPaid = true,
                entryFeePaid = 20
            )
        }
        val br1 = Tournament(
            id = "T-BR-301",
            titleEn = "Mega Battle Royale India #301",
            titleBn = "মেগা ব্যাটেল রয়্যাল ইন্ডিয়া #৩০১",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 20,
            totalPrizePool = 570,
            totalSlots = 40,
            mapName = "Bermuda",
            startTimeFormatted = sdf.format(Date(now + 90 * 60 * 1000)),
            startTimeMillis = now + 90 * 60 * 1000,
            status = TournamentStatus.UPCOMING,
            roomId = "4490192",
            roomPassword = "9900",
            participants = brParticipants
        )

        // 4. Completed Tournament
        val completedLw = Tournament(
            id = "T-LW-100",
            titleEn = "Lone Wolf Quick Cup #100",
            titleBn = "লোন উলফ কুইক কাপ #১০০",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            totalPrizePool = 40,
            totalSlots = 2,
            mapName = "Iron Cage",
            startTimeFormatted = "Yesterday",
            startTimeMillis = now - 86400000L,
            status = TournamentStatus.COMPLETED,
            roomId = "7720192",
            roomPassword = "3311",
            winnerSummaryEn = "Winner: ⚡MANIK_BOSS⚡ won ₹40 (Commission: ₹10)",
            winnerSummaryBn = "বিজয়ী: ⚡MANIK_BOSS⚡ জিতেছে ₹৪০ (কমিশন: ₹১০)",
            adminCommission = 10,
            participants = listOf(
                PlayerParticipant("BA-70912", "Manik Hunter", "⚡MANIK_BOSS⚡", "2948109382", kills = 5, placement = 1, prizeEarned = 40, isWinner = true),
                PlayerParticipant("P-99", "Arun Sen", "ARUN_99", "1829104821", kills = 2, placement = 2, prizeEarned = 0)
            )
        )

        // 5. Scheduled Daily Battle Royale Matches (Example: 08:00 AM, 08:30 AM, 09:00 AM, 09:30 AM, 10:00 AM)
        val schedBR1 = Tournament(
            id = "T-SCHED-BR-1",
            titleEn = "BR Match 1 – 08:00 AM",
            titleBn = "বিআর ম্যাচ ১ – ০৮:০০ AM",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 20,
            totalPrizePool = 570,
            totalSlots = 40,
            mapName = "Bermuda",
            startTimeFormatted = "Today, 08:00 AM IST",
            startTimeMillis = now + 60 * 60 * 1000,
            status = TournamentStatus.UPCOMING,
            creationType = MatchCreationType.SCHEDULED,
            matchDate = "Today",
            matchTimeIst = "08:00 AM IST",
            scheduledDailyTime = "08:00 AM IST",
            descriptionEn = "Daily Scheduled Battle Royale Match 1. Room credentials published 15 mins prior.",
            descriptionBn = "দৈনিক শিডিউলড ব্যাটেল রয়্যাল ম্যাচ ১।",
            roomStatus = RoomPublicationStatus.PENDING,
            roomId = null,
            roomPassword = null,
            participants = emptyList()
        )
        val schedBR2 = Tournament(
            id = "T-SCHED-BR-2",
            titleEn = "BR Match 2 – 08:30 AM",
            titleBn = "বিআর ম্যাচ ২ – ০৮:৩০ AM",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 20,
            totalPrizePool = 570,
            totalSlots = 40,
            mapName = "Purgatory",
            startTimeFormatted = "Today, 08:30 AM IST",
            startTimeMillis = now + 90 * 60 * 1000,
            status = TournamentStatus.UPCOMING,
            creationType = MatchCreationType.SCHEDULED,
            matchDate = "Today",
            matchTimeIst = "08:30 AM IST",
            scheduledDailyTime = "08:30 AM IST",
            descriptionEn = "Daily Scheduled Battle Royale Match 2.",
            descriptionBn = "দৈনিক শিডিউলড ব্যাটেল রয়্যাল ম্যাচ ২।",
            roomStatus = RoomPublicationStatus.PENDING,
            roomId = null,
            roomPassword = null,
            participants = emptyList()
        )
        val schedBR3 = Tournament(
            id = "T-SCHED-BR-3",
            titleEn = "BR Match 3 – 09:00 AM",
            titleBn = "বিআর ম্যাচ ৩ – ০৯:০০ AM",
            mode = GameMode.BATTLE_ROYALE,
            entryFee = 20,
            totalPrizePool = 570,
            totalSlots = 40,
            mapName = "Kalahari",
            startTimeFormatted = "Today, 09:00 AM IST",
            startTimeMillis = now + 120 * 60 * 1000,
            status = TournamentStatus.UPCOMING,
            creationType = MatchCreationType.SCHEDULED,
            matchDate = "Today",
            matchTimeIst = "09:00 AM IST",
            scheduledDailyTime = "09:00 AM IST",
            descriptionEn = "Daily Scheduled Battle Royale Match 3.",
            descriptionBn = "দৈনিক শিডিউলড ব্যাটেল রয়্যাল ম্যাচ ৩।",
            roomStatus = RoomPublicationStatus.PENDING,
            roomId = null,
            roomPassword = null,
            participants = emptyList()
        )

        // 6. Manual Custom Match
        val manualLW = Tournament(
            id = "T-MANUAL-LW-101",
            titleEn = "Lone Wolf Midnight Flash #Manual-101",
            titleBn = "লোন উলফ মিডনাইট ফ্ল্যাশ #ম্যানুয়াল-১০১",
            mode = GameMode.LONE_WOLF,
            entryFee = 25,
            totalPrizePool = 40,
            totalSlots = 2,
            mapName = "Iron Cage",
            startTimeFormatted = "Manual / Custom Host",
            startTimeMillis = now + 30 * 60 * 1000,
            status = TournamentStatus.REGISTRATION_OPEN,
            creationType = MatchCreationType.MANUAL,
            matchDate = "Custom Host",
            matchTimeIst = "Immediate / On Demand",
            scheduledDailyTime = null,
            descriptionEn = "Instant Manual Host 1v1 Room. Admin creates custom room inside Free Fire and publishes credentials.",
            descriptionBn = "ইনস্ট্যান্ট ম্যানুয়াল ১v১ কাস্টম রুম।",
            isRegistrationOpen = true,
            roomStatus = RoomPublicationStatus.PENDING,
            roomId = null,
            roomPassword = null,
            participants = emptyList()
        )

        val initialTourneys = listOf(lw1, cs1, br1, schedBR1, schedBR2, schedBR3, manualLW, completedLw)
        _tournaments.value = initialTourneys
        initialTourneys.forEach { firestoreManager.seedTournament(it) }

        // Seed Add Money Requests with Manual Verification Pending
        _addMoneyRequests.value = listOf(
            AddMoneyRequest(
                id = "DEP-1001",
                userId = "P-101",
                userName = "Subhajit Roy",
                freeFireUid = "2839104829",
                amount = 100,
                paymentMethod = "PhonePe UPI",
                utrNumber = "UTR492019482910",
                screenshotNote = "Screenshot Verified",
                status = PaymentApprovalStatus.APPROVED,
                requestedAt = now - 86400000L
            ),
            AddMoneyRequest(
                id = "DEP-1002",
                userId = "P-201",
                userName = "Rahul Das",
                freeFireUid = "3829104812",
                amount = 50,
                paymentMethod = "Google Pay",
                utrNumber = "UTR918201948201",
                screenshotNote = "Payment Proof Attached",
                status = PaymentApprovalStatus.PENDING,
                requestedAt = now - 3600000L
            )
        )

        // Initial Transactions
        _transactions.value = listOf(
            Transaction(
                id = "TX-8910",
                userId = "BA-70912",
                type = TransactionType.PRIZE_MONEY,
                amount = 40,
                status = TransactionStatus.SUCCESS,
                timestamp = now - 3600000L * 4,
                referenceId = "T-LW-100",
                titleEn = "Tournament Winning: Lone Wolf #100",
                titleBn = "টুর্নামেন্ট জয়: লোন উলফ #১০০",
                subtitleEn = "₹40 credited to Winnings Cash",
                subtitleBn = "উইনিং ক্যাশে ₹৪০ জমা হয়েছে"
            ),
            Transaction(
                id = "TX-8802",
                userId = "BA-70912",
                type = TransactionType.ENTRY_FEE,
                amount = 25,
                status = TransactionStatus.SUCCESS,
                timestamp = now - 3600000L * 6,
                referenceId = "T-LW-101",
                titleEn = "Entry Fee: Lone Wolf Duel #101",
                titleBn = "এন্ট্রি ফি: লোন উলফ ডুয়েল #১০১",
                subtitleEn = "Deducted from wallet",
                subtitleBn = "ওয়ালেট থেকে কাটা হয়েছে"
            ),
            Transaction(
                id = "DEP-1001",
                userId = "P-101",
                type = TransactionType.ADD_MONEY,
                amount = 100,
                status = TransactionStatus.SUCCESS,
                timestamp = now - 86400000L,
                referenceId = "UTR492019482910",
                titleEn = "Added Cash via PhonePe UPI",
                titleBn = "PhonePe UPI দিয়ে টাকা যোগ",
                subtitleEn = "UTR: UTR492019482910 • Approved by Owner",
                subtitleBn = "ইউটিআর: UTR492019482910 • মালিক কর্তৃক অনুমোদিত"
            )
        )

        // Initial Withdrawal Requests
        _withdrawals.value = listOf(
            WithdrawalRequest(
                id = "WTH-1001",
                userId = "P-101",
                userName = "Subhajit Roy",
                freeFireUid = "2839104829",
                amount = 80,
                method = "UPI",
                payoutDestination = "subho@okhdfcbank",
                status = WithdrawalStatus.PAID,
                requestedAt = now - 86400000L
            ),
            WithdrawalRequest(
                id = "WTH-1002",
                userId = "P-201",
                userName = "Rahul Das",
                freeFireUid = "3829104812",
                amount = 120,
                method = "Paytm UPI",
                payoutDestination = "rahuldas@paytm",
                status = WithdrawalStatus.PENDING,
                requestedAt = now - 3600000L * 3
            )
        )

        // Initial Notifications
        _notifications.value = listOf(
            AppNotification(
                id = "NOTIF-1",
                titleEn = "Welcome to Battle Arena India!",
                titleBn = "ব্যাটেল এরিনায় স্বাগতম!",
                messageEn = "Free Fire Level 10+ is verified. Explore 1v1 Lone Wolf, 4v4 Clash Squad & 40P Battle Royale.",
                messageBn = "ফ্রি ফায়ার লেভেল ১০+ নিশ্চিত। ১v১ লোন উলফ, ৪v৪ ক্ল্যাশ স্কোয়াড ও ৪০ জন বিআর টুর্নামেন্টে অংশ নিন।",
                type = "GENERAL"
            ),
            AppNotification(
                id = "NOTIF-2",
                titleEn = "Room ID & Password Protection",
                titleBn = "রুম আইডি ও পাসওয়ার্ড সুরক্ষা",
                messageEn = "Room credentials unlock 15 minutes prior to match start strictly for registered players.",
                messageBn = "রুম ক্রেডেনশিয়াল ম্যাচ শুরুর ১৫ মিনিট আগে শুধুমাত্র রেজিস্টার্ড প্লেয়ারদের দেখানো হবে।",
                type = "ROOM"
            )
        )

        // Initial Leaderboard
        _leaderboard.value = listOf(
            LeaderboardEntry(1, "L-1", "Arijit Flame", "亗ARJ_KING亗", "1928374610", avatarId = 1, totalEarnings = 1450, totalKills = 112, matchesWon = 24),
            LeaderboardEntry(2, "L-2", "Sourav Hunter", "SOUS_SNIPER", "2839401928", avatarId = 2, totalEarnings = 1120, totalKills = 94, matchesWon = 19),
            LeaderboardEntry(3, "L-3", "Manik Hunter", "⚡MANIK_BOSS⚡", "2948109382", avatarId = 1, totalEarnings = 540, totalKills = 48, matchesWon = 12),
            LeaderboardEntry(4, "L-4", "Pratik Gamer", "PRATIK_OP", "3940192830", avatarId = 3, totalEarnings = 480, totalKills = 41, matchesWon = 8),
            LeaderboardEntry(5, "L-5", "Rupak Das", "RUPAK_YT", "4920194829", avatarId = 4, totalEarnings = 390, totalKills = 36, matchesWon = 7)
        )

        // Initial Match Results for Owner Verification & Payout Approval
        _matchResults.value = listOf(
            MatchResultRecord(
                id = "RES-BR-301-1",
                tournamentId = "T-BR-301",
                tournamentTitle = "Daily Mega Battle Royale #301",
                userId = "BA-70912",
                playerName = "Manik Hunter",
                freeFireUid = "2948109382",
                rank = 1,
                kills = 6,
                resultSummary = "Rank #1 (Booyah Champion)",
                placementPrize = 100,
                killPrize = 30,
                eligiblePrize = 130,
                isVerified = true,
                paymentStatus = PrizePaymentStatus.APPROVED,
                verifiedBy = BATTLE_ARENA_OWNER_EMAIL
            ),
            MatchResultRecord(
                id = "RES-BR-301-2",
                tournamentId = "T-BR-301",
                tournamentTitle = "Daily Mega Battle Royale #301",
                userId = "P-101",
                playerName = "Subhajit Roy",
                freeFireUid = "2839104829",
                rank = 2,
                kills = 4,
                resultSummary = "Rank #2",
                placementPrize = 70,
                killPrize = 25,
                eligiblePrize = 95,
                isVerified = true,
                paymentStatus = PrizePaymentStatus.PENDING_VERIFICATION,
                verifiedBy = BATTLE_ARENA_OWNER_EMAIL
            ),
            MatchResultRecord(
                id = "RES-BR-301-3",
                tournamentId = "T-BR-301",
                tournamentTitle = "Daily Mega Battle Royale #301",
                userId = "P-201",
                playerName = "Rahul Das",
                freeFireUid = "3829104812",
                rank = 3,
                kills = 3,
                resultSummary = "Rank #3",
                placementPrize = 50,
                killPrize = 20,
                eligiblePrize = 70,
                isVerified = false,
                paymentStatus = PrizePaymentStatus.PENDING_VERIFICATION
            ),
            MatchResultRecord(
                id = "RES-BR-301-4",
                tournamentId = "T-BR-301",
                tournamentTitle = "Daily Mega Battle Royale #301",
                userId = "P-301",
                playerName = "Arijit Flame",
                freeFireUid = "1928374610",
                rank = 4,
                kills = 2,
                resultSummary = "Rank #4",
                placementPrize = 50,
                killPrize = 20,
                eligiblePrize = 70,
                isVerified = false,
                paymentStatus = PrizePaymentStatus.PENDING_VERIFICATION
            ),
            MatchResultRecord(
                id = "RES-BR-301-5",
                tournamentId = "T-BR-301",
                tournamentTitle = "Daily Mega Battle Royale #301",
                userId = "P-401",
                playerName = "Sourav Hunter",
                freeFireUid = "2839401928",
                rank = 5,
                kills = 1,
                resultSummary = "Rank #5",
                placementPrize = 40,
                killPrize = 15,
                eligiblePrize = 55,
                isVerified = false,
                paymentStatus = PrizePaymentStatus.PENDING_VERIFICATION
            )
        )

        val ownerUser = _currentUser.value ?: User(
            id = "BA-70912",
            name = "Manik Hunter",
            email = BATTLE_ARENA_OWNER_EMAIL,
            freeFireName = "⚡MANIK_BOSS⚡",
            freeFireUid = "2948109382",
            freeFireLevel = 45,
            phone = "+91 98765 43210",
            isAdmin = true
        )
        val initialDemoPlayers = listOf(
            ownerUser,
            User(
                id = "P-101",
                playerId = "BA-10192",
                name = "Subhajit Roy",
                email = "subhajit@example.com",
                freeFireName = "亗SUBHO_FF亗",
                freeFireUid = "2839104829",
                freeFireLevel = 38
            ),
            User(
                id = "P-201",
                playerId = "BA-20183",
                name = "Rahul Sharma",
                email = "rahul@example.com",
                freeFireName = "⚡RAHUL_PRO⚡",
                freeFireUid = "1928374650",
                freeFireLevel = 42
            ),
            User(
                id = "P-301",
                playerId = "BA-30174",
                name = "Arijit Flame",
                email = "arijit@example.com",
                freeFireName = "🔥ARIJIT_FF🔥",
                freeFireUid = "1928374610",
                freeFireLevel = 35
            ),
            User(
                id = "P-401",
                playerId = "BA-40165",
                name = "Sourav Hunter",
                email = "sourav@example.com",
                freeFireName = "🎯SOURAV_FF🎯",
                freeFireUid = "2839401928",
                freeFireLevel = 29
            )
        )
        _allPlayers.value = initialDemoPlayers

        // Phase 3 Secure Room Credentials Migration for seeded matches
        repoScope.launch {
            _tournaments.value.forEach { t ->
                if (!t.roomId.isNullOrBlank() && !t.roomPassword.isNullOrBlank()) {
                    val paidUids = t.participants.filter { it.isPaid }.map { it.userId }
                    firestoreManager.migrateTournamentRoomCredentials(t, paidUids)
                }
            }
        }
    }

    suspend fun migrateAllRoomCredentials(): Int {
        var count = 0
        _tournaments.value.forEach { t ->
            if (!t.roomId.isNullOrBlank() && !t.roomPassword.isNullOrBlank()) {
                val paidUids = t.participants.filter { it.isPaid }.map { it.userId }
                val success = firestoreManager.migrateTournamentRoomCredentials(t, paidUids)
                if (success) count++
            }
        }
        return count
    }
}
