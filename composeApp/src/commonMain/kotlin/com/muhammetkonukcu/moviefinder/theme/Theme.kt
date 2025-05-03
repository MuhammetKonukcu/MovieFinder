package com.muhammetkonukcu.moviefinder.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = White,
    surface = Neutral900,
    tertiary = Neutral500,
    secondary = Neutral300,
    onTertiary = Neutral500,
    background = Neutral900,
    onSecondary = Neutral300,
    onBackground = Neutral700,
    onSurfaceVariant = Neutral800,
    tertiaryContainer = Neutral550
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colors = DarkColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography(),
        content = content,
    )
}