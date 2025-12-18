package org.clc.justgoup.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.RecentClimbingSession

class HomeViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository
) : ViewModel() {

    private val _recentSessions = MutableStateFlow<List<RecentClimbingSession>>(emptyList())
    val recentSessions: StateFlow<List<RecentClimbingSession>> = _recentSessions

    init {
        viewModelScope.launch {
            _recentSessions.value = climbingSessionRepository.findRecentSessions()
        }
    }
}
