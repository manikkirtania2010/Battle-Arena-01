package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.GameMode
import com.example.model.Tournament
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
fun VerifyResultDialog(
    tournament: Tournament,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onVerifyLoneWolf: (winnerUserId: String) -> Unit,
    onVerifyClashSquad: (winningTeam: String) -> Unit,
    onVerifyBattleRoyale: () -> Unit
) {
    var selectedWinnerId by remember {
        mutableStateOf(tournament.participants.firstOrNull()?.userId ?: "")
    }
    var selectedWinningTeam by remember { mutableStateOf("Team A") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("verify_result_dialog"),
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
                        Icon(Icons.Default.Verified, contentDescription = "Verify", tint = GamingGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "ম্যাচ ফলাফল যাচাই" else "Verify Match Results",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${tournament.titleEn} (${tournament.mode.tagEn})",
                    fontSize = 13.sp,
                    color = GamingSecondary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Specific Verification Input
                when (tournament.mode) {
                    GameMode.LONE_WOLF -> {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "১v১ বিজয়ী নির্বাচন করুন (পাবে ₹৪০):" else "Select 1v1 Winner (Will receive ₹40):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (tournament.participants.isEmpty()) {
                            Text(text = "No players found in this match.", color = TextMuted)
                        } else {
                            tournament.participants.forEach { p ->
                                val isSelected = selectedWinnerId == p.userId
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedWinnerId = p.userId },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) GamingSurfaceVariant else GamingDarkBackground),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GamingGold else GamingBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedWinnerId = p.userId },
                                                colors = RadioButtonDefaults.colors(selectedColor = GamingGold)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(text = p.freeFireName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Text(text = "UID: ${p.freeFireUid}", fontSize = 11.sp, color = TextSecondary)
                                            }
                                        }
                                        if (isSelected) {
                                            Text(text = "Prize: ₹40", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GamingGold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingSurfaceVariant)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Total Entry: ₹50 • Winner: ₹40 • Admin Commission: ₹10",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    GameMode.CLASH_SQUAD -> {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "বিজয়ী স্কোয়াড নির্বাচন করুন (মোট ₹১২০):" else "Select Winning Squad (Total ₹120 Prize):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        listOf("Team A", "Team B").forEach { team ->
                            val isSelected = selectedWinningTeam == team
                            val teamPlayers = tournament.participants.filter { it.team == team }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedWinningTeam = team },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) GamingSurfaceVariant else GamingDarkBackground),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GamingSecondary else GamingBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedWinningTeam = team },
                                                colors = RadioButtonDefaults.colors(selectedColor = GamingSecondary)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "$team (4 Players)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                        Text(text = "Wins ₹120", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GamingSecondary)
                                    }
                                    if (teamPlayers.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Players: " + teamPlayers.joinToString(", ") { it.freeFireName },
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingSurfaceVariant)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Winning Squad: 1st ₹35, 2nd ₹30, 3rd ₹30, 4th ₹25 (Total ₹120) • Losing Squad ₹0 • Commission ₹40",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    GameMode.BATTLE_ROYALE -> {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "ব্যাটেল রয়্যাল স্বয়ংক্রিয় প্লেসমেন্ট ও কিল হিসাব:" else "Battle Royale Automated Prize Matrix:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingDarkBackground)
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Total Entry (40 × ₹20): ₹800", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "• Placement Prizes: Top 1 (₹100), Top 2 (₹70), Top 3-4 (₹50 ea), Top 5 (₹40), Top 6-10 (₹30 ea) = Total ₹460", fontSize = 11.sp, color = GamingGold)
                                Text(text = "• Kill Prizes: Kill 1st (₹30), 2nd (₹25), 3rd-4th (₹20 ea), 5th (₹15) = Total ₹110", fontSize = 11.sp, color = GamingSecondary)
                                HorizontalDivider(color = GamingBorder, modifier = Modifier.padding(vertical = 4.dp))
                                Text(text = "Total Prize Distributed: ₹570", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GamingGreen)
                                Text(text = "Platform Remaining / Commission: ₹230", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GamingPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Confirmation / Approve Prize Distribution Button
                Button(
                    onClick = {
                        when (tournament.mode) {
                            GameMode.LONE_WOLF -> onVerifyLoneWolf(selectedWinnerId)
                            GameMode.CLASH_SQUAD -> onVerifyClashSquad(selectedWinningTeam)
                            GameMode.BATTLE_ROYALE -> onVerifyBattleRoyale()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_verify_distribute_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingGold)
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = "Distribute", tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) "অনুমোদন ও ওয়ালেটে প্রাইজ যোগ করুন" else "Approve & Credit Wallets",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
