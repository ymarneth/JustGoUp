package org.clc.justgoup.climbingSession

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object ClimbingSessionRepository {
    fun getSessionById(sessionId: String): Flow<ClimbingSession?> = flow {
        val session = sampleSessions.find { it.id == sessionId }
        emit(session)
    }

    fun getRecentSessions(): Flow<List<RecentClimbingSession>> = flow {
        emit(sampleSessions.map { it.toRecent() })
    }
}
