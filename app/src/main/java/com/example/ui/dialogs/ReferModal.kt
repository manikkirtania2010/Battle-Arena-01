package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
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
fun ReferModal(
    referralCode: String,
    referredBy: String?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onApplyCode: (code: String) -> Unit
) {
    val context = LocalContext.current
    var friendCodeInput by remember { mutableStateOf("") }

    fun copyCode() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Battle Arena Referral Code", referralCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Referral code $referralCode copied!", Toast.LENGTH_SHORT).show()
    }

    fun shareInvite() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "🔥 Join me on Battle Arena - Free Fire India Tournament App! Use my referral code $referralCode and we BOTH get ₹5 Bonus Cash immediately to play Lone Wolf & Clash Squad matches!"
            )
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Invite via"))
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("refer_modal"),
            color = GamingSurface
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Refer", tint = GamingGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "রেফার ও আয় করুন" else "Refer & Earn ₹10",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Incentive Card (₹5 + ₹5 = ₹10)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GamingSurfaceVariant)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BENGALI) 
                                "প্রতিটি সফল আমন্ত্রণে দুজনেই ₹৫ করে বোনাস পাবেন!" 
                                else "Both you and your friend receive ₹5 each per successful referral!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Referrer gets ₹5 • Friend gets ₹5 (Total ₹10 Bonus Pool)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Your Referral Code
                Text(
                    text = if (language == AppLanguage.BENGALI) "আপনার ইউনিক রেফারেল কোড:" else "Your Unique Referral Code:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingDarkBackground)
                        .border(1.dp, GamingPrimary, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = referralCode,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GamingPrimary,
                        letterSpacing = 1.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(onClick = { copyCode() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { shareInvite() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = GamingGold, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = GamingBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Claim Friend's Referral Code Section
                Text(
                    text = if (language == AppLanguage.BENGALI) "বন্ধুর রেফারেল কোড সাবমিট করুন (পাবেন ₹৫)" else "Claim Friend's Code (+₹5 Bonus)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (referredBy != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingGreen.copy(alpha = 0.12f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "✓ Referral code applied: $referredBy (₹5 Bonus Claimed)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingGreen
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = friendCodeInput,
                            onValueChange = { friendCodeInput = it.uppercase() },
                            placeholder = { Text("e.g. ARENA77", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingGold,
                                unfocusedBorderColor = GamingBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Button(
                            onClick = { onApplyCode(friendCodeInput) },
                            enabled = friendCodeInput.isNotBlank(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("apply_referral_btn")
                        ) {
                            Text(text = "Claim ₹5", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
