package org.clc.justgoup.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.ui.theme.ThemeMode


@Composable
fun HomeScreen(
    repository: ClimbingSessionRepository,
    onStartSession: () -> Unit,
    onOpenSession: (String) -> Unit,
    onChangeTheme: (ThemeMode) -> Unit,
    currentTheme: ThemeMode
) {

    val recentSessions by repository.getRecentSessions().collectAsState(initial = emptyList())

    HomeScreenContent(
        recentClimbingSessions = recentSessions,
        onStartSession = onStartSession,
        onOpenSession = onOpenSession,
        onChangeTheme = onChangeTheme,
        currentTheme = currentTheme
    )
}
