package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BattleArenaColorScheme = darkColorScheme(
    primary = GamingPrimary,
    onPrimary = OnGamingPrimary,
    primaryContainer = GamingPrimaryContainer,
    onPrimaryContainer = GamingPrimaryHover,
    secondary = GamingSecondary,
    onSecondary = OnGamingSecondary,
    secondaryContainer = GamingSecondaryContainer,
    tertiary = GamingGold,
    background = GamingDarkBackground,
    surface = GamingSurface,
    surfaceVariant = GamingSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = GamingBorder,
    outlineVariant = GamingBorder,
    error = GamingRed
)

@Composable
fun BattleArenaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BattleArenaColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias if needed
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = BattleArenaTheme(content)
