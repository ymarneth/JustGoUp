package org.clc.justgoup

import androidx.compose.runtime.*
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.climbingSession.RecentClimbingSession
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.clc.justgoup.home.HomeScreen
import org.clc.justgoup.theme.BoulderTheme

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

    BoulderTheme {
        HomeScreen(
            recentClimbingSessions = exampleSessions,
            onStartSession = { /* TODO: navigate to create session */ },
            onOpenSession = { id -> /* TODO: open existing session */ }
        )
    }
}
