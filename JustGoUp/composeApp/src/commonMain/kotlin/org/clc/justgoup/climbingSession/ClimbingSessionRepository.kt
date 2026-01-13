package org.clc.justgoup.climbingSession

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.cache.Database
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


interface ClimbingSessionRepository {
    suspend fun findRecentSessions(): List<RecentClimbingSession>
    suspend fun getSessionById(id: String): ClimbingSession?
    suspend fun startSession(command: StartClimbingSessionCommand): ClimbingSession
    suspend fun updateSession(id: String, command: ClimbingSessionUpdateCommand)
    suspend fun addBoulderToSession(id: String, boulder: Boulder)
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

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun startSession(command: StartClimbingSessionCommand): ClimbingSession {
        val sessionId = Uuid.random().toString()
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val session = ClimbingSession(
            id = sessionId,
            location = command.location,
            startTime = now,
            notes = command.notes
        )

        database.insertSession(
            id = session.id,
            location = session.location,
            startTime = session.startTime,
            notes = session.notes
        )

        return getSessionById(sessionId)
            ?: throw IllegalStateException("Session $sessionId was inserted but not found")
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

    override suspend fun addBoulderToSession(id: String, boulder: Boulder) {
        database.insertBoulder(
            id = boulder.id,
            sessionId = id,
            grade = boulder.grade,
            attempts = boulder.attempts,
            sent = boulder.sent,
            flash = boulder.flash,
            color = boulder.color ?: HoldColor.GREY,
            notes = boulder.notes
        )
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
