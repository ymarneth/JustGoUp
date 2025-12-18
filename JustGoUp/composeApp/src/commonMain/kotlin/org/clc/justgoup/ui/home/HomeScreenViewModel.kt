package org.clc.justgoup.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.RecentClimbingSession

class HomeScreenViewModel(
    repository: ClimbingSessionRepository
) : ViewModel() {

    val recentSessions: StateFlow<List<RecentClimbingSession>> =
        repository.findRecentSessions()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}

val HomeScreenModelFactory = viewModelFactory {
    initializer {
        HomeScreenViewModel(repository = ClimbingSessionRepository)
    }
}
