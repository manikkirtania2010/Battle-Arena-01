package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.example.ui.components.BattleArenaHeroLogoBadge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.ui.dialogs.AccountSuspensionNoticeDialog
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuthenticationRepository
import com.example.model.AppLanguage
import com.example.model.AppStrings
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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthenticationRepository,
    language: AppLanguage,
    onLanguageToggle: () -> Unit,
    onAuthSuccess: (User) -> Unit,
    onContinueAsGuest: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var ffName by remember { mutableStateOf("") }
    var ffUid by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val restrictedUser by authRepository.lastRestrictedUser.collectAsState()

    if (restrictedUser != null) {
        AccountSuspensionNoticeDialog(
            user = restrictedUser!!,
            language = language,
            onDismiss = { authRepository.clearLastRestrictedUser() }
        )
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        color = GamingDarkBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background ambient esports gradient glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GamingPrimary.copy(alpha = 0.12f),
                                GamingDarkBackground
                            ),
                            radius = 900f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar (Language Switcher & Optional Close)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Language Switcher Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(GamingSurfaceVariant)
                            .border(1.dp, GamingBorder, RoundedCornerShape(10.dp))
                            .clickable { onLanguageToggle() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("login_language_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = GamingGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = language.nativeName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    if (onDismiss != null) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GamingSurfaceVariant)
                                .testTag("login_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Hero Esports Tournament Emblem & Brand Identity
                BattleArenaHeroLogoBadge(
                    subtitle = if (language == AppLanguage.BENGALI) 
                        "ব্যাটেল এরিনা — ফ্রি ফায়ার ইন্ডিয়া টুর্নামেন্ট" 
                        else "FREE FIRE INDIA COMMUNITY TOURNAMENTS"
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Mode Tabs (Sign In / Register)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GamingSurface)
                        .border(1.dp, GamingBorder, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isSignUp) GamingPrimary else Color.Transparent)
                            .clickable {
                                isSignUp = false
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_sign_in"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "লগইন" else "Sign In",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isSignUp) Color.White else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSignUp) GamingPrimary else Color.Transparent)
                            .clickable {
                                isSignUp = true
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_sign_up"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "নতুন একাউন্ট" else "Register",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSignUp) Color.White else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Gaming Login Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {

                        // One-Tap Google Sign-In Button
                        Button(
                            onClick = {
                                isLoading = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val result = authRepository.signInWithGoogle(context)
                                    isLoading = false
                                    result.onSuccess { user ->
                                        Toast.makeText(context, "Welcome, ${user.name}!", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess(user)
                                    }.onFailure { err ->
                                        errorMessage = err.message ?: "Google Sign-In failed"
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_screen_google_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GamingSecondary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "G ",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GamingSecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "Google দিয়ে সরাসরি প্রবেশ করুন" else "Continue with Google",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divider OR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = GamingBorder)
                            Text(
                                text = "  OR / অথবা  ",
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = GamingBorder)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sign Up specific fields: Name, FF IGN, FF UID
                        AnimatedVisibility(visible = isSignUp) {
                            Column {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Player Real Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GamingPrimary) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_player_name"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GamingPrimary,
                                        unfocusedBorderColor = GamingBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = ffName,
                                    onValueChange = { ffName = it },
                                    label = { Text("Free Fire In-Game Name (IGN)") },
                                    leadingIcon = { Icon(Icons.Default.SportsEsports, contentDescription = null, tint = GamingGold) },
                                    placeholder = { Text("e.g. ⚡WARRIOR_OP⚡") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_ff_ign"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GamingGold,
                                        unfocusedBorderColor = GamingBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = ffUid,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) ffUid = it },
                                    label = { Text("Free Fire Player UID") },
                                    leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = GamingSecondary) },
                                    placeholder = { Text("e.g. 2948194820") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_ff_uid"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GamingSecondary,
                                        unfocusedBorderColor = GamingBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GamingSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_email"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingSecondary,
                                unfocusedBorderColor = GamingBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Field with toggle visibility
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password (পাসওয়ার্ড)") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GamingPrimary) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = TextMuted
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_password"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingPrimary,
                                unfocusedBorderColor = GamingBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        // Error Banner
                        errorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GamingRed.copy(alpha = 0.15f))
                                    .border(0.8.dp, GamingRed, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "⚠️ $err",
                                    fontSize = 11.sp,
                                    color = GamingRed,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please enter your email and password"
                                    return@Button
                                }
                                isLoading = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val result = if (isSignUp) {
                                        authRepository.signUpWithEmail(email, password, name, ffName, ffUid)
                                    } else {
                                        authRepository.signInWithEmail(email, password)
                                    }
                                    isLoading = false
                                    result.onSuccess { user ->
                                        Toast.makeText(context, "Welcome, ${user.name}!", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess(user)
                                    }.onFailure { err ->
                                        errorMessage = err.message ?: "Authentication failed"
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = if (isSignUp) {
                                        if (language == AppLanguage.BENGALI) "রেজিস্টার ও ম্যাচ খেলুন" else "Register & Play"
                                    } else {
                                        if (language == AppLanguage.BENGALI) "লগইন করুন" else "Sign In"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Guest / Quick Play Demo Mode Button
                        OutlinedButton(
                            onClick = onContinueAsGuest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("login_guest_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = GamingGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "গেস্ট হিসেবে সরাসরি ম্যাচ দেখুন" else "Explore as Guest Player",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Security & Legal Fair Play Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingSurfaceVariant.copy(alpha = 0.5f))
                        .border(0.6.dp, GamingBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🛡️ 256-Bit Encrypted Authentication • Anti-Fraud Security",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) 
                                "অনুমোদিত এস্পোর্টস স্কিল টুর্নামেন্ট • ১৮+ শর্তাবলী প্রযোজ্য" 
                                else "Skill-based Esports Tournaments • 18+ T&C Apply",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
