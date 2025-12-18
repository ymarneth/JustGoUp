package org.clc.justgoup.ui.climbingSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.FrenchGrade
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

    @OptIn(ExperimentalUuidApi::class)
    fun addBoulderToSession(sessionId: String) {
        val id = Uuid.random().toString()
        val boulder = Boulder(
            id = id,
            grade = Grade.French(FrenchGrade(6, 'a', FrenchGrade.Modifier.Plus)),
            attempts = 3,
            sent = true,
            flash = false,
            color = HoldColor.RED,
            notes = null
        )

        viewModelScope.launch {
            climbingSessionRepository.addBoulderToSession(sessionId, boulder)
            _session.value = climbingSessionRepository.getSessionById(sessionId)
        }
    }
}
