package org.clc.justgoup.ui.home.addSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.StartClimbingSessionCommand
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddSessionViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository
) : ViewModel() {

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
}
