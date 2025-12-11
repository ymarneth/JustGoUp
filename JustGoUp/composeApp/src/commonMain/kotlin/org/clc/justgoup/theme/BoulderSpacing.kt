package org.clc.justgoup.theme

import androidx.compose.runtime.staticCompositionLocalOf

data class BoulderSpacing(
    val tiny: Int = 4,
    val small: Int = 8,
    val medium: Int = 16,
    val large: Int = 24,
    val extraLarge: Int = 32,
)

val LocalSpacing = staticCompositionLocalOf { BoulderSpacing() }
