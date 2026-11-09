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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Link
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
import com.example.model.IncidentReport
import com.example.model.User
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
fun SubmitAppealDialog(
    user: User?,
    report: IncidentReport? = null,
    language: AppLanguage = AppLanguage.ENGLISH,
    onDismiss: () -> Unit,
    onSubmitAppeal: (reportId: String, explanation: String, proofUrl: String?) -> Unit
) {
    val context = LocalContext.current
    var reportIdInput by remember { mutableStateOf(report?.id ?: "") }
    var explanationInput by remember { mutableStateOf("") }
    var proofUrlInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.2.dp, GamingGold.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .testTag("submit_appeal_dialog"),
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
                                .background(GamingGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Gavel, contentDescription = "Appeal", tint = GamingGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "অ্যাকাউন্ট শাস্তি আপিল" else "Fair Enforcement Appeal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Request reconsideration by Battle Arena Admin Team",
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

                // Fair Enforcement Notice
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingDarkBackground)
                        .border(1.dp, GamingBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Every Battle Arena player has the right to appeal penalties. If you believe your account was suspended due to false reports, network lag, or spectator glitch, explain your perspective with gameplay proof. Serious penalties will be reversed upon authorized Admin verification.",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        lineHeight = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Report ID or Penalty Reference
                OutlinedTextField(
                    value = reportIdInput,
                    onValueChange = { reportIdInput = it },
                    label = { Text("Report or Match Reference ID *") },
                    placeholder = { Text("e.g. AC-123456 or T-BR-301") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("appeal_report_id_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingGold,
                        unfocusedBorderColor = GamingBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Explanation & Rebuttal
                OutlinedTextField(
                    value = explanationInput,
                    onValueChange = { explanationInput = it },
                    label = { Text("Detailed Explanation / Clarification *") },
                    placeholder = { Text("Explain why the report was mistaken, network conditions, in-game positioning...") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth().testTag("appeal_explanation_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingPrimary,
                        unfocusedBorderColor = GamingBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Proof / Counter-Evidence Link
                OutlinedTextField(
                    value = proofUrlInput,
                    onValueChange = { proofUrlInput = it },
                    label = { Text("Gameplay Video / Screenshot Proof Link (Optional)") },
                    placeholder = { Text("https://youtube.com/... or https://drive.google.com/...") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = GamingSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("appeal_proof_url_input")
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
                        modifier = Modifier.weight(1f).testTag("cancel_appeal_btn")
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            if (reportIdInput.isBlank()) {
                                Toast.makeText(context, "Report Reference ID is required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (explanationInput.trim().length < 10) {
                                Toast.makeText(context, "Please provide an explanation of at least 10 characters", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            onSubmitAppeal(reportIdInput.trim(), explanationInput.trim(), proofUrlInput.ifBlank { null })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.4f).testTag("confirm_submit_appeal_btn")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Submit Appeal", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}
