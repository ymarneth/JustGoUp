package org.clc.justgoup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.ui.home.HomeScreen
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val repository = ClimbingSessionRepository()

    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    BoulderTheme(themeMode = themeMode) {
        HomeScreen(
            repository = repository,
            onStartSession = { /* ... */ },
            onOpenSession = { id -> /* ... */ },
            onChangeTheme = { mode -> themeMode = mode },
            currentTheme = themeMode
        )
    }
}
