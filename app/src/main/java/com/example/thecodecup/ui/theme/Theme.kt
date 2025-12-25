package com.example.thecodecup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Primary colors - for main actions and buttons
    primary = PrimaryBlue,              // #324A59 - Blue for buttons
    onPrimary = Color.White,            // White text on blue buttons
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = Color.White,
    
    // Secondary colors - for less prominent actions
    secondary = CardBlue,               // #324A59 - Blue for cards
    onSecondary = Color.White,          // White text on blue cards
    secondaryContainer = CardBlue,
    onSecondaryContainer = Color.White,
    
    // Tertiary colors - for accents
    tertiary = AccentCoffee,            // #C58B5A - Coffee accent
    onTertiary = Color.White,
    tertiaryContainer = AccentCoffee,
    onTertiaryContainer = Color.White,
    
    // Background and Surface
    background = BackgroundLight,       // #F7F8FB - Light gray background
    onBackground = TextPrimary,         // #001833 - Dark text on background
    surface = SurfaceLight,             // #FFFFFF - White surface
    onSurface = TextPrimary,            // #001833 - Dark text on surface
    surfaceVariant = BackgroundLight,   // #F7F8FB - Variant surface
    onSurfaceVariant = TextSecondary,   // #A2A2A2 - Gray text
    
    // Error colors
    error = Color(0xFFD32F2F),
    onError = Color.White,
    
    // Outline
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF0F0F0)
)

@Composable
fun TheCodeCupTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // App chỉ hỗ trợ light theme
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}