package org.clc.justgoup.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.GymStats
import org.clc.justgoup.climbingSession.computeGymStats

class StatsViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository
) : ViewModel() {

    private val _gymStats = MutableStateFlow<List<GymStats>>(emptyList())
    val gymStats: StateFlow<List<GymStats>> = _gymStats

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            val sessions = climbingSessionRepository.exportAllSessions()
            _gymStats.value = computeGymStats(sessions)
            _isLoading.value = false
        }
    }
}
