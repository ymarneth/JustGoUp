package org.clc.justgoup.ui.climbingSessionDetail.addBoulder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddBoulderViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository,
    private val sessionId: String
) : ViewModel() {

    @OptIn(ExperimentalUuidApi::class)
    fun addBoulderToSession(
        grade: Grade,
        attempts: Int,
        sent: Boolean,
        flash: Boolean,
        color: HoldColor?,
        notes: String?,
        onBoulderAdded: (String) -> Unit
    ) {
        val id = Uuid.random().toString()

        val boulder = Boulder(
            id = id,
            grade = grade,
            attempts = attempts,
            sent = sent,
            flash = flash,
            color = color,
            notes = notes
        )

        viewModelScope.launch {
            climbingSessionRepository.addBoulderToSession(sessionId, boulder)
        }

        onBoulderAdded(sessionId)
    }
}
