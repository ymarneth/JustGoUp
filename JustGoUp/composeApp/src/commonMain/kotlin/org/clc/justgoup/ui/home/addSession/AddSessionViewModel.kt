package org.clc.justgoup.ui.home.addSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddSessionViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository
) : ViewModel() {

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    fun startSession(locationInput: String, onSessionCreated: (String) -> Unit) {
        val sessionId = Uuid.random().toString()
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val session = ClimbingSession(
            id = sessionId,
            location = locationInput,
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
