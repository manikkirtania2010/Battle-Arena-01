package com.example.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.model.AppLanguage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Manages Firebase Cloud Messaging (FCM) channels, token registration,
 * topic subscriptions, and system push notifications for:
 * 1. Tournament Registration Reminders
 * 2. Match Start Times & Room Access
 * 3. Prize Distribution Alerts
 */
object BattleArenaNotificationManager {

    private const val TAG = "BattleArenaFCM"

    // Notification Channel IDs
    const val CHANNEL_TOURNAMENT_REMINDERS = "ba_channel_tournament_reminders"
    const val CHANNEL_MATCH_STARTS = "ba_channel_match_starts"
    const val CHANNEL_PRIZE_DISTRIBUTION = "ba_channel_prize_distribution"
    const val CHANNEL_SYSTEM = "ba_channel_system"

    // FCM Topics
    const val TOPIC_ALL_PLAYERS = "all_players"
    const val TOPIC_TOURNAMENT_REMINDERS = "tournament_reminders"
    const val TOPIC_MATCH_STARTS = "match_starts"
    const val TOPIC_PRIZE_DISTRIBUTION = "prize_distribution"

    // Notification IDs
    private const val NOTIF_ID_TOURNAMENT_BASE = 1000
    private const val NOTIF_ID_MATCH_BASE = 2000
    private const val NOTIF_ID_PRIZE_BASE = 3000
    private const val NOTIF_ID_GENERAL_BASE = 4000

