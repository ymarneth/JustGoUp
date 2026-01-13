package org.clc.justgoup.climbingSession

import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.cache.Database


interface ClimbingSessionRepository {
    suspend fun findRecentSessions(): List<RecentClimbingSession>
    suspend fun getSessionById(id: String): ClimbingSession?
    suspend fun startSession(command: StartClimbingSessionCommand): ClimbingSession
    suspend fun updateSession(id: String, command: ClimbingSessionUpdateCommand)
    suspend fun addBoulderToSession(sessionId: String, command: CreateBoulderCommand)
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

    override suspend fun getSessionById(id: String): ClimbingSession? =
        database.findSessionWithBouldersById(id)

    override suspend fun startSession(command: StartClimbingSessionCommand): ClimbingSession {
        val session = ClimbingSession(
            location = command.location,
            notes = command.notes
        )

        database.insertSession(
            id = session.id,
            location = session.location,
            startTime = session.startTime,
            notes = session.notes
        )

        return getSessionById(session.id)
            ?: throw IllegalStateException("Session ${session.id} was inserted but not found")
    }

    override suspend fun updateSession(id: String, command: ClimbingSessionUpdateCommand) {
        val session = getSessionById(id) ?: return

        database.updateSession(
            session = session,
            location = command.location,
            startTime = command.startTime,
            notes = command.notes
        )
    }

    override suspend fun addBoulderToSession(sessionId: String, command: CreateBoulderCommand) {
        val boulder = Boulder(
            grade = command.grade,
            attempts = command.attempts,
            sent = command.sent,
            flash = command.flash,
            color = command.color,
            notes = command.notes,
        )

        database.insertBoulder(sessionId, boulder)
    }
}

data class StartClimbingSessionCommand(
    val location: String,
    val notes: String?
)

data class ClimbingSessionUpdateCommand(
    val location: String? = null,
    val startTime: LocalDateTime? = null,
    val notes: String? = null
)

data class CreateBoulderCommand(
    val grade: Grade,
    val attempts: Int,
    val sent: Boolean,
    val flash: Boolean = false,
    val color: HoldColor? = null,
    val notes: String? = null,
)
