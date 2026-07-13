package org.clc.justgoup.testsupport

import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.ClimbingSessionUpdateCommand
import org.clc.justgoup.climbingSession.CreateBoulderCommand
import org.clc.justgoup.climbingSession.RecentClimbingSession
import org.clc.justgoup.climbingSession.StartClimbingSessionCommand
import org.clc.justgoup.climbingSession.UpdateBoulderCommand

internal class FakeClimbingSessionRepository(
    recentSessions: List<RecentClimbingSession> = emptyList(),
    private val sessionsToExport: List<ClimbingSession> = emptyList(),
    private val restoreResult: Int = 0
) : ClimbingSessionRepository {

    private val recentSessionsBacking = recentSessions.toMutableList()

    val findRecentSessionsCalls = mutableListOf<Pair<Int, Int>>()
    val deletedIds = mutableListOf<String>()
    var restoredWith: List<ClimbingSession>? = null
        private set

    override suspend fun findRecentSessions(offset: Int, limit: Int): List<RecentClimbingSession> {
        findRecentSessionsCalls.add(offset to limit)
        return recentSessionsBacking.drop(offset).take(limit)
    }

    override suspend fun getSessionById(id: String): ClimbingSession? = error("not used in this test")

    override suspend fun startSession(command: StartClimbingSessionCommand): ClimbingSession =
        error("not used in this test")

    override suspend fun updateSession(id: String, command: ClimbingSessionUpdateCommand) = Unit

    override suspend fun deleteSession(id: String) {
        deletedIds.add(id)
        recentSessionsBacking.removeAll { it.id == id }
    }

    override suspend fun addBoulderToSession(sessionId: String, command: CreateBoulderCommand) = Unit
    override suspend fun updateBoulderInSession(boulderId: String, command: UpdateBoulderCommand) = Unit
    override suspend fun deleteBoulderFromSession(boulderId: String) = Unit
    override suspend fun exportAllSessions(): List<ClimbingSession> = sessionsToExport

    override suspend fun restoreSessions(sessions: List<ClimbingSession>): Int {
        restoredWith = sessions
        return restoreResult
    }
}
