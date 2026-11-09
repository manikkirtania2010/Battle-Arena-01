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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.GameMode
import com.example.ui.theme.GamingBorder
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
fun CreateScheduledMatchDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onCreateScheduled: (
        titleEn: String,
        titleBn: String,
        mode: GameMode,
        matchDate: String,
        matchTimeIst: String,
        playerLimit: Int,
        entryFee: Int,
        descriptionEn: String,
        descriptionBn: String,
        map: String
    ) -> Unit,
    onGenerateBatchSchedule: (
        mode: GameMode,
        matchDate: String,
        startTimes: List<String>,
        entryFee: Int
    ) -> Unit
) {
    val context = LocalContext.current
    var isBatchMode by remember { mutableStateOf(false) }

    var selectedMode by remember { mutableStateOf(GameMode.BATTLE_ROYALE) }
    var matchDateInput by remember { mutableStateOf("Today") }
    var matchTimeInput by remember { mutableStateOf("08:00 AM IST") }
    var playerLimitInput by remember { mutableStateOf("40") }
    var entryFeeInput by remember { mutableStateOf("20") }
    var titleEnInput by remember { mutableStateOf("BR Match 1 – 08:00 AM") }
    var titleBnInput by remember { mutableStateOf("বিআর ম্যাচ ১ – ০৮:০০ AM") }
    var descEnInput by remember { mutableStateOf("Scheduled Battle Royale 40-Player tournament. Room credentials published 15 mins prior.") }
    var descBnInput by remember { mutableStateOf("শিডিউলড ব্যাটেল রয়্যাল ৪০ জনের টুর্নামেন্ট।") }
    var mapInput by remember { mutableStateOf("Bermuda") }

    val presetTimes = listOf("08:00 AM IST", "08:30 AM IST", "09:00 AM IST", "09:30 AM IST", "10:00 AM IST", "08:00 PM IST")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("create_scheduled_match_dialog"),
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
                    Column {
                        Text(
                            text = if (isBatchMode) "Batch Schedule Generator" else "Create Scheduled Match",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Indian Standard Time (IST) • Dual-Mode System",
                            fontSize = 11.sp,
                            color = GamingGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle Single vs Batch Tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GamingSurfaceVariant)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isBatchMode) GamingPrimary else Color.Transparent)
                            .clickable { isBatchMode = false }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Single Scheduled Match",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isBatchMode) Color.White else TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isBatchMode) GamingGold else Color.Transparent)
                            .clickable { isBatchMode = true }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Batch", tint = if (isBatchMode) Color.Black else GamingGold, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "5-Match Quick Schedule",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBatchMode) Color.Black else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Game Mode Selector
                Text(text = "Game Mode:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameMode.values().forEach { mode ->
                        val isSelected = selectedMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
                                .clickable {
                                    selectedMode = mode
                                    playerLimitInput = mode.maxPlayers.toString()
                                    entryFeeInput = mode.defaultEntryFee.toString()
                                    when (mode) {
                                        GameMode.BATTLE_ROYALE -> {
                                            titleEnInput = "BR Match 1 – 08:00 AM"
                                            titleBnInput = "বিআর ম্যাচ ১ – ০৮:০০ AM"
                                            mapInput = "Bermuda"
                                        }
                                        GameMode.CLASH_SQUAD -> {
                                            titleEnInput = "CS Match 1 – 08:00 AM"
                                            titleBnInput = "সিএস ম্যাচ ১ – ০৮:০০ AM"
                                            mapInput = "Kalahari"
                                        }
                                        GameMode.LONE_WOLF -> {
                                            titleEnInput = "LW Match 1 – 08:00 AM"
                                            titleBnInput = "এলডব্লিউ ম্যাচ ১ – ০৮:০০ AM"
                                            mapInput = "Iron Cage"
                                        }
                                    }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.tagEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Match Date & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = matchDateInput,
                        onValueChange = { matchDateInput = it },
                        label = { Text("Match Date (IST)", fontSize = 11.sp) },
                        placeholder = { Text("Today / 04 Nov 2026", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("scheduled_date_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = matchTimeInput,
                        onValueChange = { matchTimeInput = it },
                        label = { Text("Starting Time (IST)", fontSize = 11.sp) },
                        placeholder = { Text("08:00 AM IST", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("scheduled_time_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                // Preset Time Quick Chips
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    presetTimes.take(4).forEach { pt ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (matchTimeInput == pt) GamingPrimary.copy(alpha = 0.3f) else GamingSurfaceVariant)
                                .border(0.6.dp, if (matchTimeInput == pt) GamingPrimary else GamingBorder, RoundedCornerShape(6.dp))
                                .clickable { matchTimeInput = pt }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = pt.replace(" IST", ""), fontSize = 9.sp, color = TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isBatchMode) {
                    // Batch Mode Explanation Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingGold.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingGold.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⚡ 5-Match Schedule Sequence:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• ${selectedMode.tagEn} Match 1 – 08:00 AM IST\n" +
                                       "• ${selectedMode.tagEn} Match 2 – 08:30 AM IST\n" +
                                       "• ${selectedMode.tagEn} Match 3 – 09:00 AM IST\n" +
                                       "• ${selectedMode.tagEn} Match 4 – 09:30 AM IST\n" +
                                       "• ${selectedMode.tagEn} Match 5 – 10:00 AM IST\n\n" +
                                       "Each match receives a unique Match ID, independent player list, separate countdown, and separate room credentials.",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val fee = entryFeeInput.toIntOrNull() ?: selectedMode.defaultEntryFee
                            val batchTimes = listOf("08:00 AM IST", "08:30 AM IST", "09:00 AM IST", "09:30 AM IST", "10:00 AM IST")
                            onGenerateBatchSchedule(selectedMode, matchDateInput.trim(), batchTimes, fee)
                            Toast.makeText(context, "Generated 5 scheduled matches for $matchDateInput!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_generate_batch_schedule_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GamingGold)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Generate", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Generate 5 Scheduled Matches",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    // Single Scheduled Match Fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = playerLimitInput,
                            onValueChange = { playerLimitInput = it },
                            label = { Text("Player Limit", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("scheduled_slots_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingPrimary,
                                unfocusedBorderColor = GamingBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = entryFeeInput,
                            onValueChange = { entryFeeInput = it },
                            label = { Text("Entry Fee (₹)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("scheduled_fee_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingPrimary,
                                unfocusedBorderColor = GamingBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = titleEnInput,
                        onValueChange = { titleEnInput = it },
                        label = { Text("Match Title (English)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("scheduled_title_en_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = titleBnInput,
                        onValueChange = { titleBnInput = it },
                        label = { Text("Match Title (Bengali)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descEnInput,
                        onValueChange = { descEnInput = it },
                        label = { Text("Match Description", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val fee = entryFeeInput.toIntOrNull() ?: selectedMode.defaultEntryFee
                            val slots = playerLimitInput.toIntOrNull() ?: selectedMode.maxPlayers
                            onCreateScheduled(
                                titleEnInput.trim(),
                                titleBnInput.trim(),
                                selectedMode,
                                matchDateInput.trim(),
                                matchTimeInput.trim(),
                                slots,
                                fee,
                                descEnInput.trim(),
                                descBnInput.trim(),
                                mapInput.trim()
                            )
                            Toast.makeText(context, "Scheduled match created successfully!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_submit_scheduled_match_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Create Scheduled Match",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
