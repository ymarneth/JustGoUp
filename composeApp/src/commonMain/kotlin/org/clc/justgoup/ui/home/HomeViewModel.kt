package org.clc.justgoup.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.RecentClimbingSession

class HomeViewModel(
    private val climbingSessionRepository: ClimbingSessionRepository
) : ViewModel() {

    private val _recentSessions = MutableStateFlow<List<RecentClimbingSession>>(emptyList())
    val recentSessions: StateFlow<List<RecentClimbingSession>> = _recentSessions

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private var endReached = false

    init {
        viewModelScope.launch {
            val page = climbingSessionRepository.findRecentSessions(offset = 0, limit = PAGE_SIZE)
            _recentSessions.value = page
            endReached = page.size < PAGE_SIZE
        }
    }

    fun loadMore() {
        if (endReached || _isLoadingMore.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val page = climbingSessionRepository.findRecentSessions(
                offset = _recentSessions.value.size,
                limit = PAGE_SIZE
            )
            _recentSessions.value = _recentSessions.value + page
            endReached = page.size < PAGE_SIZE
            _isLoadingMore.value = false
        }
    }

    fun deleteSession(id: String) {
        _recentSessions.value = _recentSessions.value.filterNot { it.id == id }
        viewModelScope.launch {
            climbingSessionRepository.deleteSession(id)
        }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
