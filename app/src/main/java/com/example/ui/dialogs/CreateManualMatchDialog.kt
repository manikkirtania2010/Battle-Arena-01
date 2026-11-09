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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun CreateManualMatchDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onCreateManual: (
        titleEn: String,
        titleBn: String,
        mode: GameMode,
        playerLimit: Int,
        entryFee: Int,
        descriptionEn: String,
        descriptionBn: String,
        isRegistrationOpen: Boolean,
        map: String
    ) -> Unit
) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(GameMode.LONE_WOLF) }
    var titleEnInput by remember { mutableStateOf("Lone Wolf Flash 1v1 Room") }
    var titleBnInput by remember { mutableStateOf("লোন উলফ ফ্ল্যাশ ১v১ রুম") }
    var playerLimitInput by remember { mutableStateOf("2") }
    var entryFeeInput by remember { mutableStateOf("25") }
    var descEnInput by remember { mutableStateOf("Manual host match. Room will be published as soon as ready.") }
    var descBnInput by remember { mutableStateOf("ম্যানুয়াল কাস্টম রুম হোস্ট ম্যাচ।") }
    var isRegistrationOpenInput by remember { mutableStateOf(true) }
    var mapInput by remember { mutableStateOf("Iron Cage") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("create_manual_match_dialog"),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FlashOn, contentDescription = "Manual", tint = GamingGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Create Manual Match",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "On-Demand Host • Independent from Scheduled matches",
                            fontSize = 11.sp,
                            color = GamingSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selection
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
                                        GameMode.LONE_WOLF -> {
                                            titleEnInput = "Lone Wolf Flash 1v1 Room"
                                            titleBnInput = "লোন উলফ ফ্ল্যাশ ১v১ রুম"
                                            mapInput = "Iron Cage"
                                        }
                                        GameMode.CLASH_SQUAD -> {
                                            titleEnInput = "Clash Squad 4v4 Showdown"
                                            titleBnInput = "ক্ল্যাশ স্কোয়াড ৪v৪ শোডাউন"
                                            mapInput = "Kalahari"
                                        }
                                        GameMode.BATTLE_ROYALE -> {
                                            titleEnInput = "Battle Royale 40P Survival"
                                            titleBnInput = "ব্যাটেল রয়্যাল ৪০ জন সারভাইভাল"
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

                // Slots & Fee
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
                        modifier = Modifier.weight(1f).testTag("manual_slots_input"),
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
                        modifier = Modifier.weight(1f).testTag("manual_fee_input"),
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
                    modifier = Modifier.fillMaxWidth().testTag("manual_title_en_input"),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Registration Status Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Registration Status",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isRegistrationOpenInput) "Open immediately for player entry" else "Draft (Registration closed)",
                            fontSize = 10.sp,
                            color = if (isRegistrationOpenInput) GamingGreen else GamingGold
                        )
                    }

                    Switch(
                        checked = isRegistrationOpenInput,
                        onCheckedChange = { isRegistrationOpenInput = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GamingGreen,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = GamingSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val fee = entryFeeInput.toIntOrNull() ?: selectedMode.defaultEntryFee
                        val slots = playerLimitInput.toIntOrNull() ?: selectedMode.maxPlayers
                        onCreateManual(
                            titleEnInput.trim(),
                            titleBnInput.trim(),
                            selectedMode,
                            slots,
                            fee,
                            descEnInput.trim(),
                            descBnInput.trim(),
                            isRegistrationOpenInput,
                            mapInput.trim()
                        )
                        Toast.makeText(context, "Manual match created successfully!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_submit_manual_match_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSecondary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Create Manual Match",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
