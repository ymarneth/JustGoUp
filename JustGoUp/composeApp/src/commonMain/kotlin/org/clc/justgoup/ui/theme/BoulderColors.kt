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
    primary = Color(0xFF8EDBD1),
    secondary = Color(0xFF6B6B6B),

    background = Color(0xFFF2EFEA),
    surface    = Color(0xFFFFFBF6),

    textPrimary = Color(0xFF1D1D1D),
    textSecondary = Color(0xFF6A6A6A),

    success = Color(0xFF4CAF50),
    warning = Color(0xFFF2C94C),
    error = Color(0xFFE65C5C),
)

val BoulderDarkColors = BoulderColors(
    primary = Color(0xFF2F5D62),
    secondary = Color(0xFF9E9E9E),

    background = Color(0xFF151A1E),
    surface = Color(0xFF1D2328),

    textPrimary = Color(0xFFF0F0F0),
    textSecondary = Color(0xFFB0B0B0),

    success = Color(0xFF66BB6A),
    warning = Color(0xFFFFD54F),
    error = Color(0xFFEF6C6C),
)