    /**
     * Creates High/Max-importance Notification Channels for Android 8.0+ (API 26+)
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            // 1. Tournament Registration Reminders Channel
            val tournamentChannel = NotificationChannel(
                CHANNEL_TOURNAMENT_REMINDERS,
                "Tournament Reminders / টুর্নামেন্ট অনুস্মারক",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for tournament registration closing and upcoming tournament slots."
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // 2. Match Start Times Channel
            val matchChannel = NotificationChannel(
                CHANNEL_MATCH_STARTS,
                "Match Start Times & Rooms / ম্যাচ শুরুর সময় ও রুম",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Live match countdowns, room credentials, and match start alerts."
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // 3. Prize Distribution Alerts Channel
            val prizeChannel = NotificationChannel(
                CHANNEL_PRIZE_DISTRIBUTION,
                "Prize & Winnings Alerts / পুরস্কার ও জয়ের অ্যালার্ট",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Prize money credits, match winning payouts, and wallet balance updates."
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // 4. System & Security Channel
            val systemChannel = NotificationChannel(
                CHANNEL_SYSTEM,
                "Battle Arena System & Fair Play",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Account verification, UTR approvals, and system announcements."
                setShowBadge(true)
            }

            notificationManager.createNotificationChannels(
                listOf(tournamentChannel, matchChannel, prizeChannel, systemChannel)
            )
            Log.d(TAG, "FCM Notification Channels created successfully")
        }
    }

    /**
     * Initializes FCM, fetches registration token, and subscribes to essential topics.
     */
    fun initFcm(context: Context, onTokenRetrieved: (String) -> Unit = {}) {
        createNotificationChannels(context)

        try {
            // Ensure FirebaseApp is initialized before accessing FirebaseMessaging
            val firebaseApp = if (FirebaseApp.getApps(context).isEmpty()) {
                val app = try {
                    FirebaseApp.initializeApp(context)
                } catch (e: Throwable) {
                    null
                }
                app ?: run {
                    try {
                        val options = FirebaseOptions.Builder()
                            .setApplicationId(context.packageName)
                            .setApiKey("AIzaSyDummyKeyForBattleArenaClientPush")
                            .setProjectId("battle-arena-esports")
                            .setGcmSenderId("103953800507")
                            .build()
                        FirebaseApp.initializeApp(context, options)
                    } catch (e2: Throwable) {
                        Log.w(TAG, "Fallback FirebaseApp options init skipped", e2)
                        null
                    }
                }
            } else {
                FirebaseApp.getInstance()
            }

            if (firebaseApp != null) {
                try {
                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                Log.d(TAG, "Firebase Cloud Messaging Token: $token")
                                saveToken(context, token)
                                safeUpdateRepoToken(token)
                                onTokenRetrieved(token)
                            } else {
                                Log.w(TAG, "Fetching FCM registration token failed, using local token", task.exception)
                                fallbackLocalToken(context, onTokenRetrieved)
                            }
                        }

                    // Subscribe to default push topics
                    try {
                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL_PLAYERS)
                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_TOURNAMENT_REMINDERS)
                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_MATCH_STARTS)
                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_PRIZE_DISTRIBUTION)
                    } catch (e: Throwable) {
                        Log.w(TAG, "Error subscribing to FCM topics", e)
                    }
                } catch (e: Throwable) {
                    Log.w(TAG, "FirebaseMessaging not available in this environment, using local fallback", e)
                    fallbackLocalToken(context, onTokenRetrieved)
                }
            } else {
                fallbackLocalToken(context, onTokenRetrieved)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error initializing FCM messaging", e)
            fallbackLocalToken(context, onTokenRetrieved)
        }
    }

    private fun safeUpdateRepoToken(token: String) {
        try {
            BattleArenaRepository.getInstance().updateFcmToken(token)
        } catch (e: Throwable) {
            // Ignore if repository not initialized yet
        }
    }

    private fun fallbackLocalToken(context: Context, onTokenRetrieved: (String) -> Unit = {}) {
        var token = getStoredToken(context)
        if (token.isBlank()) {
            token = "fcm_token_ba_" + (100000..999999).random()
            saveToken(context, token)
        }
        safeUpdateRepoToken(token)
        onTokenRetrieved(token)
    }

    private fun saveToken(context: Context, token: String) {
        try {
            val prefs = context.getSharedPreferences("battle_arena_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("fcm_token", token).apply()
        } catch (e: Throwable) {
            // Ignore
        }
    }

    fun getStoredToken(context: Context): String {
        return try {
            val prefs = context.getSharedPreferences("battle_arena_prefs", Context.MODE_PRIVATE)
            prefs.getString("fcm_token", "") ?: ""
        } catch (e: Throwable) {
            ""
        }
    }

    /**
     * 1. TOURNAMENT REGISTRATION REMINDER PUSH
     */
    fun dispatchTournamentReminder(
        context: Context,
        tournamentTitle: String,
        tournamentId: String,
        startsIn: String = "15 mins",
        language: AppLanguage = AppLanguage.ENGLISH
    ) {
        val titleEn = "⏰ Registration Reminder: $tournamentTitle"
        val titleBn = "⏰ টুর্নামেন্ট অনুস্মারক: $tournamentTitle"
        val bodyEn = "Hurry! Match starts in $startsIn. Complete registration now to secure your slot."
        val bodyBn = "দ্রুত করুন! মেচ শুরু হতে আর $startsIn বাকি। আপনার স্লট নিশ্চিত করতে এখনই রেজিস্ট্রেশন করুন।"

        val title = if (language == AppLanguage.BENGALI) titleBn else titleEn
        val body = if (language == AppLanguage.BENGALI) bodyBn else bodyEn

        showSystemNotification(
            context = context,
            channelId = CHANNEL_TOURNAMENT_REMINDERS,
            notificationId = NOTIF_ID_TOURNAMENT_BASE + (tournamentId.hashCode() % 500),
            title = title,
            body = body,
            extraTarget = "tournaments",
            extraId = tournamentId
        )

        // Also record in in-app notifications safely
        safeAddNotification(
            titleEn = titleEn,
            titleBn = titleBn,
            messageEn = bodyEn,
            messageBn = bodyBn,
            type = "TOURNAMENT"
        )
    }

    /**
     * 2. MATCH START TIME & ROOM ACCESS PUSH
     */
    fun dispatchMatchStartAlert(
        context: Context,
        tournamentTitle: String,
        tournamentId: String,
        roomId: String? = null,
        roomPassword: String? = null,
        language: AppLanguage = AppLanguage.ENGLISH
    ) {
        val titleEn = "🎮 Match Starting Now: $tournamentTitle"
        val titleBn = "🎮 ম্যাচ শুরু হচ্ছে: $tournamentTitle"

        val bodyEn = if (!roomId.isNullOrBlank()) {
            "Room ID: $roomId | Pass: $roomPassword. Join the custom room immediately!"
        } else {
            "Your Battle Arena match is beginning! Open the app to view Room ID & Password."
        }

        val bodyBn = if (!roomId.isNullOrBlank()) {
            "রুম আইডি: $roomId | পাসওয়ার্ড: $roomPassword. দ্রুত কাস্টম রুমে প্রবেশ করুন!"
        } else {
            "আপনার ব্যাটেল এরিনা ম্যাচ শুরু হচ্ছে! রুম আইডি ও পাসওয়ার্ড দেখতে অ্যাপ খুলুন।"
        }

        val title = if (language == AppLanguage.BENGALI) titleBn else titleEn
        val body = if (language == AppLanguage.BENGALI) bodyBn else bodyEn

        showSystemNotification(
            context = context,
            channelId = CHANNEL_MATCH_STARTS,
            notificationId = NOTIF_ID_MATCH_BASE + (tournamentId.hashCode() % 500),
            title = title,
            body = body,
            extraTarget = "match_room",
            extraId = tournamentId
        )

        safeAddNotification(
            titleEn = titleEn,
            titleBn = titleBn,
            messageEn = bodyEn,
            messageBn = bodyBn,
            type = "ROOM"
        )
    }

    /**
     * 3. PRIZE DISTRIBUTION PUSH ALERT
     */
    fun dispatchPrizeDistributionAlert(
        context: Context,
        tournamentTitle: String,
        prizeAmount: Int,
        rank: Int,
        language: AppLanguage = AppLanguage.ENGLISH
    ) {
        val titleEn = "🏆 Prize Won! ₹$prizeAmount Credited"
        val titleBn = "🏆 প্রাইজ মানি বিজয়ী! ₹$prizeAmount জমা হয়েছে"

        val bodyEn = "Congratulations! You secured Rank #$rank in $tournamentTitle. ₹$prizeAmount has been credited to your Virtual Prize Wallet."
        val bodyBn = "অভিনন্দন! $tournamentTitle টুর্নামেন্টে আপনি Rank #$rank স্থান অধিকার করেছেন। ₹$prizeAmount আপনার ভার্চুয়াল প্রাইজ ওয়ালেটে জমা হয়েছে।"

        val title = if (language == AppLanguage.BENGALI) titleBn else titleEn
        val body = if (language == AppLanguage.BENGALI) bodyBn else bodyEn

        showSystemNotification(
            context = context,
            channelId = CHANNEL_PRIZE_DISTRIBUTION,
            notificationId = NOTIF_ID_PRIZE_BASE + (System.currentTimeMillis() % 500).toInt(),
            title = title,
            body = body,
            extraTarget = "wallet",
            extraId = "prize"
        )

        safeAddNotification(
            titleEn = titleEn,
            titleBn = titleBn,
            messageEn = bodyEn,
            messageBn = bodyBn,
            type = "PAYMENT"
        )
    }

    private fun safeAddNotification(
        titleEn: String,
        titleBn: String,
        messageEn: String,
        messageBn: String,
        type: String
    ) {
        try {
            BattleArenaRepository.getInstance().addNotification(
                titleEn = titleEn,
                titleBn = titleBn,
                messageEn = messageEn,
                messageBn = messageBn,
                type = type
            )
        } catch (e: Throwable) {
            Log.w(TAG, "Could not add in-app notification", e)
        }
    }

    /**
     * Displays a native Android notification in the system notification shade.
     */
    fun showSystemNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        body: String,
        extraTarget: String = "",
        extraId: String = ""
    ) {
        // Permission check for Android 13+ (TIRAMISU / API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted. Notification shown only in-app.")
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("target_screen", extraTarget)
            putExtra("target_id", extraId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
            Log.d(TAG, "Dispatched system push notification: id=$notificationId, title=$title")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException sending notification", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending notification", e)
        }
    }
}
