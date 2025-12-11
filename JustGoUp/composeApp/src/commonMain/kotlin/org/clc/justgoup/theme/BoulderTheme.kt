package org.clc.justgoup.theme

import androidx.compose.runtime.*

private val LocalColors = staticCompositionLocalOf { BoulderLightColors }

@Composable
fun BoulderTheme(
    colors: BoulderColors = BoulderLightColors,
    typography: BoulderTypography = BoulderDefaultTypography,
    spacing: BoulderSpacing = BoulderSpacing(),
    content: @Composable () -> Unit
) {
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
