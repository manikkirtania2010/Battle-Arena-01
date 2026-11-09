package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.TextSecondary
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * High-definition visual UPI QR Code Card for Battle Arena.
 * Dynamically generates a valid, standard ISO/IEC 18004 QR code matrix using ZXing
 * encoding the exact UPI payment URI:
 * upi://pay?pa=...&pn=...&am=...&cu=INR&tn=...
 * Fully compatible with PhonePe, Google Pay, Paytm, BHIM, and any UPI scanner.
 */
@Composable
fun UpiQrCodeView(
    upiId: String = "manikkirtania1@ibl",
    merchantName: String = "Battle Arena",
    amount: Int,
    note: String = "Add Money Battle Arena",
    modifier: Modifier = Modifier
) {
    // Generate valid standard UPI URI dynamically
    val upiPayload = remember(upiId, merchantName, amount, note) {
        val encodedName = Uri.encode(merchantName)
        val encodedNote = Uri.encode(note)
        "upi://pay?pa=$upiId&pn=$encodedName&am=$amount&cu=INR&tn=$encodedNote"
    }

    // Generate standard QR code bitmap via ZXing
    val qrBitmap = remember(upiPayload) {
        generateQrBitmap(upiPayload, 512)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("upi_qr_code_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GamingDarkBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "QR Code",
                        tint = GamingGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dynamic UPI QR Code",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingGold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(GamingGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Scan & Pay ₹$amount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // White QR Code Container
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .padding(8.dp)
                    .testTag("upi_qr_container"),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "UPI Payment QR Code for ₹$amount",
                        modifier = Modifier
                            .size(184.dp)
                            .testTag("upi_qr_code_image")
                    )
                } else {
                    // Fallback placeholder
                    Text(
                        text = "Generating QR...",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Supported UPI Apps Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Scan with PhonePe • GPay • Paytm • BHIM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * Builds a standard ISO/IEC 18004 QR code Bitmap using ZXing's QRCodeWriter.
 */
private fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M
        )
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap
    } catch (e: Exception) {
        null
    }
}
