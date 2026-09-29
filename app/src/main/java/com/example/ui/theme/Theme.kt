package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CineVaultColorScheme = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = Color.White,
    primaryContainer = ElectricVioletDark,
    onPrimaryContainer = Color.White,
    secondary = SoftCyan,
    onSecondary = Color.Black,
    secondaryContainer = SoftCyanDark,
    onSecondaryContainer = Color.White,
    tertiary = ElectricVioletLight,
    onTertiary = Color.Black,
    background = MidnightBackground,
    onBackground = TextPrimary,
    surface = MidnightSurface,
    onSurface = TextPrimary,
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = MidnightSurfaceElevated,
    outline = MidnightCardBorder,
    outlineVariant = GlassBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun CineVaultTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CineVaultColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward-compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CineVaultTheme(content = content)
}
