package org.clc.justgoup.ui.stats

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.computeGymStats
import org.clc.justgoup.testsupport.FakeClimbingSessionRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init populates gym stats computed from every exported session`() {
        val sessions = listOf(
            session("Gym A"),
            session("Gym A"),
            session("Gym B")
        )
        val repository = FakeClimbingSessionRepository(sessionsToExport = sessions)

        val viewModel = StatsViewModel(repository)

        assertEquals(computeGymStats(sessions), viewModel.gymStats.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `gym stats is empty and loading finishes when there are no sessions`() {
        val viewModel = StatsViewModel(FakeClimbingSessionRepository())

        assertEquals(emptyList(), viewModel.gymStats.value)
        assertFalse(viewModel.isLoading.value)
    }

    private fun session(location: String) = ClimbingSession(
        location = location,
        startTime = LocalDateTime.parse("2026-01-01T09:00:00"),
        boulders = listOf(Boulder(grade = Grade.None, attempts = 1, sent = false))
    )
}
