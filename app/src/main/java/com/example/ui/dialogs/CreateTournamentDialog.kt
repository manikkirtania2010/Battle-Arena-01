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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.GameMode
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingPrimary
import com.example.ui.theme.GamingSecondary
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.GamingSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CreateTournamentDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onCreate: (titleEn: String, titleBn: String, mode: GameMode, entryFee: Int, startTime: String, map: String) -> Unit
) {
    var selectedMode by remember { mutableStateOf(GameMode.LONE_WOLF) }
    var titleEnInput by remember { mutableStateOf("Lone Wolf Speed Duel #105") }
    var titleBnInput by remember { mutableStateOf("লোন উলফ স্পিড ডুয়েল #১০৫") }
    var entryFeeInput by remember { mutableStateOf(selectedMode.defaultEntryFee.toString()) }
    var startTimeInput by remember { mutableStateOf("09:30 PM") }
    var mapInput by remember { mutableStateOf("Bermuda") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("create_tournament_dialog"),
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
                    Text(
                        text = if (language == AppLanguage.BENGALI) "নতুন টুর্নামেন্ট তৈরি করুন" else "Create New Tournament",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Select Mode Chips
                Text(text = "Game Mode:", fontSize = 12.sp, color = TextSecondary)
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
                                    entryFeeInput = mode.defaultEntryFee.toString()
                                    when (mode) {
                                        GameMode.LONE_WOLF -> {
                                            titleEnInput = "Lone Wolf Speed Duel #105"
                                            titleBnInput = "লোন উলফ স্পিড ডুয়েল #১০৫"
                                            mapInput = "Iron Cage"
                                        }
                                        GameMode.CLASH_SQUAD -> {
                                            titleEnInput = "Clash Squad Elite #205"
                                            titleBnInput = "ক্ল্যাশ স্কোয়াড এলিট #২০৫"
                                            mapInput = "Kalahari"
                                        }
                                        GameMode.BATTLE_ROYALE -> {
                                            titleEnInput = "Battle Royale Super 40 #305"
                                            titleBnInput = "ব্যাটেল রয়্যাল সুপার ৪০ #৩০৫"
                                            mapInput = "Bermuda"
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

                Spacer(modifier = Modifier.height(12.dp))

                // Title EN
                OutlinedTextField(
                    value = titleEnInput,
                    onValueChange = { titleEnInput = it },
                    label = { Text("Tournament Title (English)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingPrimary,
                        unfocusedBorderColor = GamingBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Title BN
                OutlinedTextField(
                    value = titleBnInput,
                    onValueChange = { titleBnInput = it },
                    label = { Text("টুর্নামেন্টের নাম (বাংলা)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingPrimary,
                        unfocusedBorderColor = GamingBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Entry Fee & Start Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = entryFeeInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) entryFeeInput = it },
                        label = { Text("Entry Fee (₹)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingGold,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = startTimeInput,
                        onValueChange = { startTimeInput = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingSecondary,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Map
                OutlinedTextField(
                    value = mapInput,
                    onValueChange = { mapInput = it },
                    label = { Text("Map (e.g. Bermuda, Purgatory, Kalahari)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingSecondary,
                        unfocusedBorderColor = GamingBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Financial Summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingSurfaceVariant)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Config: ${selectedMode.descriptionEn}",
                        fontSize = 11.sp,
                        color = GamingGold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val fee = entryFeeInput.toIntOrNull() ?: selectedMode.defaultEntryFee
                        onCreate(titleEnInput, titleBnInput, selectedMode, fee, startTimeInput, mapInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_create_tournament_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary)
                ) {
                    Text(text = "Publish Tournament", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
