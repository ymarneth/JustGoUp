package org.clc.justgoup.cache

import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.climbingSession.ClimbingSession

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = JustGoUpDatabase(
        driver = databaseDriverFactory.createDriver(),
        climbing_sessionAdapter = Climbing_session.Adapter(
            startTimeAdapter = LocalDateTimeAdapter,
            endTimeAdapter = LocalDateTimeAdapter
        )
    )
    private val queries = database.justGoUpDatabaseQueries

    internal fun findAllSessions(): List<ClimbingSession> =
        queries.findAllSessions { id, location, startTime, endTime, notes ->
            ClimbingSession(
                id = id,
                location = location,
                startTime = startTime,
                endTime = endTime,
                notes = notes,
                boulders = emptyList() // loaded separately
            )
        }.executeAsList()

    internal fun findSessionById(sessionId: String): ClimbingSession? =
        queries.findSessionById(sessionId) { id, location, startTime, endTime, notes ->
            ClimbingSession(
                id = id,
                location = location,
                startTime = startTime,
                endTime = endTime,
                notes = notes,
                boulders = emptyList()
            )
        }.executeAsOneOrNull()

    internal fun findBoulders(sessionId: String): List<Boulder> =
        queries.findBouldersBySessionId(sessionId) { id, sessionId, gradeType, gradeValue,
                                                     attempts, sent, flash, color, notes ->
            Boulder(
                id = id,
                grade = GradeAdapter.decode(gradeType, gradeValue),
                attempts = attempts.toInt(),
                sent = sent != 0L,
                flash = flash != 0L,
                color = color?.let(HoldColor::valueOf),
                notes = notes
            )
        }.executeAsList()

    internal fun findFullSession(sessionId: String): ClimbingSession? {
        val session = findSessionById(sessionId) ?: return null
        val boulders = findBoulders(sessionId)
        return session.copy(boulders = boulders)
    }
}
