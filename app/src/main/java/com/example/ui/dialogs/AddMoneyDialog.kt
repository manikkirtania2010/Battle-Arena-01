package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.components.UpiQrCodeView
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

/**
 * Add Money Dialog featuring:
 * 1. Dynamically generated UPI payment QR code for official UPI ID: manikkirtania1@ibl.
 * 2. Real-time QR regeneration as user enters custom deposit amount.
 * 3. Minimum deposit enforcement of ₹20.
 * 4. Dedicated "Copy UPI ID" button.
 * 5. Direct UPI intent launcher button ("Pay via UPI App").
 * 6. 12-digit UTR / Transaction ID input.
 * 7. Prominent "Verify Payment" button.
 * 8. Strict anti-fraud guarantee: Payment status remains PENDING until verified by Owner;
 *    wallet is only credited after verified payment approval.
 */
@Composable
fun AddMoneyDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmitRequest: (amount: Int, method: String, utr: String, screenshotNote: String) -> Unit = { _, _, _, _ -> },
    onAddMoney: ((amount: Int, method: String) -> Unit)? = null
) {
    var amountInput by remember { mutableStateOf("50") }
    var selectedMethod by remember { mutableStateOf("PhonePe UPI") }
    var utrInput by remember { mutableStateOf("") }
    var screenshotNote by remember { mutableStateOf("Payment Proof Attached") }
    var copiedUpi by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val officialUpiId = "manikkirtania1@ibl"
    val presetAmounts = listOf(20, 50, 100, 200, 500)
    val paymentMethods = listOf("PhonePe UPI", "Google Pay", "Paytm UPI", "BHIM UPI")

    val parsedAmt = amountInput.toIntOrNull() ?: 0
    val isAmountValid = parsedAmt >= 20
    val effectiveAmountForQr = if (isAmountValid) parsedAmt else 20
    val isUtrValid = utrInput.length >= 6
    val isFormReady = isAmountValid && isUtrValid

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                .testTag("add_money_dialog"),
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
                            text = AppStrings.addMoney(language),
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
                                text = "Admin Verified",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Deposit Amount Input Field
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { input ->
                        if (input.all { char -> char.isDigit() }) {
                            amountInput = input
                        }
                    },
                    label = { Text("Deposit Amount (₹)") },
                    placeholder = { Text("Min ₹20") },
                    prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = GamingGold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountInput.isNotEmpty() && !isAmountValid,
                    supportingText = {
                        if (amountInput.isNotEmpty() && !isAmountValid) {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "সর্বনিম্ন ডিপোজিট পরিমাণ ₹২০" else "Minimum deposit amount is ₹20",
                                color = GamingRed,
                                fontSize = 11.sp
                            )
                        } else {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "সর্বনিম্ন ডিপোজিট: ₹২০ • QR কোড স্বয়ংক্রিয়ভাবে আপডেট হয়" else "Min deposit: ₹20 • QR updates dynamically",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_amount_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingPrimary,
                        unfocusedBorderColor = GamingBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        errorBorderColor = GamingRed
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick Preset Amount Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetAmounts.forEach { amt ->
                        val isSelected = amountInput == amt.toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
                                .clickable { amountInput = amt.toString() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+₹$amt",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Dynamic UPI QR Code Component (using manikkirtania1@ibl)
                UpiQrCodeView(
                    upiId = officialUpiId,
                    merchantName = "Battle Arena",
                    amount = effectiveAmountForQr,
                    note = "Add Money Battle Arena"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Direct "Pay via UPI App" Launcher Button
                Button(
                    onClick = {
                        val upiUri = "upi://pay?pa=$officialUpiId&pn=Battle%20Arena&am=$effectiveAmountForQr&cu=INR&tn=Add%20Money%20Battle%20Arena"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUri))
                        try {
                            context.startActivity(Intent.createChooser(intent, "Pay ₹$effectiveAmountForQr via UPI"))
                        } catch (e: Exception) {
                            clipboardManager.setText(AnnotatedString(officialUpiId))
                            copiedUpi = true
                            Toast.makeText(context, "UPI ID copied! Open PhonePe/GPay to pay", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("pay_via_upi_app_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSecondary)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = "Open UPI", tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) "UPI অ্যাপ দিয়ে সরাসরি পেমেন্ট করুন (₹$effectiveAmountForQr)" else "Pay via UPI App (₹$effectiveAmountForQr)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Official UPI Details Card with Prominent "Copy UPI ID" Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("official_upi_card"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Official UPI ID",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = officialUpiId,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = GamingGold
                            )
                        }
                        // Copy UPI ID Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (copiedUpi) GamingGreen.copy(alpha = 0.2f) else GamingPrimary.copy(alpha = 0.2f))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(officialUpiId))
                                    copiedUpi = true
                                    Toast.makeText(context, "UPI ID copied: $officialUpiId", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("copy_upi_id_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (copiedUpi) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                    contentDescription = "Copy UPI ID",
                                    tint = if (copiedUpi) GamingGreen else GamingPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (copiedUpi) "Copied!" else "Copy UPI ID",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (copiedUpi) GamingGreen else GamingPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Selector
                Text(
                    text = if (language == AppLanguage.BENGALI) "যে অ্যাপ দিয়ে পেমেন্ট করেছেন" else "Payment Method Used",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    paymentMethods.forEach { method ->
                        val isSelected = selectedMethod == method
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingSurfaceVariant else GamingDarkBackground)
                                .border(1.dp, if (isSelected) GamingSecondary else GamingBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedMethod = method }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = method.split(" ").first(),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 12-digit UTR / Transaction ID Input
                OutlinedTextField(
                    value = utrInput,
                    onValueChange = { utrInput = it.trim().uppercase() },
                    label = { Text("12-Digit UTR / Transaction ID") },
                    placeholder = { Text("e.g. 492019482910") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utr_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingGold,
                        unfocusedBorderColor = GamingBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Verification & Pending Status Explanation Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = "Pending Status",
                                tint = GamingGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Payment Status: PENDING Verification",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingGold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "পেমেন্ট রিকোয়েস্ট পেন্ডিং থাকবে। অ্যাডমিন/মালিক ব্যাংক স্টেটমেন্টের সাথে UTR মিলিয়ে দেখার পরেই ওয়ালেটে ব্যালেন্স যুক্ত হবে।"
                            else
                                "Payment remains Pending until verified. Funds are only credited after the Owner confirms receipt against the bank statement.",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Anti-Fraud Security Notice
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = "Notice", tint = GamingSecondary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "ডুপ্লিকেট বা ভুয়া UTR সাবমিট করলে অ্যাকাউন্ট স্থায়ীভাবে ব্যান হবে।"
                        else
                            "Never enter fake or duplicate UTRs. Accounts submitting fraudulent IDs are suspended.",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary "Verify Payment" Button
                val buttonText = when {
                    !isAmountValid -> if (language == AppLanguage.BENGALI) "সর্বনিম্ন ডিপোজিট ₹২০" else "Minimum Deposit is ₹20"
                    !isUtrValid -> if (language == AppLanguage.BENGALI) "১২-সংখ্যার UTR লিখুন (কমপক্ষে ৬ ডিজিট)" else "Enter Valid UTR (Min 6 digits)"
                    else -> if (language == AppLanguage.BENGALI) "পেমেন্ট ভেরিফাই করুন • ₹$parsedAmt" else "Verify Payment • ₹$parsedAmt"
                }

                Button(
                    onClick = {
                        if (isFormReady) {
                            // Submits payment request for verification with PENDING status
                            // NEVER credits wallet immediately; wallet is only credited after Owner approves
                            onSubmitRequest(parsedAmt, selectedMethod, utrInput, screenshotNote)
                            Toast.makeText(
                                context,
                                if (language == AppLanguage.BENGALI) "রিকোয়েস্ট জমা হয়েছে! স্ট্যাটাস: পেন্ডিং" else "Request submitted! Status: Pending Owner Verification",
                                Toast.LENGTH_LONG
                            ).show()
                            onDismiss()
                        }
                    },
                    enabled = isFormReady,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("verify_payment_button")
                        .testTag("confirm_add_money_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GamingPrimary,
                        disabledContainerColor = GamingSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verify",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = buttonText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
