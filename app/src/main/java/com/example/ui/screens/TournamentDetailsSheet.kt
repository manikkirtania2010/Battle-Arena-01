package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.GameMode
import com.example.model.Tournament
import com.example.model.TournamentStatus
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

@Composable
fun TournamentDetailsSheet(
    tournament: Tournament,
    isJoined: Boolean,
    currentUserId: String?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onJoin: () -> Unit,
    onCancelWaitingPlayer: (String) -> Unit = {},
    onReportCheater: (tournament: Tournament, targetPlayerId: String?) -> Unit = { _, _ -> },
    onFetchSecureRoomCredentials: (tournamentId: String, userId: String) -> Pair<String?, String?> = { _, _ -> Pair(null, null) }
) {
    val context = LocalContext.current
    val modeColor = when (tournament.mode) {
        GameMode.LONE_WOLF -> GamingPrimary
        GameMode.CLASH_SQUAD -> GamingSecondary
        GameMode.BATTLE_ROYALE -> GamingGold
    }

    fun openFreeFireApp(ctx: Context) {
        val pm = ctx.packageManager
        val packages = listOf("com.dts.freefireth", "com.dts.freefiremax")
        for (pkg in packages) {
            val launchIntent = pm.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                ctx.startActivity(launchIntent)
                Toast.makeText(ctx, "Opening Free Fire... Please enter Room ID & Password in Custom Room.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        try {
            val storeIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=com.dts.freefiremax"))
            ctx.startActivity(storeIntent)
        } catch (e: Exception) {
            Toast.makeText(ctx, "Free Fire / Free Fire MAX is not installed on this device.", Toast.LENGTH_LONG).show()
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("tournament_details_dialog"),
            color = GamingSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(modeColor.copy(alpha = 0.2f))
                            .border(1.dp, modeColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tournament.getModeTitle(language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = modeColor
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GamingSurfaceVariant)
                            .testTag("close_details_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = tournament.getTitle(language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Map: ${tournament.mapName} • Start: ${tournament.startTimeFormatted}",
                    fontSize = 12.sp,
                    color = GamingSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Matrix: Entry Fee | Total Prize | Slots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GamingDarkBackground)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = AppStrings.entryFee(language), fontSize = 11.sp, color = TextSecondary)
                        Text(text = "₹${tournament.entryFee}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GamingPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = AppStrings.prizePool(language), fontSize = 11.sp, color = TextSecondary)
                        Text(text = "₹${tournament.totalPrizePool}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GamingGold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = AppStrings.slots(language), fontSize = 11.sp, color = TextSecondary)
                        Text(text = "${tournament.filledSlots}/${tournament.totalSlots}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Synchronized 45-Second Countdown Alert Card
                if (tournament.status == TournamentStatus.CONFIRMED && tournament.countdownStartMillis != null) {
                    val remainingSeconds = tournament.getRemainingCountdownSeconds()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("countdown_timer_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingGold.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingGold)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Timer", tint = GamingGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI)
                                        "⚡ ম্যাচ শুরু হতে বাকি: $remainingSeconds সেকেন্ড"
                                        else "⚡ MATCH STARTING IN $remainingSeconds SECONDS!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GamingGold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "সকল প্লেয়ার নিশ্চিত! রুম আইডি ও পাসওয়ার্ড দিয়ে ফ্রি ফায়ারে প্রস্তুত হন।"
                                    else "All slots filled! Room ID & Password unlocked. Enter Free Fire custom room now.",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Lone Wolf Matchmaking (Waiting for 2nd player)
                if (tournament.status == TournamentStatus.MATCHMAKING && tournament.mode == GameMode.LONE_WOLF) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("matchmaking_waiting_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSecondary.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingSecondary)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Groups, contentDescription = "Matchmaking", tint = GamingSecondary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "⚔️ লোন উলফ ম্যাচমেকিং (১/২ খেলোয়াড়)" else "⚔️ Lone Wolf Matchmaking (1/2 Players)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "দ্বিতীয় খেলোয়াড় যোগদানের জন্য অপেক্ষা করা হচ্ছে। প্রতিপক্ষ যোগ দিলে ৪৫ সেকেন্ডের কাউন্টডাউন শুরু হবে।"
                                    else "Waiting for Player 2 to join. 45-second countdown will start immediately once opponent joins.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            if (isJoined) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { onCancelWaitingPlayer(tournament.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .testTag("cancel_matchmaking_btn")
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "ম্যাচমেকিং বাতিল ও ফি রিফান্ড (₹${tournament.entryFee})" else "Cancel Matchmaking & Refund Fee (₹${tournament.entryFee})",
                                        fontSize = 11.sp,
                                        color = GamingRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Battle Royale Daily Schedule Card
                if (tournament.mode == GameMode.BATTLE_ROYALE) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("battle_royale_schedule_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "দৈনিক নির্ধারিত ম্যাচ" else "Daily Scheduled Match",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = tournament.scheduledDailyTime ?: "08:00 PM IST",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GamingGold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GamingGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "40 Slots Limit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Unsuccessful Match Notice Card
                if (tournament.status == TournamentStatus.UNSUCCESSFUL) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("unsuccessful_match_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingRed.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Unsuccessful", tint = GamingRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "ম্যাচ সফল হয়নি (রিফান্ড যোগ্য)" else "Match Unsuccessful (Eligible for Refund)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingRed
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (tournament.isRefundProcessed)
                                    if (language == AppLanguage.BENGALI) "সকল এন্ট্রি ফি অংশগ্রহণকারীদের ওয়ালেটে ফেরত দেওয়া হয়েছে।" else "All entry fees have been refunded to participants' wallets."
                                    else if (language == AppLanguage.BENGALI) "অ্যাডমিন এই ম্যাচের জন্য ₹${tournament.entryFee} এন্ট্রি ফি ওয়ালেট রিফান্ডের অনুমোদন দিচ্ছেন।" else "Admin has queued ₹${tournament.entryFee} entry fee refund to your wallet.",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // =====================================
                // OFFICIAL BOUND ENTRY PASS (JOINED PLAYERS)
                // =====================================
                val userParticipant = tournament.getParticipant(currentUserId ?: "")
                val userEntryId = tournament.getEntryIdForUser(currentUserId ?: "") ?: ""

                if (isJoined && userParticipant != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tournament_entry_pass_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingGold)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Entry Pass",
                                        tint = GamingGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "অফিসিয়াল টুর্নামেন্ট এন্ট্রি পাস" else "OFFICIAL ENTRY PASS",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GamingGold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GamingGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "BOUND & VERIFIED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GamingGold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bound Entry ID with Copy Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GamingSurfaceVariant)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Unique Bound Entry ID", fontSize = 10.sp, color = TextMuted)
                                    val displayEntryId = if (userEntryId.isNotBlank()) userEntryId else "BA-ENTRY-${tournament.id.takeLast(4)}-${userParticipant.freeFireUid.takeLast(4)}"
                                    Text(
                                        text = displayEntryId,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GamingGreen
                                    )
                                }
                                val displayEntryId = if (userEntryId.isNotBlank()) userEntryId else "BA-ENTRY-${tournament.id.takeLast(4)}-${userParticipant.freeFireUid.takeLast(4)}"
                                Button(
                                    onClick = { copyToClipboard("Entry ID", displayEntryId) },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary.copy(alpha = 0.2f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("copy_entry_id_btn")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GamingPrimary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy ID", fontSize = 10.sp, color = GamingPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Bound Account & Free Fire UID
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Player: ${userParticipant.freeFireName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "FF UID: ${userParticipant.freeFireUid}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "🔒 এই এন্ট্রি আইডিটি শুধুমাত্র আপনার ফ্রি ফায়ার UID ও এই টুর্নামেন্টের জন্য বৈধ। অন্য কোনো খেলোয়াড় এটি ব্যবহার করতে পারবে না।"
                                    else "🔒 Single-Use Bound Entry Pass. Strictly bound to your Free Fire UID for this tournament. Transfer or reuse is forbidden.",
                                fontSize = 10.sp,
                                color = TextMuted,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // =====================================
                // ROOM ID & PASSWORD SECTION (SECURE ACCESS & ANTI-FREE ENTRY)
                // =====================================
                Text(
                    text = AppStrings.roomDetails(language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val roomState = tournament.getRoomAccessState(currentUserId ?: "")

                when (roomState) {
                    com.example.model.RoomAccessState.UNLOCKED -> {
                        // Unlocked only for registered, paid players in confirmed/live matches
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GamingGreen.copy(alpha = 0.12f))
                                .border(1.dp, GamingGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("room_unlocked_container")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LockOpen,
                                            contentDescription = "Room ID",
                                            tint = GamingGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (language == AppLanguage.BENGALI) "রুম প্রবেশপত্র (অনুমোদিত ও পেইড)" else "Room Pass (Paid & Authorized)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GamingGreen
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(GamingGreen)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "UNLOCKED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val (authRoomId, authRoomPass) = onFetchSecureRoomCredentials(tournament.id, currentUserId ?: "")
                                val isAuthorizedByServer = authRoomId != null && authRoomId.isNotBlank()

                                val displayedRoomId = if (isAuthorizedByServer) authRoomId!! else "Locked (Join Match to View)"
                                val displayedRoomPass = if (isAuthorizedByServer) (authRoomPass ?: "None") else "••••"

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Room ID:", fontSize = 11.sp, color = TextSecondary)
                                        Text(
                                            text = displayedRoomId,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary,
                                            modifier = Modifier.testTag("room_id_text")
                                        )
                                    }
                                    if (isAuthorizedByServer) {
                                        Button(
                                            onClick = { copyToClipboard("Room ID", authRoomId ?: "") },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                                            modifier = Modifier.height(36.dp).testTag("copy_room_id_btn")
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy ID", fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Room Password:", fontSize = 11.sp, color = TextSecondary)
                                        Text(
                                            text = displayedRoomPass,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary,
                                            modifier = Modifier.testTag("room_password_text")
                                        )
                                    }
                                    if (isAuthorizedByServer) {
                                        Button(
                                            onClick = { copyToClipboard("Room Password", authRoomPass ?: "") },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                                            modifier = Modifier.height(36.dp).testTag("copy_room_pass_btn")
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy Pass", fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Anti-Free Entry & Security Warning Notice Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GamingGold.copy(alpha = 0.15f))
                                        .border(1.dp, GamingGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Security Alert",
                                            tint = GamingGold,
                                            modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (language == AppLanguage.BENGALI)
                                                "⚠️ অ্যান্টি-ফ্রি এন্ট্রি নীতি: বন্ধুদের সাথে রুম আইডি/পাস শেয়ার করলেও তারা অনুমোদিত হবে না। শুধুমাত্র পেইড ফ্রি ফায়ার UID ছাড়া অন্য কাউকে অ্যাডমিন রুম থেকে কিক করবে এবং কোনো প্রাইজ দেওয়া হবে না।"
                                                else "⚠️ ANTI-FREE ENTRY NOTICE: Sharing Room ID & Password with friends does NOT make them authorized players. Only registered Free Fire UIDs who paid the Entry Fee are allowed. Unauthorized players in the Free Fire room will be kicked before match start and cannot claim prizes.",
                                            fontSize = 10.sp,
                                            color = GamingGold,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Match Metadata Info
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GamingSurfaceVariant)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Match ID: ${tournament.id}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "⏰ ${tournament.startTimeFormatted}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GamingGold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Open Free Fire App Button
                                Button(
                                    onClick = { openFreeFireApp(context) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("open_free_fire_btn")
                                ) {
                                    Icon(Icons.Default.SportsEsports, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Open Free Fire App",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ℹ️ Note: Tapping launches Free Fire on your device. Navigate to Custom Room and enter the Room ID and Password above.",
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                    com.example.model.RoomAccessState.WAITING_CONFIRMATION -> {
                        // Paid & registered, but match is not yet confirmed
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GamingSecondary.copy(alpha = 0.12f))
                                .border(1.dp, GamingSecondary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("room_waiting_confirmation_container")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Hidden",
                                    tint = GamingSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "⏳ ম্যাচ নিশ্চিত হওয়ার অপেক্ষায় রুম লক রয়েছে" else "⏳ Room Locked: Awaiting Match Confirmation",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GamingSecondary
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BENGALI)
                                            "আপনার এন্ট্রি ফি পেইড হয়েছে। স্লট পূর্ণ হয়ে ম্যাচ নিশ্চিত (CONFIRMED) হলে রুম আইডি এবং পাসওয়ার্ড এই পেজে স্বয়ংক্রিয়ভাবে দৃশ্যমান হবে।"
                                            else "Your Entry Fee is verified & paid. Room credentials will automatically unlock here once the match is CONFIRMED and the 45-second countdown starts.",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                    com.example.model.RoomAccessState.EXPIRED -> {
                        // Match completed/cancelled/unsuccessful - expired
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GamingSurfaceVariant)
                                .border(1.dp, GamingBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("room_expired_container")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Expired",
                                    tint = TextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "🔒 রুমের মেয়াদ শেষ হয়েছে" else "🔒 Room Credentials Expired",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BENGALI)
                                            "ম্যাচটি সমাপ্ত বা বাতিল হয়েছে। নিরাপত্তার স্বার্থে রুম আইডি ও পাসওয়ার্ড সরিয়ে নেওয়া হয়েছে।"
                                            else "This match has ended. Room credentials have been automatically expired and hidden for security.",
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                    com.example.model.RoomAccessState.UNAUTHORIZED_OR_KICKED -> {
                        // Player was removed/kicked by Admin
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GamingRed.copy(alpha = 0.15f))
                                .border(1.dp, GamingRed, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("room_revoked_container")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Access Revoked",
                                    tint = GamingRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "🚫 রুম অ্যাক্সেস প্রত্যাহার করা হয়েছে" else "🚫 Access Revoked by Admin",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GamingRed
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BENGALI)
                                            "আপনাকে অননুমোদিত প্লেয়ার হিসেবে চিহ্নিত বা রুম থেকে বহিষ্কার (Kicked) করা হয়েছে।"
                                            else "You have been marked as Unauthorized or Kicked from this room by the Admin.",
                                        fontSize = 11.sp,
                                        color = TextPrimary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                    com.example.model.RoomAccessState.NOT_REGISTERED,
                    com.example.model.RoomAccessState.UNPAID -> {
                        // Locked state for non-registered/unpaid players
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GamingSurfaceVariant)
                                .border(1.dp, GamingBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("room_locked_notice_container")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = GamingGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "🔒 রুম আইডি ও পাসওয়ার্ড সংরক্ষিত" else "🔒 Room Credentials Protected",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BENGALI)
                                            "শুধুমাত্র সফলভাবে রেজিস্টার্ড এবং পেইড (₹${tournament.entryFee}) প্লেয়াররা রুম এক্সেস পাবেন।"
                                            else "Only successfully registered and paid (₹${tournament.entryFee}) players can view Room ID and Password.",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // =====================================
                // PRIZE DISTRIBUTION BREAKDOWN TABLE
                // =====================================
                Text(
                    text = AppStrings.prizeBreakdown(language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GamingSurfaceVariant)
                        .border(1.dp, GamingBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        when (tournament.mode) {
                            GameMode.LONE_WOLF -> {
                                PrizeRow(label = "Winner (বিজয়ী)", prize = "₹40", note = "80% of entry pool", isGold = true)
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 6.dp))
                                PrizeRow(label = "Loser (পরাজিত)", prize = "₹0", note = "No prize")
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 6.dp))
                                PrizeRow(label = "Platform Commission (কমিশন)", prize = "₹10", note = "Host fee", isMuted = true)
                            }
                            GameMode.CLASH_SQUAD -> {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "বিজয়ী দলের জন্য পুরস্কার (মোট ₹১২০):" else "Winning Team Prize (Total ₹120):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                PrizeRow(label = "1st Player (MVP)", prize = "₹35", note = "Top fragger", isGold = true)
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 4.dp))
                                PrizeRow(label = "2nd Player", prize = "₹30", note = "Winning team")
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 4.dp))
                                PrizeRow(label = "3rd Player", prize = "₹30", note = "Winning team")
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 4.dp))
                                PrizeRow(label = "4th Player", prize = "₹25", note = "Winning team")
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 4.dp))
                                PrizeRow(label = "Losing Team", prize = "₹0", note = "0 Prize")
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 4.dp))
                                PrizeRow(label = "Platform Commission", prize = "₹40", note = "Admin host fee", isMuted = true)
                            }
                            GameMode.BATTLE_ROYALE -> {
                                val placementPool = tournament.dynamicPlacementPrizePool
                                val killPool = tournament.brKillPrizePool
                                val totalPool = tournament.dynamicTotalPrizePool
                                val winningPositions = tournament.totalWinningPositions

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "প্লেসমেন্ট প্রাইজ (মোট ₹$placementPool):" else "Placement Prizes (Total ₹$placementPool):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GamingGold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GamingGold.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$winningPositions Winners • Total ₹$totalPool",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GamingGold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))

                                tournament.brPrizeChart.forEachIndexed { index, tier ->
                                    val count = tier.totalWinningPositions
                                    val eachSuffix = if (count > 1) " each (x$count = ₹${tier.totalTierPrize})" else ""
                                    PrizeRow(
                                        label = tier.rankRange,
                                        prize = "₹${tier.prizePerPlayer}$eachSuffix",
                                        isGold = index == 0
                                    )
                                    if (index < tournament.brPrizeChart.size - 1) {
                                        HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 3.dp))
                                    }
                                }

                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "কিল প্রাইজ (মোট ₹$killPool):" else "Kill Prizes (Total ₹$killPool):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PrizeRow(label = "Kill Rank 1 (Most Kills)", prize = "₹30")
                                PrizeRow(label = "Kill Rank 2", prize = "₹25")
                                PrizeRow(label = "Kill Rank 3 - 4", prize = "₹20 each")
                                PrizeRow(label = "Kill Rank 5", prize = "₹15")
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 6.dp))
                                val commission = (tournament.entryFee * tournament.totalSlots) - totalPool
                                PrizeRow(
                                    label = "Host / Platform Commission",
                                    prize = "₹${maxOf(0, commission)}",
                                    note = "Entry ₹${tournament.entryFee} x ${tournament.totalSlots} = ₹${tournament.entryFee * tournament.totalSlots}",
                                    isMuted = true
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // =====================================
                // PARTICIPANTS LIST
                // =====================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "অংশগ্রহণকারী (${tournament.filledSlots}/${tournament.totalSlots})" else "Participants (${tournament.filledSlots}/${tournament.totalSlots})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GamingDarkBackground)
                        .border(1.dp, GamingBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    if (tournament.participants.isEmpty()) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "এখনও কোনো খেলোয়াড় যোগ দেয়নি। প্রথম যোগ দিন!" else "No players joined yet. Be the first to join!",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            tournament.participants.take(10).forEachIndexed { index, p ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "#${index + 1}", fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(24.dp))
                                        Text(text = p.freeFireName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        if (p.team != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "(${p.team})", fontSize = 10.sp, color = GamingSecondary)
                                        }
                                        if (p.userId == currentUserId) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(GamingGreen)
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(text = "YOU", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }
                                        }
                                    }
                                    Text(text = "UID: ${p.freeFireUid.take(6)}***", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                            if (tournament.participants.size > 10) {
                                Text(
                                    text = "+ ${tournament.participants.size - 10} more players...",
                                    fontSize = 11.sp,
                                    color = GamingSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // =====================================
                // RULES
                // =====================================
                Text(
                    text = AppStrings.rules(language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GamingSurfaceVariant)
                        .border(1.dp, GamingBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val rulesList = if (language == AppLanguage.BENGALI) tournament.rulesBn else tournament.rulesEn
                        rulesList.forEach { rule ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(text = "• ", fontSize = 12.sp, color = GamingPrimary, fontWeight = FontWeight.Bold)
                                Text(text = rule, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Join Action Button
                if (tournament.status == TournamentStatus.UPCOMING || tournament.status == TournamentStatus.LIVE) {
                    Button(
                        onClick = onJoin,
                        enabled = !isJoined && !tournament.isFull,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dialog_join_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isJoined) GamingGreen else GamingPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = when {
                                isJoined -> if (language == AppLanguage.BENGALI) "যোগদান সম্পন্ন (Room ID আনলকড) ✓" else "Joined (Room Unlocked) ✓"
                                tournament.isFull -> AppStrings.matchFull(language)
                                else -> "${AppStrings.joinTournament(language)} • ₹${tournament.entryFee}"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Fair Play & Anti-Cheat Player Report Button
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onReportCheater(tournament, null) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_cheater_match_btn")
                ) {
                    Icon(Icons.Default.Security, contentDescription = "Fair Play", tint = GamingRed, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) "🛡️ সন্দেহজনক খেলোয়াড় বা চিটিং রিপোর্ট করুন" else "🛡️ Report Suspected Cheating / Fair Play",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingRed
                    )
                }
            }
        }
    }
}

@Composable
private fun PrizeRow(
    label: String,
    prize: String,
    note: String? = null,
    isGold: Boolean = false,
    isMuted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isGold) FontWeight.Bold else FontWeight.Medium,
                color = if (isGold) GamingGold else if (isMuted) TextMuted else TextPrimary
            )
            if (note != null) {
                Text(text = note, fontSize = 10.sp, color = TextMuted)
            }
        }
        Text(
            text = prize,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = if (isGold) GamingGold else if (isMuted) TextMuted else GamingGreen
        )
    }
}
