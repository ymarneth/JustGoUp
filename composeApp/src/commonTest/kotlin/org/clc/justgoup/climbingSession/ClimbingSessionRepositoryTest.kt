package org.clc.justgoup.climbingSession

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ClimbingSessionRepositoryTest {

    @Test
    fun `excludingExistingIds keeps only sessions whose id is not already present`() {
        val existing = session("existing-1")
        val new = session("new-1")

        val result = listOf(existing, new).excludingExistingIds(setOf("existing-1"))

        assertEquals(listOf(new), result)
    }

    @Test
    fun `excludingExistingIds returns everything when nothing exists yet`() {
        val sessions = listOf(session("a"), session("b"))

        val result = sessions.excludingExistingIds(emptySet())

        assertEquals(sessions, result)
    }

    @Test
    fun `excludingExistingIds returns nothing when every session already exists`() {
        val sessions = listOf(session("a"), session("b"))

        val result = sessions.excludingExistingIds(setOf("a", "b"))

        assertEquals(emptyList(), result)
    }

    private fun session(id: String) = ClimbingSession(
        id = id,
        location = "Test Gym",
        startTime = LocalDateTime.parse("2026-07-08T09:00:00"),
        notes = null
    )
}
