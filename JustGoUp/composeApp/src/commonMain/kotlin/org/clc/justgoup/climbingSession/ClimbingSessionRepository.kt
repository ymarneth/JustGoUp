package org.clc.justgoup.climbingSession

import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.cache.Database


interface ClimbingSessionRepository {
    suspend fun findRecentSessions(): List<RecentClimbingSession>
    suspend fun getSessionById(sessionId: String): ClimbingSession?
    suspend fun startSession(session: ClimbingSession)
    suspend fun addBoulderToSession(sessionId: String, boulder: Boulder)
}

internal class ClimbingSessionRepositoryImpl(
    private val database: Database
) : ClimbingSessionRepository {

    override suspend fun findRecentSessions(): List<RecentClimbingSession> {
        val sessions = database.findAllSessions()
        return sessions
            .sortedByDescending { it.startTime }
            .map { it.toRecent() }
    }

    override suspend fun getSessionById(sessionId: String): ClimbingSession? =
        database.findSessionWithBouldersById(sessionId)

    override suspend fun startSession(session: ClimbingSession) {
        database.insertSession(
            id = session.id,
            location = session.location,
            startTime = session.startTime,
            endTime = session.endTime,
            notes = session.notes
        )
    }

    override suspend fun addBoulderToSession(sessionId: String, boulder: Boulder) {
        database.insertBoulder(
            id = boulder.id,
            sessionId = sessionId,
            grade = boulder.grade,
            attempts = boulder.attempts,
            sent = boulder.sent,
            flash = boulder.flash,
            color = boulder.color ?: HoldColor.GREY,
            notes = boulder.notes
        )
    }
}
