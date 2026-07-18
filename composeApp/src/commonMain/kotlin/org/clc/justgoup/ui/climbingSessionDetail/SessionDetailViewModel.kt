package org.clc.justgoup.ui.climbingSessionDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.SessionStats
import org.clc.justgoup.climbingSession.computeSessionStats

class SessionDetailViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository,
    private val sessionId: String
) : ViewModel() {
    private val _session = MutableStateFlow<ClimbingSession?>(null)
    val session: StateFlow<ClimbingSession?> = _session

    private val _sessionStats = MutableStateFlow<SessionStats?>(null)
    val sessionStats: StateFlow<SessionStats?> = _sessionStats

    init {
        viewModelScope.launch {
            refreshSession()
        }
    }

    fun deleteBoulder(id: String) {
        viewModelScope.launch {
            climbingSessionRepository.deleteBoulderFromSession(id)
            refreshSession()
        }
    }

    private suspend fun refreshSession() {
        val current = climbingSessionRepository.getSessionById(sessionId) ?: return
        _session.value = current

        val otherSessionsAtSameGym = climbingSessionRepository.exportAllSessions()
            .filter { it.id != current.id && it.location.trim() == current.location.trim() }
        _sessionStats.value = computeSessionStats(current, otherSessionsAtSameGym)
    }
}
