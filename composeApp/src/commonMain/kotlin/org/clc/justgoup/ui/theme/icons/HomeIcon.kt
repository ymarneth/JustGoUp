package org.clc.justgoup.ui.theme.icons

import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// material-icons-core doesn't include a Home icon (only material-icons-extended does), and that
// module isn't a dependency here -- see StatsIcon.kt for why file-based drawable resources are a
// dead end on this project's setup, so this is hand-authored the same way.
val HomeIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Home",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        materialPath {
            moveTo(19.0f, 9.3f)
            verticalLineTo(4.0f)
            horizontalLineToRelative(-3.0f)
            verticalLineToRelative(2.6f)
            lineTo(12.0f, 3.0f)
            lineTo(2.0f, 12.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(8.0f)
            horizontalLineToRelative(5.0f)
            verticalLineToRelative(-6.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(6.0f)
            horizontalLineToRelative(5.0f)
            verticalLineToRelative(-8.0f)
            horizontalLineToRelative(3.0f)
            lineTo(19.0f, 9.3f)
            close()
            moveTo(10.0f, 10.0f)
            curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
            reflectiveCurveToRelative(2.0f, 0.9f, 2.0f, 2.0f)
            horizontalLineTo(10.0f)
            close()
        }
    }.build()
}
