package com.example.data

import android.util.Log
import com.example.model.AppLanguage
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging Service for Battle Arena.
 * Receives downstream push messages from Firebase Cloud Messaging servers,
 * parses tournament reminders, match start alerts, and prize distributions,
 * and posts high-priority notifications.
 */
class BattleArenaMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Registration Token: $token")
        try {
            BattleArenaRepository.getInstance().updateFcmToken(token)
        } catch (e: Throwable) {
            // Ignore
        }
        try {
            val prefs = getSharedPreferences("battle_arena_prefs", MODE_PRIVATE)
            prefs.edit().putString("fcm_token", token).apply()
        } catch (e: Throwable) {
            // Ignore
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        // 1. Extract data payload
        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val type = data["type"] ?: data["notification_type"] ?: "GENERAL"
        val tournamentTitle = data["tournament_title"] ?: data["title"] ?: notification?.title ?: "Battle Arena Tournament"
        val tournamentId = data["tournament_id"] ?: "TOURNAMENT-1"
        val messageBody = data["body"] ?: notification?.body ?: "New Battle Arena notification"

        val language = try {
            BattleArenaRepository.getInstance().currentLanguage.value
        } catch (e: Throwable) {
            AppLanguage.ENGLISH
        }

        when (type.uppercase()) {
            "TOURNAMENT_REMINDER", "REMINDER" -> {
                val startsIn = data["starts_in"] ?: "15 mins"
                BattleArenaNotificationManager.dispatchTournamentReminder(
                    context = applicationContext,
                    tournamentTitle = tournamentTitle,
                    tournamentId = tournamentId,
                    startsIn = startsIn,
                    language = language
                )
            }
            "MATCH_START", "MATCH_STARTING", "ROOM_UNLOCKED" -> {
                val roomId = data["room_id"]
                val password = data["room_password"]
                BattleArenaNotificationManager.dispatchMatchStartAlert(
                    context = applicationContext,
                    tournamentTitle = tournamentTitle,
                    tournamentId = tournamentId,
                    roomId = roomId,
                    roomPassword = password,
                    language = language
                )
            }
            "PRIZE_DISTRIBUTION", "PRIZE_CREDITED", "WINNINGS" -> {
                val prizeAmount = data["prize_amount"]?.toIntOrNull() ?: 100
                val rank = data["rank"]?.toIntOrNull() ?: 1
                BattleArenaNotificationManager.dispatchPrizeDistributionAlert(
                    context = applicationContext,
                    tournamentTitle = tournamentTitle,
                    prizeAmount = prizeAmount,
                    rank = rank,
                    language = language
                )
            }
            else -> {
                val title = notification?.title ?: data["title"] ?: "Battle Arena"
                val body = notification?.body ?: messageBody
                BattleArenaNotificationManager.showSystemNotification(
                    context = applicationContext,
                    channelId = BattleArenaNotificationManager.CHANNEL_SYSTEM,
                    notificationId = (System.currentTimeMillis() % 10000).toInt(),
                    title = title,
                    body = body
                )
                try {
                    BattleArenaRepository.getInstance().addNotification(
                        titleEn = title,
                        titleBn = title,
                        messageEn = body,
                        messageBn = body,
                        type = "GENERAL"
                    )
                } catch (e: Throwable) {
                    // Ignore
                }
            }
        }
    }

    companion object {
        private const val TAG = "BAMessagingService"
    }
}
