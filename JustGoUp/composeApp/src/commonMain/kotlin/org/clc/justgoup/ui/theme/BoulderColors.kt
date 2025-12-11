package org.clc.justgoup.ui.theme

import androidx.compose.ui.graphics.Color

data class BoulderColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
)

val BoulderLightColors = BoulderColors(
    primary = Color(0xFF33BFA6),      // Teal hold
    secondary = Color(0xFFFD8A43),    // Orange hold
    background = Color(0xFFF9F7F4),   // Soft chalk white
    surface = Color(0xFFFFFFFF),      // Paper white
    textPrimary = Color(0xFF1D1D1D),
    textSecondary = Color(0xFF5F5F5F),
    success = Color(0xFF7CC46E),      // Flash / Send green
    warning = Color(0xFFFFD43B),      // Slippery yellow hold
    error = Color(0xFFE15659),        // Fall red
)

val BoulderDarkColors = BoulderColors(
    primary = Color(0xFF33BFA6),      // Same teal — still pops great on dark
    secondary = Color(0xFFFD8A43),    // Orange stays vivid for accents
    background = Color(0xFF121212),   // Standard dark background
    surface = Color(0xFF1E1E1E),      // Slightly lighter surface, card backgrounds
    textPrimary = Color(0xFFECECEC),  // Soft-white, avoids OLED glare
    textSecondary = Color(0xFFB3B3B3), // Medium gray for secondary
    success = Color(0xFF6BBF5E),      // Adjusted green for dark mode
    warning = Color(0xFFFFC93A),      // Slightly warmer yellow
    error = Color(0xFFE26A6D),        // Softened red for dark surfaces
)
