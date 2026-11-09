package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.RoomPublicationStatus
import com.example.model.Tournament
import com.example.ui.theme.GamingBorder
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
fun ManageCustomRoomDialog(
    tournament: Tournament,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onPublishRoom: (roomId: String, roomPass: String, notes: String?) -> Unit,
    onUpdateRoom: (roomId: String, roomPass: String, status: RoomPublicationStatus, notes: String?) -> Unit,
    onHideRoom: () -> Unit,
    onCloseRoomAccess: () -> Unit
) {
    val context = LocalContext.current
    var roomIdInput by remember { mutableStateOf(tournament.roomId ?: "") }
    var roomPasswordInput by remember { mutableStateOf(tournament.roomPassword ?: "") }
    var selectedStatus by remember { mutableStateOf(tournament.roomStatus) }
    var notesInput by remember { mutableStateOf(tournament.customRoomNotes ?: "") }

    fun copyToClipboard(label: String, value: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied!", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("manage_custom_room_dialog"),
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
                            text = "Free Fire Custom Room",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Match ID: ${tournament.id} • ${tournament.mode.tagEn}",
                            fontSize = 11.sp,
                            color = GamingSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Realistic Admin Notice: Manual Free Fire Room creation
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Security, contentDescription = "Info", tint = GamingGold, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Free Fire India rooms are manually created by Admin inside the Free Fire app. After creating the custom room in-game, enter the Room ID and Password below to publish.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Room ID field with Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = roomIdInput,
                        onValueChange = { roomIdInput = it },
                        label = { Text("Free Fire Room ID", fontSize = 11.sp) },
                        placeholder = { Text("e.g. 8920194", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_room_id_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    if (roomIdInput.isNotBlank()) {
                        IconButton(
                            onClick = { copyToClipboard("Room ID", roomIdInput) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy ID", tint = GamingGold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Room Password field with Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = roomPasswordInput,
                        onValueChange = { roomPasswordInput = it },
                        label = { Text("Room Password", fontSize = 11.sp) },
                        placeholder = { Text("e.g. 7788", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_room_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingPrimary,
                            unfocusedBorderColor = GamingBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    if (roomPasswordInput.isNotBlank()) {
                        IconButton(
                            onClick = { copyToClipboard("Room Password", roomPasswordInput) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Pass", tint = GamingGold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Room Status Selector
                Text(text = "Room Publication Status:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RoomPublicationStatus.values().forEach { status ->
                        val isSelected = selectedStatus == status
                        val badgeColor = when (status) {
                            RoomPublicationStatus.PUBLISHED -> GamingGreen
                            RoomPublicationStatus.PENDING -> GamingGold
                            RoomPublicationStatus.HIDDEN -> GamingPrimary
                            RoomPublicationStatus.CLOSED -> GamingRed
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) badgeColor.copy(alpha = 0.25f) else GamingSurfaceVariant)
                                .border(1.dp, if (isSelected) badgeColor else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { selectedStatus = status }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = status.displayName,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) badgeColor else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Room Notes
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Room Notes / Instructions for Players", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Join Slot 1-4 for Team A. No grenade spam.", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingPrimary,
                        unfocusedBorderColor = GamingBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                // 1. Primary Action: Publish Room Details
                Button(
                    onClick = {
                        if (roomIdInput.isBlank()) {
                            Toast.makeText(context, "Please enter Free Fire Room ID", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onPublishRoom(roomIdInput.trim(), roomPasswordInput.trim(), notesInput.takeIf { it.isNotBlank() })
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_publish_room_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen)
                ) {
                    Icon(Icons.Default.Publish, contentDescription = "Publish", tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Publish Room Details to Players",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Secondary Row: Update & Hide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onUpdateRoom(roomIdInput.trim(), roomPasswordInput.trim(), selectedStatus, notesInput.takeIf { it.isNotBlank() })
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_update_room_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Update", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onHideRoom()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_hide_room_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant)
                    ) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = "Hide", tint = GamingGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Hide Room", fontSize = 11.sp, color = GamingGold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Close Room Access
                Button(
                    onClick = {
                        onCloseRoomAccess()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_close_room_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingRed.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingRed.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Close", tint = GamingRed, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Close Room Access (Match Over)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GamingRed)
                }
            }
        }
    }
}
