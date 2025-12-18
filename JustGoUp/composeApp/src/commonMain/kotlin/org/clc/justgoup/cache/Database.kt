package org.clc.justgoup.cache

import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.climbingSession.ClimbingSession

internal class Database(
    databaseDriverFactory: DatabaseDriverFactory
) {
    private val database = JustGoUpDatabase(
        driver = databaseDriverFactory.createDriver(),
        climbing_sessionAdapter = Climbing_session.Adapter(
            startTimeAdapter = LocalDateTimeAdapter,
            endTimeAdapter = LocalDateTimeAdapter
        )
    )

    private val queries = database.justGoUpDatabaseQueries

    internal suspend fun findAllSessions(): List<ClimbingSession> =
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            mapSessionsWithBoulders(
                queries.findSessionsWithBoulders { sessionId,
                                                   sessionLocation,
                                                   sessionStartTime,
                                                   sessionEndTime,
                                                   sessionNotes,
                                                   boulderId,
                                                   boulderGradeType,
                                                   boulderGradeValue,
                                                   boulderAttempts,
                                                   boulderSent,
                                                   boulderFlash,
                                                   boulderColor,
                                                   boulderNotes ->
                    row(
                        sessionId,
                        sessionLocation,
                        sessionStartTime,
                        sessionEndTime,
                        sessionNotes,
                        boulderId,
                        boulderGradeType,
                        boulderGradeValue,
                        boulderAttempts,
                        boulderSent,
                        boulderFlash,
                        boulderColor,
                        boulderNotes
                    )
                }.executeAsList()
            )
        }

    internal suspend fun findSessionWithBouldersById(
        sessionId: String
    ): ClimbingSession? =
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            mapSessionsWithBoulders(
                queries.findSessionWithBouldersById(sessionId) { sessionId,
                                                                 sessionLocation,
                                                                 sessionStartTime,
                                                                 sessionEndTime,
                                                                 sessionNotes,
                                                                 boulderId,
                                                                 boulderGradeType,
                                                                 boulderGradeValue,
                                                                 boulderAttempts,
                                                                 boulderSent,
                                                                 boulderFlash,
                                                                 boulderColor,
                                                                 boulderNotes ->
                    row(
                        sessionId,
                        sessionLocation,
                        sessionStartTime,
                        sessionEndTime,
                        sessionNotes,
                        boulderId,
                        boulderGradeType,
                        boulderGradeValue,
                        boulderAttempts,
                        boulderSent,
                        boulderFlash,
                        boulderColor,
                        boulderNotes
                    )
                }.executeAsList()
            ).firstOrNull()
        }

    internal suspend fun insertSession(
        id: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime?,
        notes: String?
    ) = withContext(kotlinx.coroutines.Dispatchers.IO) {
        queries.insertSession(
            id = id,
            location = location,
            startTime = startTime,
            endTime = endTime,
            notes = notes
        )
    }

    internal suspend fun insertBoulder(
        id: String,
        sessionId: String,
        grade: Grade,
        attempts: Int,
        sent: Boolean,
        flash: Boolean,
        color: HoldColor,
        notes: String?
    ) = withContext(kotlinx.coroutines.Dispatchers.IO) {
        val (type, value) = GradeAdapter.encode(grade)
        queries.insertBoulder(
            id = id,
            sessionId = sessionId,
            gradeType = type,
            gradeValue = value,
            attempts = attempts.toLong(),
            sent = if (sent) 1L else 0L,
            flash = if (flash) 1L else 0L,
            color = color.name,
            notes = notes
        )
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
                    endTime = row.endTime,
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
                        color = row.color?.let(HoldColor::valueOf),
                        notes = row.boulderNotes
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
        sessionEndTime: LocalDateTime?,
        sessionNotes: String?,
        boulderId: String?,
        boulderGradeType: String?,
        boulderGradeValue: String?,
        boulderAttempts: Long?,
        boulderSent: Long?,
        boulderFlash: Long?,
        boulderColor: String?,
        boulderNotes: String?
    ) = SessionWithBoulderRow(
        sessionId = sessionId,
        location = sessionLocation,
        startTime = sessionStartTime,
        endTime = sessionEndTime,
        notes = sessionNotes,
        boulderId = boulderId,
        gradeType = boulderGradeType,
        gradeValue = boulderGradeValue,
        attempts = boulderAttempts,
        sent = boulderSent,
        flash = boulderFlash,
        color = boulderColor,
        boulderNotes = boulderNotes
    )
}

private data class SessionWithBoulderRow(
    val sessionId: String,
    val location: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val notes: String?,
    val boulderId: String?,
    val gradeType: String?,
    val gradeValue: String?,
    val attempts: Long?,
    val sent: Long?,
    val flash: Long?,
    val color: String?,
    val boulderNotes: String?
)
