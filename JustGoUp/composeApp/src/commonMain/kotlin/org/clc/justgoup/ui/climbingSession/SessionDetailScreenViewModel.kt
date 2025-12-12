package org.clc.justgoup.ui.climbingSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository

class SessionDetailScreenViewModel(
    repository: ClimbingSessionRepository,
    sessionId: String
) : ViewModel() {

    val session: StateFlow<ClimbingSession?> =
        repository.getSessionById(sessionId)
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                null // initial value
            )
}

fun sessionDetailScreenViewModelFactory(sessionId: String) = viewModelFactory {
    initializer {
        SessionDetailScreenViewModel(
            repository = getClimbingSessionRepository(),
            sessionId = sessionId
        )
    }
}

fun getClimbingSessionRepository(): ClimbingSessionRepository = ClimbingSessionRepository()
