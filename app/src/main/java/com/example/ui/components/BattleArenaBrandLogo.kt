package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EsportsDeepPurple
import com.example.ui.theme.EsportsElectricBlue
import com.example.ui.theme.EsportsMetallicSilver
import com.example.ui.theme.EsportsNeonBlue
import com.example.ui.theme.EsportsNeonPurple
import com.example.ui.theme.EsportsOnyx
import com.example.ui.theme.EsportsSilverDark
import com.example.ui.theme.EsportsSilverLight
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Reusable Premium Esports Tournament Logo & Emblem for Battle Arena.
 * Designed with metallic silver bevels, neon cyan-blue and ultraviolet purple rim lighting,
 * and high-contrast rendering for both dark and light surfaces.
 */
@Composable
fun BattleArenaBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showText: Boolean = false,
    subtitle: String? = "COMMUNITY TOURNAMENTS",
    isLightBackground: Boolean = false,
    enablePulse: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_glow_pulse")
    val pulseGlowAlpha by if (enablePulse) {
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
    } else {
        rememberInfiniteTransition(label = "static_alpha").animateFloat(
            initialValue = 0.6f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static"
        )
    }

    val badgeBackgroundBrush = if (isLightBackground) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E2235),
                Color(0xFF0F1220)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF171B2B),
                EsportsOnyx
            )
        )
    }

    val metallicBorderBrush = Brush.linearGradient(
        colors = listOf(
            EsportsSilverLight,
            EsportsNeonBlue.copy(alpha = pulseGlowAlpha),
            EsportsNeonPurple.copy(alpha = pulseGlowAlpha),
            EsportsSilverDark
        )
    )

    Row(
        modifier = modifier.testTag("battle_arena_brand_logo"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shield Emblem Container with 3D Depth & Metallic Bevel
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (isLightBackground) 8.dp else 12.dp,
                    shape = RoundedCornerShape(size * 0.24f),
                    spotColor = EsportsNeonBlue.copy(alpha = 0.5f),
                    ambientColor = EsportsNeonPurple.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(size * 0.24f))
                .background(badgeBackgroundBrush)
                .border(
                    width = if (size > 60.dp) 2.dp else 1.5.dp,
                    brush = metallicBorderBrush,
                    shape = RoundedCornerShape(size * 0.24f)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Subtle ambient neon back-glow inside shield
            Box(
                modifier = Modifier
                    .size(size * 0.75f)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                EsportsNeonBlue.copy(alpha = 0.25f * pulseGlowAlpha),
                                EsportsDeepPurple.copy(alpha = 0.15f * pulseGlowAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )

            // High-Resolution Esports Shield Emblem
            Image(
                painter = painterResource(id = R.drawable.img_battle_arena_icon_1793539212122),
                contentDescription = "Battle Arena Esports Shield Emblem",
                modifier = Modifier
                    .size(size * 0.88f)
                    .clip(RoundedCornerShape(size * 0.18f)),
                contentScale = ContentScale.Fit
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                // Bold and stylish BATTLE ARENA Typography
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BATTLE ",
                        fontSize = if (size > 60.dp) 24.sp else 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.sp,
                        color = if (isLightBackground) Color(0xFF0F172A) else EsportsSilverLight
                    )
                    Text(
                        text = "ARENA",
                        fontSize = if (size > 60.dp) 24.sp else 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.sp,
                        color = EsportsNeonBlue
                    )
                }

                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle.uppercase(),
                        fontSize = if (size > 60.dp) 11.sp else 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = if (isLightBackground) Color(0xFF475569) else EsportsNeonPurple.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

/**
 * Large Full Hero Logo Emblem for Splash, Login, and Tournament Stage Headers.
 */
@Composable
fun BattleArenaHeroLogoBadge(
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    isLightBackground: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_logo_animation")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_glow"
    )

    Column(
        modifier = modifier.testTag("battle_arena_hero_logo_badge"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Outer 3D Esports Tournament Crest Frame
        Box(
            modifier = Modifier
                .size(118.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = EsportsNeonBlue.copy(alpha = 0.6f * glowAlpha),
                    ambientColor = EsportsNeonPurple.copy(alpha = 0.5f * glowAlpha)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1B2035),
                            EsportsOnyx,
                            Color(0xFF070911)
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            EsportsMetallicSilver,
                            EsportsNeonBlue,
                            EsportsSilverLight,
                            EsportsNeonPurple,
                            EsportsMetallicSilver
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Neon Radial Back-Glow
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                EsportsNeonBlue.copy(alpha = 0.3f * glowAlpha),
                                EsportsDeepPurple.copy(alpha = 0.2f * glowAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.img_battle_arena_icon_1793539212122),
                contentDescription = "Battle Arena Official Esports Tournament Logo",
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Styled BATTLE ARENA Typography Header with dual-tone metal & neon styling
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BATTLE ",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = if (isLightBackground) Color(0xFF0F172A) else EsportsSilverLight
            )
            Text(
                text = "ARENA",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = EsportsNeonBlue
            )
        }

        // Subtitle badge: Free Fire India Community Tournaments
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isLightBackground) Color(0xFFE2E8F0) else Color(0x337C4DFF)
                )
                .border(
                    1.dp,
                    if (isLightBackground) Color(0xFF94A3B8) else EsportsNeonPurple.copy(alpha = 0.6f),
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = subtitle ?: "FREE FIRE INDIA COMMUNITY TOURNAMENTS",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                color = if (isLightBackground) Color(0xFF334155) else EsportsMetallicSilver,
                textAlign = TextAlign.Center
            )
        }
    }
}
