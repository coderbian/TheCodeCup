package com.example.thecodecup.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================
// LIGHT THEME COLORS
// ============================================

// Primary Brand Colors
val BluePrimary = Color(0xFF324A59)           // Main brand color
val CoffeeAccent = Color(0xFFC58B5A)          // Coffee brown accent

// Background Colors
val BackgroundPrimary = Color(0xFFF7F8FB)     // Main app background
val BackgroundSecondary = Color(0xFFFFFFFF)   // White background
val SurfaceElevated = Color(0xFFFFFFFF)       // Elevated surfaces

// Card Colors
val CardDarkBlue = Color(0xFF324A59)          // Dark blue cards (loyalty, coffee grid container)
val CardLightGray = Color(0xFFF7F8FB)         // Light gray cards (inner coffee cards, order cards)
val CardWhite = Color(0xFFFFFFFF)             // White cards

// Text Colors
val TextPrimaryDark = Color(0xFF001833)       // Primary text on light bg
val TextSecondaryGray = Color(0xFFA2A2A2)     // Secondary/subtitle text
val TextOnDarkSurface = Color(0xFFFFFFFF)     // Text on dark backgrounds

// Border & Divider Colors
val BorderLight = Color(0xFFE0E0E0)           // Light borders
val BorderMedium = Color(0xFFD8D8D8)          // Medium borders
val DividerColor = Color(0xFFF0F0F0)          // Dividers

// Icon Colors
val IconActive = Color(0xFF324A59)            // Active/selected icons
val IconInactive = Color(0xFFD8D8D8)          // Inactive/unselected icons
val IconOnDark = Color(0xFFFFFFFF)            // Icons on dark surfaces

// Button Colors
val ButtonPrimary = Color(0xFF324A59)         // Primary button
val ButtonDisabled = Color(0xFFE0E0E0)        // Disabled button

// Status Colors
val ErrorRed = Color(0xFFD32F2F)
val SuccessGreen = Color(0xFF4CAF50)
val WarningOrange = Color(0xFFFF9800)

// ============================================
// DARK THEME COLORS
// ============================================

// Background Colors (Dark)
val BackgroundPrimaryDark = Color(0xFF121212)      // Main dark background
val BackgroundSecondaryDark = Color(0xFF1E1E1E)    // Secondary dark background
val SurfaceElevatedDark = Color(0xFF2C2C2C)        // Elevated surfaces in dark

// Card Colors (Dark)
val CardDarkBlueDark = Color(0xFF4A6273)           // Dark blue cards in dark mode
val CardLightGrayDark = Color(0xFF2C2C2C)          // Gray cards in dark mode
val CardWhiteDark = Color(0xFF1E1E1E)              // White cards become dark gray

// Text Colors (Dark)
val TextPrimaryLight = Color(0xFFFFFFFF)           // Primary text in dark mode
val TextSecondaryLight = Color(0xFFB0B0B0)         // Secondary text in dark mode
val TextOnLightSurface = Color(0xFF121212)         // Text on light surfaces in dark mode

// Border & Divider Colors (Dark)
val BorderDark = Color(0xFF3A3A3A)                 // Borders in dark mode
val BorderMediumDark = Color(0xFF4A4A4A)           // Medium borders in dark mode
val DividerColorDark = Color(0xFF2A2A2A)           // Dividers in dark mode

// Icon Colors (Dark)
val IconActiveDark = Color(0xFF6B9AB8)             // Active icons in dark mode
val IconInactiveDark = Color(0xFF5A5A5A)           // Inactive icons in dark mode
val IconOnDarkDark = Color(0xFFFFFFFF)             // Icons on dark surfaces

// Button Colors (Dark)
val ButtonPrimaryDark = Color(0xFF4A6273)          // Primary button in dark mode
val ButtonDisabledDark = Color(0xFF3A3A3A)         // Disabled button in dark mode

// ============================================
// LEGACY ALIASES (for backward compatibility)
// ============================================
@Deprecated("Use BluePrimary instead", ReplaceWith("BluePrimary"))
val PrimaryBlue = BluePrimary

@Deprecated("Use CardDarkBlue instead", ReplaceWith("CardDarkBlue"))
val CardBlue = CardDarkBlue

@Deprecated("Use BackgroundPrimary instead", ReplaceWith("BackgroundPrimary"))
val BackgroundLight = BackgroundPrimary

@Deprecated("Use BackgroundSecondary instead", ReplaceWith("BackgroundSecondary"))
val SurfaceLight = BackgroundSecondary

@Deprecated("Use TextPrimaryDark instead", ReplaceWith("TextPrimaryDark"))
val TextPrimary = TextPrimaryDark

@Deprecated("Use TextSecondaryGray instead", ReplaceWith("TextSecondaryGray"))
val TextSecondary = TextSecondaryGray

@Deprecated("Use TextOnDarkSurface instead", ReplaceWith("TextOnDarkSurface"))
val TextOnDark = TextOnDarkSurface

@Deprecated("Use ButtonPrimary instead", ReplaceWith("ButtonPrimary"))
val ButtonBlue = ButtonPrimary

@Deprecated("Use CardDarkBlue instead", ReplaceWith("CardDarkBlue"))
val LightCards = CardDarkBlue

@Deprecated("Use CoffeeAccent instead", ReplaceWith("CoffeeAccent"))
val CoffeeBrown = CoffeeAccent

@Deprecated("Use TextOnDarkSurface instead", ReplaceWith("TextOnDarkSurface"))
val TextWhite = TextOnDarkSurface

@Deprecated("Use TextSecondaryGray instead", ReplaceWith("TextSecondaryGray"))
val TextGray = TextSecondaryGray