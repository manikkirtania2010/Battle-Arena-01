package com.example.ui.dialogs

import android.widget.Toast
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.CheatCategory
import com.example.model.Tournament
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingPrimary
import com.example.ui.theme.GamingRed
import com.example.ui.theme.GamingSecondary
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.GamingSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ReportCheatingDialog(
    tournament: Tournament,
    language: AppLanguage = AppLanguage.ENGLISH,
    prefilledTargetPlayerId: String? = null,
    onDismiss: () -> Unit,
    onSubmitReport: (matchId: String, targetPlayerId: String, category: CheatCategory, reason: String, evidenceText: String?, evidenceUrl: String?) -> Unit
) {
    val context = LocalContext.current
    var matchIdInput by remember { mutableStateOf(tournament.id) }
    var targetPlayerIdInput by remember { mutableStateOf(prefilledTargetPlayerId ?: "") }
    var selectedCategory by remember { mutableStateOf(CheatCategory.AIMBOT_AUTO_AIM) }
    var reasonInput by remember { mutableStateOf("") }
    var evidenceTextDetail by remember { mutableStateOf("") }
    var evidenceUrlInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.2.dp, GamingRed.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .testTag("report_cheating_dialog"),
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
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Report, contentDescription = "Report", tint = GamingRed, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ফেয়ার প্লে ও চিট রিপোর্টিং" else "Fair Play & Anti-Cheat Report",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Match #${tournament.id} • ${tournament.mode.tagEn}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingSurfaceVariant)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scope & Privacy Notice
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingDarkBackground)
                        .border(1.dp, GamingBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = "Notice", tint = GamingGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BATTLE ARENA enforces tournament integrity. We do not access or tamper with the Free Fire client. Every report is audited by human Admins using match logs & evidence. False or spam reports may incur penalties.",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Match ID (Pre-filled / Editable)
                OutlinedTextField(
                    value = matchIdInput,
                    onValueChange = { matchIdInput = it },
                    label = { Text("Match ID *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("report_match_id_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingPrimary,
                        unfocusedBorderColor = GamingBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Suspect Player ID / FF UID
                OutlinedTextField(
                    value = targetPlayerIdInput,
                    onValueChange = { targetPlayerIdInput = it },
                    label = { Text("Reported Player ID or Free Fire UID *") },
                    placeholder = { Text("e.g. BA-70912 or 987654321") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("report_target_player_id_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingRed,
                        unfocusedBorderColor = GamingBorder
                    )
                )

                // Quick Select from Tournament Roster if available
                if (tournament.participants.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Quick Select Player from Roster:", fontSize = 10.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tournament.participants.take(8).forEach { participant ->
                            val isSelected = targetPlayerIdInput == participant.userId || targetPlayerIdInput == participant.freeFireUid
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) GamingRed.copy(alpha = 0.25f) else GamingSurfaceVariant)
                                    .border(1.dp, if (isSelected) GamingRed else GamingBorder, RoundedCornerShape(6.dp))
                                    .clickable { targetPlayerIdInput = participant.freeFireUid }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = participant.freeFireName,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GamingRed else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Violation Category Selector
                Text(text = "Violation Category *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CheatCategory.values().forEach { category ->
                        val isSelected = selectedCategory == category
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingRed.copy(alpha = 0.15f) else GamingSurfaceVariant)
                                .border(1.dp, if (isSelected) GamingRed else GamingBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (isSelected) GamingRed else TextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GamingRed else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reason / Incident Description
                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text("Describe the Incident *") },
                    placeholder = { Text("e.g. Unnatural snap headshots through gloo wall, impossible movement speed in zone...") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("report_reason_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingPrimary,
                        unfocusedBorderColor = GamingBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Evidence Link (Optional Screenshot/Screen Recording URL)
                OutlinedTextField(
                    value = evidenceUrlInput,
                    onValueChange = { evidenceUrlInput = it },
                    label = { Text("Evidence Link (Optional: YouTube / Imgur / Drive)") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = GamingSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("report_evidence_url_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingSecondary,
                        unfocusedBorderColor = GamingBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Additional Observations / Kill Details
                OutlinedTextField(
                    value = evidenceTextDetail,
                    onValueChange = { evidenceTextDetail = it },
                    label = { Text("Additional Observations (Optional)") },
                    placeholder = { Text("Kill count, spectator observations, exact round number...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("report_evidence_text_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("cancel_report_btn")
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            if (matchIdInput.isBlank()) {
                                Toast.makeText(context, "Match ID is required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (targetPlayerIdInput.isBlank()) {
                                Toast.makeText(context, "Reported Player ID or UID is required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (reasonInput.trim().length < 5) {
                                Toast.makeText(context, "Please provide description of incident", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            onSubmitReport(
                                matchIdInput.trim(),
                                targetPlayerIdInput.trim(),
                                selectedCategory,
                                reasonInput.trim(),
                                evidenceTextDetail.ifBlank { null },
                                evidenceUrlInput.ifBlank { null }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.4f).testTag("submit_report_btn")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Submit Report", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
