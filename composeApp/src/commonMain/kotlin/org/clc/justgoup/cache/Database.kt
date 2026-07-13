package org.clc.justgoup.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.RecentClimbingSession
import org.clc.justgoup.climbingSession.sessionTitleFor
import kotlin.time.ExperimentalTime

internal class Database(
    databaseDriverFactory: DatabaseDriverFactory
) {
    private val database = JustGoUpDatabase(
        driver = databaseDriverFactory.createDriver(),
        climbing_sessionAdapter = Climbing_session.Adapter(
            startTimeAdapter = LocalDateTimeAdapter
        ),
        boulderAdapter = org.clc.justgoup.cache.Boulder.Adapter(
            createdAtAdapter = LocalDateTimeAdapter
        )
    )

    private val queries = database.justGoUpDatabaseQueries

    internal suspend fun findAllSessions(): List<ClimbingSession> =
        withContext(Dispatchers.IO) {
            mapSessionsWithBoulders(
                queries.findSessionsWithBoulders { sessionId,
                                                   sessionLocation,
                                                   sessionStartTime,
                                                   sessionNotes,
                                                   boulderId,
                                                   boulderGradeType,
                                                   boulderGradeValue,
                                                   boulderAttempts,
                                                   boulderSent,
                                                   boulderFlash,
                                                   boulderRepeated,
                                                   boulderColor,
                                                   boulderNotes,
                                                   boulderCreatedAt ->
                    row(
                        sessionId,
                        sessionLocation,
                        sessionStartTime,
                        sessionNotes,
                        boulderId,
                        boulderGradeType,
                        boulderGradeValue,
                        boulderAttempts,
                        boulderSent,
                        boulderFlash,
                        boulderRepeated,
                        boulderColor,
                        boulderNotes,
                        boulderCreatedAt
                    )
                }.executeAsList()
            )
        }

    internal suspend fun findRecentSessionsPage(offset: Int, limit: Int): List<RecentClimbingSession> =
        withContext(Dispatchers.IO) {
            queries.findRecentSessionsPage(
                limit = limit.toLong(),
                offset = offset.toLong()
            ) { sessionId, location, startTime, boulderCount ->
                RecentClimbingSession(
                    id = sessionId,
                    title = sessionTitleFor(startTime),
                    location = location,
                    date = startTime,
                    boulders = boulderCount.toInt()
                )
            }.executeAsList()
        }

    internal suspend fun findSessionWithBouldersById(
        sessionId: String
    ): ClimbingSession? =
        withContext(Dispatchers.IO) {
            mapSessionsWithBoulders(
                queries.findSessionWithBouldersById(sessionId) { sessionId,
                                                                 sessionLocation,
                                                                 sessionStartTime,
                                                                 sessionNotes,
                                                                 boulderId,
                                                                 boulderGradeType,
                                                                 boulderGradeValue,
                                                                 boulderAttempts,
                                                                 boulderSent,
                                                                 boulderFlash,
                                                                 boulderRepeated,
                                                                 boulderColor,
                                                                 boulderNotes,
                                                                 boulderCreatedAt ->
                    row(
                        sessionId,
                        sessionLocation,
                        sessionStartTime,
                        sessionNotes,
                        boulderId,
                        boulderGradeType,
                        boulderGradeValue,
                        boulderAttempts,
                        boulderSent,
                        boulderFlash,
                        boulderRepeated,
                        boulderColor,
                        boulderNotes,
                        boulderCreatedAt
                    )
                }.executeAsList()
            ).firstOrNull()
        }

    internal suspend fun insertSession(
        id: String,
        location: String,
        startTime: LocalDateTime,
        notes: String?
    ) = withContext(Dispatchers.IO) {
        queries.insertSession(
            id = id,
            location = location,
            startTime = startTime,
            notes = notes
        )
    }

    internal suspend fun updateSession(
        updatedSession: ClimbingSession
    ) = withContext(Dispatchers.IO) {
        queries.updateSession(
            location = updatedSession.location,
            startTime = updatedSession.startTime,
            notes = updatedSession.notes,
            id = updatedSession.id
        )
    }

    internal suspend fun deleteSession(sessionId: String) = withContext(Dispatchers.IO) {
        queries.deleteSessionById(sessionId)
    }

    @OptIn(ExperimentalTime::class)
    internal suspend fun insertBoulder(sessionId: String, boulder: Boulder) = withContext(Dispatchers.IO) {
        val (type, value) = GradeAdapter.encode(boulder.grade)
        queries.insertBoulder(
            id = boulder.id,
            sessionId = sessionId,
            gradeType = type,
            gradeValue = value,
            attempts = boulder.attempts.toLong(),
            sent = if (boulder.sent) 1L else 0L,
            flash = if (boulder.flash) 1L else 0L,
            repeated = if (boulder.repeated) 1L else 0L,
            color = boulder.color?.name,
            notes = boulder.notes,
            createdAt = boulder.createdAt
        )
    }

    internal suspend fun findBoulderById(boulderId: String): Boulder? =
        withContext(Dispatchers.IO) {
            queries.findBoulderById(boulderId) { id, _, gradeType, gradeValue,
                                                 attempts, sent, flash, repeated,
                                                 color, notes, createdAt ->
                Boulder(
                    id = id,
                    grade = GradeAdapter.decode(gradeType, gradeValue),
                    attempts = attempts.toInt(),
                    sent = sent != 0L,
                    flash = flash != 0L,
                    repeated = repeated != 0L,
                    color = color?.let(HoldColor::valueOf),
                    notes = notes,
                    createdAt = createdAt
                )
            }.executeAsOneOrNull()
        }

    internal suspend fun updateBoulder(
        boulder: Boulder
    ) = withContext(Dispatchers.IO) {
        val (type, value) = GradeAdapter.encode(boulder.grade)
        queries.updateBoulder(
            id = boulder.id,
            gradeType = type,
            gradeValue = value,
            attempts = boulder.attempts.toLong(),
            sent = if (boulder.sent) 1L else 0L,
            flash = if (boulder.flash) 1L else 0L,
            repeated = if (boulder.repeated) 1L else 0L,
            color = boulder.color?.name,
            notes = boulder.notes
        )
    }

    internal suspend fun deleteBoulder(boulderId: String) = withContext(Dispatchers.IO) {
        queries.deleteBoulderById(boulderId)
    }

    private fun mapSessionsWithBoulders(
        rows: List<SessionWithBoulderRow>
    ): List<ClimbingSession> {
        val sessionsById = LinkedHashMap<String, ClimbingSession>()

        rows.forEach { row ->
            val session = sessionsById.getOrPut(row.sessionId) {
                ClimbingSession(
                    id = row.sessionId,
                    location = row.location,
                    startTime = row.startTime,
                    notes = row.notes,
                    boulders = mutableListOf()
                )
            }

            if (row.boulderId != null) {
                (session.boulders as MutableList).add(
                    Boulder(
                        id = row.boulderId,
                        grade = GradeAdapter.decode(row.gradeType!!, row.gradeValue),
                        attempts = row.attempts!!.toInt(),
                        sent = row.sent != 0L,
                        flash = row.flash != 0L,
                        repeated = row.repeated != 0L,
                        color = row.color?.let(HoldColor::valueOf),
                        notes = row.boulderNotes,
                        createdAt = row.boulderCreatedAt!!
                    )
                )
            }
        }

        return sessionsById.values.toList()
    }

    private fun row(
        sessionId: String,
        sessionLocation: String,
        sessionStartTime: LocalDateTime,
        sessionNotes: String?,
        boulderId: String?,
        boulderGradeType: String?,
        boulderGradeValue: String?,
        boulderAttempts: Long?,
        boulderSent: Long?,
        boulderFlash: Long?,
        boulderRepeated: Long?,
        boulderColor: String?,
        boulderNotes: String?,
        boulderCreatedAt: LocalDateTime?
    ) = SessionWithBoulderRow(
        sessionId = sessionId,
        location = sessionLocation,
        startTime = sessionStartTime,
        notes = sessionNotes,
        boulderId = boulderId,
        gradeType = boulderGradeType,
        gradeValue = boulderGradeValue,
        attempts = boulderAttempts,
        sent = boulderSent,
        flash = boulderFlash,
        repeated = boulderRepeated,
        color = boulderColor,
        boulderNotes = boulderNotes,
        boulderCreatedAt = boulderCreatedAt
    )
}

private data class SessionWithBoulderRow(
    val sessionId: String,
    val location: String,
    val startTime: LocalDateTime,
    val notes: String?,
    val boulderId: String?,
    val gradeType: String?,
    val gradeValue: String?,
    val attempts: Long?,
    val sent: Long?,
    val flash: Long?,
    val repeated: Long?,
    val color: String?,
    val boulderNotes: String?,
    val boulderCreatedAt: LocalDateTime?
)
