package org.clc.justgoup.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import org.clc.justgoup.climbingSession.ClimbingSessionRepository


@Composable
fun HomeScreen(
    repository: ClimbingSessionRepository,
    onStartSession: () -> Unit,
    onOpenSession: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember { HomeViewModel(repository, scope) }

    val recentSessions by viewModel.recentSessions.collectAsState()

    HomeScreenContent(
        recentClimbingSessions = recentSessions,
        onStartSession = onStartSession,
        onOpenSession = onOpenSession
    )
}
