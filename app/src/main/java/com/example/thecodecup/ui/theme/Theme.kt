package com.example.thecodecup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = CardBlue,
    tertiary = AccentCoffee,
    background = Color(0xFF0B1220),
    surface = Color(0xFF0F1624),
    onPrimary = TextOnDark,
    onSecondary = TextOnDark,
    onTertiary = TextOnDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = CardBlue,
    tertiary = AccentCoffee,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = TextOnDark,
    onSecondary = TextOnDark,
    onTertiary = TextOnDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun TheCodeCupTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}