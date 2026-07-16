package org.clc.justgoup.ui.home.addSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.StartClimbingSessionCommand

class AddSessionViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository
) : ViewModel() {

    private val _recentLocations = MutableStateFlow<List<String>>(emptyList())
    val recentLocations: StateFlow<List<String>> = _recentLocations

    init {
        viewModelScope.launch {
            _recentLocations.value = climbingSessionRepository
                .findRecentSessions(offset = 0, limit = RECENT_SESSIONS_SCAN_LIMIT)
                .map { it.location }
                .distinct()
                .take(RECENT_LOCATIONS_LIMIT)
        }
    }

    fun startSession(locationInput: String, onSessionCreated: (String) -> Unit) {
        val command = StartClimbingSessionCommand(
            location = locationInput,
            notes = null
        )

        viewModelScope.launch {
            val session = climbingSessionRepository.startSession(command)
            onSessionCreated(session.id)
        }
    }

    private companion object {
        const val RECENT_SESSIONS_SCAN_LIMIT = 40
        const val RECENT_LOCATIONS_LIMIT = 5
    }
}
