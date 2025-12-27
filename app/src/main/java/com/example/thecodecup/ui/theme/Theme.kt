package com.example.thecodecup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Primary colors - for main actions and buttons
    primary = BluePrimary,                      // #324A59 - Blue for buttons
    onPrimary = Color.White,                    // White text on blue buttons
    primaryContainer = BluePrimary,
    onPrimaryContainer = Color.White,
    
    // Secondary colors - for less prominent actions
    secondary = CardDarkBlue,                   // #324A59 - Blue for cards
    onSecondary = Color.White,                  // White text on blue cards
    secondaryContainer = CardDarkBlue,
    onSecondaryContainer = Color.White,
    
    // Tertiary colors - for accents
    tertiary = CoffeeAccent,                    // #C58B5A - Coffee accent
    onTertiary = Color.White,
    tertiaryContainer = CoffeeAccent,
    onTertiaryContainer = Color.White,
    
    // Background and Surface
    background = BackgroundPrimary,             // #F7F8FB - Light gray background
    onBackground = TextPrimaryDark,             // #001833 - Dark text on background
    surface = BackgroundSecondary,              // #FFFFFF - White surface
    onSurface = TextPrimaryDark,                // #001833 - Dark text on surface
    surfaceVariant = CardLightGray,             // #F7F8FB - Variant surface
    onSurfaceVariant = TextSecondaryGray,       // #A2A2A2 - Gray text
    
    // Error colors
    error = ErrorRed,
    onError = Color.White,
    
    // Outline
    outline = BorderLight,                      // #E0E0E0
    outlineVariant = DividerColor               // #F0F0F0
)

private val DarkColorScheme = darkColorScheme(
    // Primary colors - for main actions and buttons
    primary = ButtonPrimaryDark,                // #4A6273 - Lighter blue for dark mode
    onPrimary = Color.White,
    primaryContainer = ButtonPrimaryDark,
    onPrimaryContainer = Color.White,
    
    // Secondary colors
    secondary = CardDarkBlueDark,               // #4A6273 - Blue cards in dark
    onSecondary = Color.White,
    secondaryContainer = CardDarkBlueDark,
    onSecondaryContainer = Color.White,
    
    // Tertiary colors - accents
    tertiary = CoffeeAccent,                    // Keep coffee accent same
    onTertiary = Color.White,
    tertiaryContainer = CoffeeAccent,
    onTertiaryContainer = Color.White,
    
    // Background and Surface
    background = BackgroundPrimaryDark,         // #121212 - Dark background
    onBackground = TextPrimaryLight,            // #FFFFFF - Light text on dark
    surface = BackgroundSecondaryDark,          // #1E1E1E - Dark surface
    onSurface = TextPrimaryLight,               // #FFFFFF - Light text
    surfaceVariant = CardLightGrayDark,         // #2C2C2C - Elevated surfaces
    onSurfaceVariant = TextSecondaryLight,      // #B0B0B0 - Gray text in dark
    
    // Error colors
    error = ErrorRed,
    onError = Color.White,
    
    // Outline
    outline = BorderDark,                       // #3A3A3A
    outlineVariant = DividerColorDark           // #2A2A2A
)

@Composable
fun TheCodeCupTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}