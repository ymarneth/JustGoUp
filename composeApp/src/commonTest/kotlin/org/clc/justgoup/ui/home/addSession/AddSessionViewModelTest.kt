package org.clc.justgoup.ui.home.addSession

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.climbingSession.RecentClimbingSession
import org.clc.justgoup.testsupport.FakeClimbingSessionRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AddSessionViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads recent locations from the repository in order`() {
        val repository = FakeClimbingSessionRepository(
            recentSessions = listOf(session("s1", "Gym A"), session("s2", "Gym B"))
        )

        val viewModel = AddSessionViewModel(repository)

        assertEquals(listOf("Gym A", "Gym B"), viewModel.recentLocations.value)
    }

    @Test
    fun `recent locations is empty when the repository has no sessions`() {
        val viewModel = AddSessionViewModel(FakeClimbingSessionRepository())

        assertEquals(emptyList(), viewModel.recentLocations.value)
    }

    @Test
    fun `duplicate locations across multiple sessions collapse to a single suggestion`() {
        val repository = FakeClimbingSessionRepository(
            recentSessions = listOf(
                session("s1", "Gym A"),
                session("s2", "Gym B"),
                session("s3", "Gym A")
            )
        )

        val viewModel = AddSessionViewModel(repository)

        assertEquals(listOf("Gym A", "Gym B"), viewModel.recentLocations.value)
    }

    @Test
    fun `more than 5 distinct locations are capped to 5`() {
        val repository = FakeClimbingSessionRepository(
            recentSessions = (1..7).map { session("s$it", "Gym $it") }
        )

        val viewModel = AddSessionViewModel(repository)

        assertEquals(listOf("Gym 1", "Gym 2", "Gym 3", "Gym 4", "Gym 5"), viewModel.recentLocations.value)
    }

    private fun session(id: String, location: String) = RecentClimbingSession(
        id = id,
        title = "Session",
        location = location,
        date = LocalDateTime.parse("2026-01-01T09:00:00"),
        boulders = 0
    )
}
