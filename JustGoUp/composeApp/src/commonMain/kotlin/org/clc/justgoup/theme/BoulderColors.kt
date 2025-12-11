package org.clc.justgoup.theme

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
