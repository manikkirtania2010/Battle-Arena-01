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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.model.AppStrings
import com.example.model.Wallet
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
fun WithdrawDialog(
    wallet: Wallet,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onWithdraw: (amount: Int, method: String, destination: String) -> Unit
) {
    var amountInput by remember { mutableStateOf("50") }
    var upiIdInput by remember { mutableStateOf("gamer@paytm") }
    val selectedMethod = "UPI Transfer"

    val parsedAmount = amountInput.toIntOrNull() ?: 0
    val isMinAmountMet = parsedAmount >= 50
    val hasSufficientWinning = wallet.winningBalance >= parsedAmount
    val isUpiValid = upiIdInput.trim().contains("@") && upiIdInput.trim().length >= 5
    val canSubmit = isMinAmountMet && hasSufficientWinning && isUpiValid

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("withdraw_dialog"),
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
                        Text(
                            text = AppStrings.withdraw(language),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GamingGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Manual Payout",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingGold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Virtual Prize Wallet Balance Overview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "উইথড্রযোগ্য উইনিং ব্যালেন্স" else "Withdrawable Winning Balance",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "₹${wallet.winningBalance}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GamingGreen
                                )
                            }
                            Button(
                                onClick = {
                                    if (wallet.winningBalance >= 50) {
                                        amountInput = wallet.winningBalance.toString()
                                    }
                                },
                                enabled = wallet.winningBalance >= 50,
                                colors = ButtonDefaults.buttonColors(containerColor = GamingGold),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "Max",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        if (wallet.reservedBalance > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GamingSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassEmpty,
                                    contentDescription = "Reserved",
                                    tint = GamingGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI)
                                        "পেন্ডিং উইথড্রয়ালে সংরক্ষিত: ₹${wallet.reservedBalance}"
                                    else
                                        "Reserved in Pending Withdrawals: ₹${wallet.reservedBalance}",
                                    fontSize = 10.sp,
                                    color = GamingGold,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Input Field
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { if (it.all { c -> c.isDigit() }) amountInput = it },
                    label = { Text("Withdraw Amount (Min ₹50)") },
                    prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = GamingGold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isMinAmountMet || !hasSufficientWinning,
                    modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingPrimary,
                        unfocusedBorderColor = GamingBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        errorBorderColor = GamingRed
                    )
                )

                if (!isMinAmountMet) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) "⚠️ নূন্যতম উইথড্র অ্যামাউন্ট ₹৫০" else "⚠️ Minimum withdrawal amount is ₹50",
                        fontSize = 11.sp,
                        color = GamingRed
                    )
                } else if (!hasSufficientWinning) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) "⚠️ অপর্যাপ্ত উইনিং ব্যালেন্স (ডিপোজিট ক্যাশ সরাসরি উইথড্র করা যাবে না)" else "⚠️ Insufficient winning balance (Deposit balance cannot be withdrawn directly)",
                        fontSize = 11.sp,
                        color = GamingRed
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quick Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(50, 100, 200, 500).forEach { amt ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (amountInput == amt.toString()) GamingPrimary else GamingSurfaceVariant)
                                .clickable { amountInput = amt.toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "₹$amt",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (amountInput == amt.toString()) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Destination UPI ID Input
                OutlinedTextField(
                    value = upiIdInput,
                    onValueChange = { upiIdInput = it.trim() },
                    label = { Text("Destination UPI ID") },
                    placeholder = { Text("e.g. mobile@upi or name@okaxis") },
                    singleLine = true,
                    isError = upiIdInput.isNotEmpty() && !isUpiValid,
                    modifier = Modifier.fillMaxWidth().testTag("withdraw_upi_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingSecondary,
                        unfocusedBorderColor = GamingBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        errorBorderColor = GamingRed
                    )
                )

                if (upiIdInput.isNotEmpty() && !isUpiValid) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) "⚠️ সঠিক UPI ID দিন (যেমন mobile@upi)" else "⚠️ Enter a valid UPI ID (e.g. mobile@upi)",
                        fontSize = 11.sp,
                        color = GamingRed
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Virtual Wallet & Balance Reservation Notice Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = "Security", tint = GamingGold, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ভার্চুয়াল ব্যালেন্স সংরক্ষণ নীতি" else "Virtual Balance Reservation Policy",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingGold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "উইথড্রয়াল জমা দিলে ₹$parsedAmount উইনিং ব্যালেন্স থেকে সংরক্ষিত রাখা হবে। অ্যাডমিন/মালিক ব্যাংক থেকে সরাসরি আপনার ইউপিআই-তে রিয়েল মানি ট্রান্সফার করবেন এবং ব্যাংক UTR প্রদান করবেন।"
                            else
                                "Submitting will reserve ₹$parsedAmount from your winning balance to prevent double-spending. The Owner will manually transfer real cash to your UPI ID and record the Bank UTR upon payment.",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (canSubmit) {
                            onWithdraw(parsedAmount, selectedMethod, upiIdInput.trim())
                            onDismiss()
                        }
                    },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_withdraw_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GamingGold,
                        disabledContainerColor = GamingSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Withdraw",
                        tint = if (canSubmit) Color.Black else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (!isMinAmountMet) "Minimum ₹50 Required"
                               else if (!hasSufficientWinning) "Insufficient Winning Balance"
                               else "Submit Withdrawal Request • ₹$parsedAmount",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canSubmit) Color.Black else TextMuted
                    )
                }
            }
        }
    }
}
