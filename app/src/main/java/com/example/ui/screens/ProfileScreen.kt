package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.ui.components.BattleArenaBrandLogo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.User
import com.example.ui.components.TelegramSupportCard
import com.example.ui.dialogs.HelpSupportDialog
import com.example.ui.theme.EsportsNeonBlue
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
fun ProfileScreen(
    user: User?,
    language: AppLanguage,
    onLanguageToggle: () -> Unit,
    onReferClick: () -> Unit,
    onAdminToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onUpdateProfile: (name: String, ffName: String, ffUid: String, phone: String, avatarId: Int) -> Unit,
    onSubmitLevelVerification: (level: Int, note: String) -> Unit = { _, _ -> },
    tournamentRemindersEnabled: Boolean = true,
    matchStartsEnabled: Boolean = true,
    prizeAlertsEnabled: Boolean = true,
    fcmToken: String = "",
    onUpdateNotificationPreferences: (reminders: Boolean, matchStarts: Boolean, prizeAlerts: Boolean) -> Unit = { _, _, _ -> },
    onSendTestPushNotification: (type: String) -> Unit = {},
    onAppealClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var showLevelDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(14.dp, 14.dp, 14.dp, 100.dp)
    ) {
        if (user?.isBanned == true || user?.isSuspensionActive == true) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingRed.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = "Banned", tint = GamingRed, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (user.effectiveAccountStatus == com.example.model.AccountStatus.PERMANENTLY_BANNED) "ACCOUNT PERMANENTLY BANNED" else "ACCOUNT TEMPORARILY SUSPENDED",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GamingRed
                                )
                                Text(
                                    text = user.banReason ?: "Fair Play rule violation. Tournament joining is restricted.",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = onAppealClick,
                                colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("profile_appeal_penalty_btn")
                            ) {
                                Text("Appeal Penalty / Fair Review", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        // Player Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GamingSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(GamingPrimary.copy(alpha = 0.2f))
                                    .border(2.dp, GamingPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar",
                                    tint = GamingPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user?.name ?: "Manik Hunter",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = GamingGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "UID: ${user?.id ?: "BA-70912"}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GamingSecondary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "FREE FIRE VERIFIED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GamingSecondary
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GamingSurfaceVariant)
                                .testTag("edit_profile_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // In-Game Credentials Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GamingSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Free Fire In-Game Name (IGN):", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = user?.freeFireName ?: "⚡MANIK_BOSS⚡", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GamingGold)
                                }
                                IconButton(onClick = { copyToClipboard("IGN", user?.freeFireName ?: "") }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                }
                            }
                            HorizontalDivider(color = GamingBorder)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Free Fire Player UID:", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = user?.freeFireUid ?: "2948109382", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }
                                IconButton(onClick = { copyToClipboard("Free Fire UID", user?.freeFireUid ?: "") }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                }
                            }
                            HorizontalDivider(color = GamingBorder)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Free Fire Account Level (Rule: Level 10+):", fontSize = 11.sp, color = TextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Level ${user?.freeFireLevel ?: 12}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GamingGreen)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val isEligible = (user?.freeFireLevel ?: 12) >= 10
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isEligible) GamingGreen.copy(alpha = 0.2f) else GamingRed.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (isEligible) "ELIGIBLE (Lvl 10+)" else "RESTRICTED (< Lvl 10)",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isEligible) GamingGreen else GamingRed
                                            )
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GamingPrimary.copy(alpha = 0.2f))
                                        .clickable { showLevelDialog = true }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(text = "Verify Level", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GamingPrimary)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Lifetime Stats
        item {
            Text(
                text = if (language == AppLanguage.BENGALI) "খেলোয়াড়ের পরিসংখ্যান" else "Player Lifetime Stats",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(label = if (language == AppLanguage.BENGALI) "মোট আয়" else "Total Won", value = "₹${user?.totalEarnings ?: 540}", color = GamingGold, modifier = Modifier.weight(1f))
                StatCard(label = if (language == AppLanguage.BENGALI) "জয়" else "Wins", value = "${user?.totalWins ?: 12}", color = GamingGreen, modifier = Modifier.weight(1f))
                StatCard(label = if (language == AppLanguage.BENGALI) "মোট কিল" else "Kills", value = "${user?.totalKills ?: 48}", color = GamingRed, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Refer & Earn Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReferClick() }
                    .testTag("profile_refer_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GamingSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GamingGold.copy(alpha = 0.6f))
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
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = "Refer", tint = GamingGold, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "রেফার ও আয় করুন (₹৫ + ₹৫)" else "Refer & Earn (₹5 + ₹5)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Code: ${user?.referralCode ?: "ARENA99"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = GamingGold
                            )
                        }
                    }

                    Button(
                        onClick = onReferClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = if (language == AppLanguage.BENGALI) "ইনভাইট" else "Invite", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Settings & Options List
        item {
            Text(
                text = if (language == AppLanguage.BENGALI) "অ্যাপ সেটিংস" else "App Settings",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GamingSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
            ) {
                Column {
                    // Language Switcher Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageToggle() }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = "Language", tint = GamingSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.selectLanguageTitle(language),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${language.nativeName} (${language.englishName})",
                                    fontSize = 11.sp,
                                    color = EsportsNeonBlue
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingSurfaceVariant)
                                .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Change",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingSecondary
                            )
                        }
                    }

                    HorizontalDivider(color = GamingBorder)

                    // Admin Mode Access Row (Secured: Only verified App Owner / Server Admin can access)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = GamingPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "অ্যাডমিন প্যানেল মোড" else "Admin Dashboard Access",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (user?.isOwner == true) {
                                        if (language == AppLanguage.BENGALI) "অনুমোদিত মালিক অ্যাকাউন্ট" else "Authorized App Owner"
                                    } else {
                                        if (language == AppLanguage.BENGALI) "শুধুমাত্র অনুমোদিত অ্যাডমিনদের জন্য" else "Restricted to Authorized Admins"
                                    },
                                    fontSize = 11.sp,
                                    color = if (user?.isOwner == true) GamingGreen else TextSecondary
                                )
                            }
                        }
                        if (user?.isOwner == true) {
                            Switch(
                                checked = user?.isAdmin == true,
                                onCheckedChange = { onAdminToggle() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GamingPrimary,
                                    uncheckedTrackColor = GamingSurfaceVariant
                                ),
                                modifier = Modifier.testTag("admin_mode_switch")
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GamingSurfaceVariant)
                                    .border(1.dp, GamingBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("admin_restricted_badge")
                            ) {
                                Text(
                                    text = "LOCKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = GamingBorder)

                    // Login / Account Switch Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLoginClick() }
                            .padding(14.dp)
                            .testTag("login_account_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = "Account", tint = GamingGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "খেলোয়াড় একাউন্ট (লগইন / পরিবর্তন)" else "Player Account (Login / Switch)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = user?.email ?: "Sign in with Google / Email",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingGold.copy(alpha = 0.15f))
                                .border(1.dp, GamingGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "লগইন" else "Login",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingGold
                            )
                        }
                    }

                    HorizontalDivider(color = GamingBorder)

                    // Help & Customer Support Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showHelpDialog = true }
                            .padding(14.dp)
                            .testTag("help_support_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Help, contentDescription = "Help", tint = GamingSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.helpAndSupport(language),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Telegram Group & Official Updates",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Open", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }

                    HorizontalDivider(color = GamingBorder)

                    // Security & Fairplay Notice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = "Security", tint = GamingGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "নিরাপদ লেনদেন ও ফেয়ার প্লে" else "Security & Fair Play Certified",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Strict Anti-cheat • 256-bit Encrypted Wallet",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Firebase Cloud Messaging (FCM) Push Notifications Section
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = AppStrings.pushNotificationsTitle(language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fcm_notifications_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GamingSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
            ) {
                Column {
                    // FCM Status Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GamingSurfaceVariant.copy(alpha = 0.5f))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GamingPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "FCM",
                                    tint = GamingPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Firebase Cloud Messaging",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (fcmToken.isNotBlank()) "Token: ${fcmToken.take(12)}... (Active)" else "Push Service Active",
                                    fontSize = 10.sp,
                                    color = GamingGreen
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GamingGreen.copy(alpha = 0.15f))
                                .border(1.dp, GamingGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "CONNECTED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = GamingGreen
                            )
                        }
                    }

                    HorizontalDivider(color = GamingBorder)

                    // 1. Tournament Registration Reminders Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = AppStrings.tournamentReminders(language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI) "স্লট বন্ধ ও ম্যাচ শুরুর আগে সতর্কতা" else "Reminders before match registration closes",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = tournamentRemindersEnabled,
                            onCheckedChange = {
                                onUpdateNotificationPreferences(it, matchStartsEnabled, prizeAlertsEnabled)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GamingPrimary,
                                uncheckedTrackColor = GamingSurfaceVariant
                            ),
                            modifier = Modifier.testTag("switch_tournament_reminders")
                        )
                    }

                    HorizontalDivider(color = GamingBorder)

                    // 2. Match Start Times & Rooms Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = AppStrings.matchStartsAlert(language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI) "রুম আইডি আনলক ও কাউন্টডাউন অ্যালার্ট" else "Instant alert when Room ID unlocks & countdown begins",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = matchStartsEnabled,
                            onCheckedChange = {
                                onUpdateNotificationPreferences(tournamentRemindersEnabled, it, prizeAlertsEnabled)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GamingSecondary,
                                uncheckedTrackColor = GamingSurfaceVariant
                            ),
                            modifier = Modifier.testTag("switch_match_starts")
                        )
                    }

                    HorizontalDivider(color = GamingBorder)

                    // 3. Prize Distribution Alerts Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = AppStrings.prizeDistributionAlerts(language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ওয়ালেটে প্রাইজ মানি ক্রেডিট হওয়ার নোটিফিকেশন" else "Instant alert when match prize money is credited",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = prizeAlertsEnabled,
                            onCheckedChange = {
                                onUpdateNotificationPreferences(tournamentRemindersEnabled, matchStartsEnabled, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GamingGold,
                                uncheckedTrackColor = GamingSurfaceVariant
                            ),
                            modifier = Modifier.testTag("switch_prize_alerts")
                        )
                    }

                    HorizontalDivider(color = GamingBorder)

                    // Quick Test Buttons
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "টেস্ট পুশ নোটিফিকেশন পাঠান:" else "Send Test FCM Push Notification:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onSendTestPushNotification("REMINDER") },
                                modifier = Modifier.weight(1f).testTag("test_notif_reminder"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GamingPrimary.copy(alpha = 0.6f))
                            ) {
                                Text("⏰ Reminder", fontSize = 10.sp, color = GamingPrimary)
                            }

                            OutlinedButton(
                                onClick = { onSendTestPushNotification("MATCH_START") },
                                modifier = Modifier.weight(1f).testTag("test_notif_match_start"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GamingSecondary.copy(alpha = 0.6f))
                            ) {
                                Text("🎮 Match", fontSize = 10.sp, color = GamingSecondary)
                            }

                            OutlinedButton(
                                onClick = { onSendTestPushNotification("PRIZE") },
                                modifier = Modifier.weight(1f).testTag("test_notif_prize"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GamingGold.copy(alpha = 0.6f))
                            ) {
                                Text("🏆 Prize", fontSize = 10.sp, color = GamingGold)
                            }
                        }
                    }
                }
            }
        }

        // Official Telegram Support & Updates Section on Profile Page
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (language == AppLanguage.BENGALI) "অফিশিয়াল সাপোর্ট ও টেলিগ্রাম আপডেট" else "Official Support & Telegram Updates",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            TelegramSupportCard(language = language)

            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BattleArenaBrandLogo(
                    size = 38.dp,
                    showText = true,
                    subtitle = if (language == AppLanguage.BENGALI) "ব্যাটেল এরিনা • ফ্রি ফায়ার টুর্নামেন্ট" else "COMMUNITY TOURNAMENTS",
                    isLightBackground = false
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        var nameInput by remember { mutableStateOf(user?.name ?: "") }
        var ffNameInput by remember { mutableStateOf(user?.freeFireName ?: "") }
        var ffUidInput by remember { mutableStateOf(user?.freeFireUid ?: "") }
        var phoneInput by remember { mutableStateOf(user?.phone ?: "") }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "প্রোফাইল তথ্য আপডেট করুন" else "Update Player Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Player Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ffNameInput,
                        onValueChange = { ffNameInput = it },
                        label = { Text("Free Fire IGN (In-Game Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ffUidInput,
                        onValueChange = { ffUidInput = it },
                        label = { Text("Free Fire Player UID") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showEditDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                onUpdateProfile(nameInput, ffNameInput, ffUidInput, phoneInput, user?.avatarId ?: 1)
                                showEditDialog = false
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

    // Verify Level Dialog
    if (showLevelDialog) {
        var levelInput by remember { mutableStateOf((user?.freeFireLevel ?: 12).toString()) }
        var proofNote by remember { mutableStateOf("Profile Screenshot Attached") }

        Dialog(onDismissRequest = { showLevelDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                color = GamingSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Verify Free Fire Account Level",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Players must have a Free Fire India account at Level 10 or above to participate in tournaments.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = levelInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) levelInput = it },
                        label = { Text("Your Free Fire Level") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = proofNote,
                        onValueChange = { proofNote = it },
                        label = { Text("Verification Method / Screenshot Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showLevelDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        val parsed = levelInput.toIntOrNull() ?: 0
                        Button(
                            onClick = {
                                onSubmitLevelVerification(parsed, proofNote)
                                showLevelDialog = false
                            },
                            enabled = parsed > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Submit Level", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Help & Support with Telegram Links & FAQs
    if (showHelpDialog) {
        HelpSupportDialog(
            language = language,
            onDismiss = { showHelpDialog = false }
        )
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}
