package org.clc.justgoup.ui.climbingSessionDetail.addBoulder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.GradingSystem
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.CreateBoulderCommand
import org.clc.justgoup.preferences.GradingSystemPreference

class AddBoulderViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository,
    private val sessionId: String,
    private val gradingSystemPreference: GradingSystemPreference
) : ViewModel() {

    private val _gradingSystem = MutableStateFlow(gradingSystemPreference.get())
    val gradingSystem: StateFlow<GradingSystem> = _gradingSystem.asStateFlow()

    fun updateGradingSystem(value: GradingSystem) {
        _gradingSystem.value = value
        gradingSystemPreference.set(value)
    }

    fun addBoulderToSession(
        grade: Grade,
        attempts: Int,
        sent: Boolean,
        flash: Boolean,
        repeated: Boolean,
        color: HoldColor?,
        notes: String?,
        onBoulderAdded: (String) -> Unit
    ) {
        val command = CreateBoulderCommand(
            grade = grade,
            attempts = attempts,
            sent = sent,
            flash = flash,
            repeated = repeated,
            color = color,
            notes = notes,
        )

        viewModelScope.launch {
            climbingSessionRepository.addBoulderToSession(sessionId, command)
        }

        onBoulderAdded(sessionId)
    }
}
