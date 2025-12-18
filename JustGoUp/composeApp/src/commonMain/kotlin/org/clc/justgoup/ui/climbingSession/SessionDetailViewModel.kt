package org.clc.justgoup.ui.climbingSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository

class SessionDetailViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository,
    sessionId: String
) : ViewModel() {
    private val _session = MutableStateFlow<ClimbingSession?>(null)
    val session: StateFlow<ClimbingSession?> = _session

    init {
        viewModelScope.launch {
            _session.value = climbingSessionRepository.getSessionById(sessionId)
        }
    }
}
