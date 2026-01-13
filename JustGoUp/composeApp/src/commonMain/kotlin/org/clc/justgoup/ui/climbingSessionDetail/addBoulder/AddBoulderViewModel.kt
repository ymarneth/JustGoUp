package org.clc.justgoup.ui.climbingSessionDetail.addBoulder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.CreateBoulderCommand

class AddBoulderViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository,
    private val sessionId: String
) : ViewModel() {

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
