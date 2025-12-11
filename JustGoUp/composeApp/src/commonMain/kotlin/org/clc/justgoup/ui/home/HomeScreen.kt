package org.clc.justgoup.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.clc.justgoup.ui.theme.ThemeMode


@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel(
        factory = HomeScreenModelFactory
    ),
    onStartSession: () -> Unit,
    onOpenSession: (String) -> Unit,
    onChangeTheme: (ThemeMode) -> Unit,
    currentTheme: ThemeMode
) {
    val recentSessions by viewModel.recentSessions.collectAsState(initial = emptyList())

    HomeScreenContent(
        recentClimbingSessions = recentSessions,
        onStartSession = onStartSession,
        onOpenSession = onOpenSession,
        onChangeTheme = onChangeTheme,
        currentTheme = currentTheme
    )
}
