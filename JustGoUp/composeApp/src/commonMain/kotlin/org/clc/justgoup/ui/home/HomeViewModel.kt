package org.clc.justgoup.ui.home

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.RecentClimbingSession

class HomeViewModel(
    private val repository: ClimbingSessionRepository,
    scope: CoroutineScope
) {
    private val _recentSessions = MutableStateFlow<List<RecentClimbingSession>>(emptyList())
    val recentSessions: StateFlow<List<RecentClimbingSession>> = _recentSessions

    init {
        scope.launch {
            _recentSessions.value = repository.findRecentSessions()
        }
    }
}
