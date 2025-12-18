package org.clc.justgoup.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.RecentClimbingSession
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    fun startSession(onSessionCreated: (String) -> Unit) {
        val sessionId = Uuid.random().toString()
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val session = ClimbingSession(
            id = sessionId,
            location = "Some location",
            startTime = now,
            endTime = null,
            notes = null
        )

        viewModelScope.launch {
            climbingSessionRepository.startSession(session)
        }

        onSessionCreated(sessionId)
    }
}
