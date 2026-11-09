package com.example.ui.dialogs

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.GamingPrimary
import com.example.ui.theme.GamingSecondary
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.GamingSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HelpSupportDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("help_support_dialog"),
            color = GamingSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
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
                            Icon(Icons.Default.HelpCenter, contentDescription = "Help", tint = GamingPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "হেল্প ও কাস্টমার সাপোর্ট" else "Help & Customer Support",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI) "অফিশিয়াল টেলিগ্রাম সাপোর্ট কেন্দ্র" else "Official Telegram Support Hub",
                                fontSize = 11.sp,
                                color = GamingSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Telegram Support & Updates Card
                TelegramSupportCard(language = language)

                Spacer(modifier = Modifier.height(16.dp))

                // Common FAQs
                Text(
                    text = if (language == AppLanguage.BENGALI) "সাধারণ প্রশ্নোত্তর (FAQ)" else "Frequently Asked Questions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                FaqItem(
                    q = if (language == AppLanguage.BENGALI) "টুর্নামেন্টে খেলতে কী যোগ্যতা প্রয়োজন?" else "What is required to join tournaments?",
                    a = if (language == AppLanguage.BENGALI) "ফ্রি ফায়ার অ্যাকাউন্টের লেভেল ১০ বা তার বেশি হতে হবে।" else "Your Free Fire India account must be Level 10 or above."
                )

                Spacer(modifier = Modifier.height(6.dp))

                FaqItem(
                    q = if (language == AppLanguage.BENGALI) "কখন রুম আইডি ও পাসওয়ার্ড পাওয়া যাবে?" else "When will Room ID & Password be shared?",
                    a = if (language == AppLanguage.BENGALI) "ম্যাচ শুরুর ১৫ মিনিট আগে শুধুমাত্র রেজিস্টার্ড প্লেয়ারদের দেখানো হবে।" else "Room credentials unlock 15 minutes before the match strictly for registered players."
                )

                Spacer(modifier = Modifier.height(6.dp))

                FaqItem(
                    q = if (language == AppLanguage.BENGALI) "নূন্যতম উইথড্রয়াল অ্যামাউন্ট কত?" else "What is the minimum withdrawal amount?",
                    a = if (language == AppLanguage.BENGALI) "উইথড্রয়ালের নূন্যতম সীমা ₹৫০। মালিকের অনুমোদনের পর UPI অ্যাকাউন্টে টাকা পাঠানো হয়।" else "Minimum withdrawal is ₹50. Payouts are transferred via UPI after owner verification."
                )
            }
        }
    }
}

@Composable
private fun FaqItem(q: String, a: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "Q: $q", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GamingGold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = a, fontSize = 11.sp, color = TextSecondary, lineHeight = 14.sp)
        }
    }
}
