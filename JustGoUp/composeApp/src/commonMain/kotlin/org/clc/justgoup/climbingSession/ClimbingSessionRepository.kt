package org.clc.justgoup.climbingSession

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.clc.justgoup.cache.Database


interface ClimbingSessionRepository {
    fun findRecentSessions(): Flow<List<RecentClimbingSession>>
    fun getSessionById(sessionId: String): Flow<ClimbingSession?>
}

internal class ClimbingSessionRepositoryImplementation(
    private val database: Database
) : ClimbingSessionRepository {

    private fun findAllSessions(): Flow<List<ClimbingSession>> = flow {
        val sessions = database.findAllSessions().map { session ->
            val boulders = database.findBoulders(session.id)
            session.copy(boulders = boulders)
        }
        emit(sessions)
    }

    override fun findRecentSessions(): Flow<List<RecentClimbingSession>> =
        findAllSessions()
            .map { sessions ->
                sessions
                    .sortedByDescending { it.startTime }
                    .map { it.toRecent() }
            }

    override fun getSessionById(sessionId: String): Flow<ClimbingSession?> = flow {
        emit(database.findFullSession(sessionId))
    }
}
