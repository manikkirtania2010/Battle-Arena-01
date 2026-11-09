package com.example.ui.dialogs

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AccountStatus
import com.example.model.AppLanguage
import com.example.model.User
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.GamingPrimary
import com.example.ui.theme.GamingRed
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.GamingSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AccountSuspensionNoticeDialog(
    user: User,
    language: AppLanguage = AppLanguage.ENGLISH,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val isPermanent = user.effectiveAccountStatus == AccountStatus.PERMANENTLY_BANNED
    val statusColor = if (isPermanent) GamingRed else GamingGold

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, statusColor.copy(alpha = 0.8f), RoundedCornerShape(22.dp))
                .testTag("account_suspension_dialog"),
            color = GamingDarkBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPermanent) Icons.Default.Block else Icons.Default.Gavel,
                        contentDescription = "Penalty Notice",
                        tint = statusColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isPermanent) {
                        if (language == AppLanguage.BENGALI) "অ্যাকাউন্ট স্থায়ীভাবে নিষিদ্ধ" else "Account Permanently Banned"
                    } else {
                        if (language == AppLanguage.BENGALI) "অ্যাকাউন্ট সাময়িকভাবে স্থগিত" else "Account Temporarily Suspended"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = statusColor
                )

                Text(
                    text = if (language == AppLanguage.BENGALI)
                        "ফেয়ার প্লে নীতি লঙ্ঘনের কারণে অ্যাক্সেস অবরুদ্ধ করা হয়েছে"
                    else
                        "Access restricted due to Fair Play policy enforcement",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Player Identity Card (Shows permanent Player ID)
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Column {
                                Text(
                                    text = "PERMANENT PLAYER ID",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGold
                                )
                                Text(
                                    text = user.playerId,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(user.playerId))
                                    Toast.makeText(context, "Player ID copied", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy ID", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = GamingBorder.copy(alpha = 0.5f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("In-Game Name", fontSize = 10.sp, color = TextMuted)
                                Text(user.freeFireName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Free Fire UID", fontSize = 10.sp, color = TextMuted)
                                Text(user.freeFireUid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Penalty Details Card (Reason & Duration)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant.copy(alpha = 0.7f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Penalty Details & Reason",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Reason: ${user.banReason?.ifBlank { "Violation of tournament rules" } ?: "Violation of tournament rules"}",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isPermanent) {
                            Text(
                                text = "Duration: Permanent Ban (No Expiration)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingRed
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = GamingGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Duration: ${user.suspensionDurationHours} Hours",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GamingGold
                                )
                            }
                            Text(
                                text = "Remaining: ${user.suspensionRemainingFormatted}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Safe Cloud Data Guarantee Notice
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingGreen.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = GamingGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Data Safe Guarantee",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your permanent Player ID, wallet balance (₹${user.walletBalance}), and game records are safely preserved in the cloud database and will never be deleted.",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Support & Appeal Note
                Text(
                    text = "If you believe this decision was made in error, you may contact the Fair Play Administrator at kirtaniamanik2010@gmail.com.",
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Close / Dismiss Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("dismiss_suspension_notice_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Text(text = "Close", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}
