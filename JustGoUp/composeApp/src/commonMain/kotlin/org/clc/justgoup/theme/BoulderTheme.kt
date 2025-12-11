package org.clc.justgoup.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*

private val LocalColors = staticCompositionLocalOf { BoulderLightColors }

@Composable
fun BoulderTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    typography: BoulderTypography = BoulderDefaultTypography,
    spacing: BoulderSpacing = BoulderSpacing(),
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colors = if (darkTheme) BoulderDarkColors else BoulderLightColors

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides typography,
        LocalSpacing provides spacing
    ) {
        content()
    }
}

object BoulderTheme {
    val colors: BoulderColors @Composable get() = LocalColors.current
    val typography: BoulderTypography @Composable get() = LocalTypography.current
    val spacing: BoulderSpacing @Composable get() = LocalSpacing.current
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}
