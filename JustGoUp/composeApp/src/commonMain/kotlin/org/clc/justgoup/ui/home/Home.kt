package org.clc.justgoup.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.clc.justgoup.di.provideClimbingSessionRepository

@Composable
fun Home(
    onOpenSession: (String) -> Unit
) {
    val repository = provideClimbingSessionRepository()
    val viewModel = remember { HomeViewModel(repository) }

    val recentSessions by viewModel.recentSessions.collectAsState(initial = emptyList())

    HomeScreenContent(
        recentClimbingSessions = recentSessions,
        onStartSession = { viewModel.startSession { newId -> onOpenSession(newId) } },
        onOpenSession = onOpenSession
    )
}
