package org.clc.justgoup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.climbingSession.RecentClimbingSession
import org.clc.justgoup.ui.home.HomeScreen
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // Example fake data for now
    val exampleSessions = listOf(
        RecentClimbingSession(
            id = "1",
            title = "Evening Session",
            location = "Boulderbar Linz",
            date = LocalDateTime(2025, 12, 5, 18, 30),
            boulders = 12
        ),
        RecentClimbingSession(
            id = "2",
            title = "Morning Training",
            location = "derSteinbock Linz",
            date = LocalDateTime(2025, 12, 3, 9, 0),
            boulders = 8
        )
    )

    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    BoulderTheme(themeMode = themeMode) {
        HomeScreen(
            recentClimbingSessions = exampleSessions,
            onStartSession = { /* ... */ },
            onOpenSession = { id -> /* ... */ },
            onChangeTheme = { mode -> themeMode = mode },
            currentTheme = themeMode
        )
    }
}
