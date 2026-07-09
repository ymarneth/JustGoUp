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
    suspend fun deleteSession(id: String)
    suspend fun addBoulderToSession(sessionId: String, command: CreateBoulderCommand)
    suspend fun updateBoulderInSession(boulderId: String, command: UpdateBoulderCommand)
    suspend fun deleteBoulderFromSession(boulderId: String)
    suspend fun exportAllSessions(): List<ClimbingSession>
    suspend fun restoreSessions(sessions: List<ClimbingSession>): Int
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

        val updatedSession = session.copy(
            location = command.location ?: session.location,
            startTime = command.startTime ?: session.startTime,
            notes = command.notes ?: session.notes
        )

        database.updateSession(updatedSession)
    }

    override suspend fun deleteSession(id: String) {
        database.deleteSession(id)
    }

    override suspend fun addBoulderToSession(sessionId: String, command: CreateBoulderCommand) {
        val boulder = Boulder(
            grade = command.grade,
            attempts = command.attempts,
            sent = command.sent,
            flash = command.flash,
            repeated = command.repeated,
            color = command.color,
            notes = command.notes,
        )

        database.insertBoulder(sessionId, boulder)
    }

    override suspend fun updateBoulderInSession(boulderId: String, command: UpdateBoulderCommand) {
        val boulder = database.findBoulderById(boulderId) ?: return

        val updatedBoulder = boulder.copy(
            grade = command.grade ?: boulder.grade,
            attempts = command.attempts ?: boulder.attempts,
            sent = command.sent ?: boulder.sent,
            flash = command.flash ?: boulder.flash,
            repeated = command.repeated ?: boulder.repeated,
            color = command.color ?: boulder.color,
            notes = command.notes ?: boulder.notes
        )

        database.updateBoulder(updatedBoulder)
    }

    override suspend fun deleteBoulderFromSession(boulderId: String) {
        database.deleteBoulder(boulderId)
    }

    override suspend fun exportAllSessions(): List<ClimbingSession> =
        database.findAllSessions()

    override suspend fun restoreSessions(sessions: List<ClimbingSession>): Int {
        val existingIds = database.findAllSessions().map { it.id }.toSet()
        val newSessions = sessions.excludingExistingIds(existingIds)

        newSessions.forEach { session ->
            database.insertSession(
                id = session.id,
                location = session.location,
                startTime = session.startTime,
                notes = session.notes
            )
            session.boulders.forEach { boulder -> database.insertBoulder(session.id, boulder) }
        }

        return newSessions.size
    }
}

internal fun List<ClimbingSession>.excludingExistingIds(existingIds: Set<String>): List<ClimbingSession> =
    filterNot { it.id in existingIds }

data class StartClimbingSessionCommand(
    val location: String,
    val notes: String?
)

data class ClimbingSessionUpdateCommand(
    val location: String?,
    val startTime: LocalDateTime?,
    val notes: String?
)

data class CreateBoulderCommand(
    val grade: Grade,
    val attempts: Int,
    val sent: Boolean,
    val flash: Boolean = false,
    val repeated: Boolean = false,
    val color: HoldColor? = null,
    val notes: String? = null,
)

data class UpdateBoulderCommand(
    val grade: Grade?,
    val attempts: Int?,
    val sent: Boolean?,
    val flash: Boolean?,
    val repeated: Boolean?,
    val color: HoldColor?,
    val notes: String?,
)
