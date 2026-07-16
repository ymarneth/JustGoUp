package org.clc.justgoup.ui.theme.icons

import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// Compose Multiplatform's file-based drawable resources (SVG/PNG/XML) don't reach the Android APK at all on this project's AGP 9 + com.android.kotlin.multiplatform.library setup -- a known upstream bug (JetBrains CMP-9547).
val StatsIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Stats",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        materialPath {
            moveTo(280f, 680f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-280f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(280f)
            close()
            moveToRelative(160f, 0f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-400f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(400f)
            close()
            moveToRelative(160f, 0f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-160f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(160f)
            close()
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            horizontalLineTo(200f)
            close()
            moveToRelative(0f, -80f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-560f)
            horizontalLineTo(200f)
            verticalLineToRelative(560f)
            close()
        }
    }.build()
}
