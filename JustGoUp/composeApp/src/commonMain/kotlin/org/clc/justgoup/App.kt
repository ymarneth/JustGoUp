package org.clc.justgoup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.boulder.VGrade
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.toRecent
import org.clc.justgoup.ui.home.HomeScreen
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // Example fake data for now
    val exampleSessions = listOf(
        ClimbingSession(
            id = "1",
            location = "Boulderbar Linz",
            startTime = LocalDateTime(2025, 12, 5, 18, 30),
            endTime = LocalDateTime(2025, 12, 5, 20, 0),
            notes = "Evening session with friends",
            boulders = listOf(
                Boulder(
                    grade = Grade.VScale(VGrade(3)),
                    attempts = 1,
                    sent = true,
                    flash = true,
                    color = HoldColor.BLUE
                ),
                Boulder(
                    grade = Grade.VScale(VGrade(4)),
                    attempts = 2,
                    sent = true,
                    color = HoldColor.RED
                ),
                Boulder(grade = Grade.VScale(VGrade(5)), attempts = 3, sent = false)
            )
        ),
        ClimbingSession(
            id = "2",
            location = "der Steinbock Linz",
            startTime = LocalDateTime(2025, 12, 3, 9, 0),
            endTime = LocalDateTime(2025, 12, 3, 10, 30),
            notes = "Morning training",
            boulders = listOf(
                Boulder(
                    grade = Grade.VScale(VGrade(2)),
                    attempts = 1,
                    sent = true,
                    color = HoldColor.GREEN
                ),
                Boulder(grade = Grade.VScale(VGrade(3)), attempts = 2, sent = true, flash = true),
                Boulder(grade = Grade.VScale(VGrade(4)), attempts = 3, sent = false)
            )
        )
    )

    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    val recentSessions = exampleSessions.map { it.toRecent() }

    BoulderTheme(themeMode = themeMode) {
        HomeScreen(
            recentClimbingSessions = recentSessions,
            onStartSession = { /* ... */ },
            onOpenSession = { id -> /* ... */ },
            onChangeTheme = { mode -> themeMode = mode },
            currentTheme = themeMode
        )
    }
}
