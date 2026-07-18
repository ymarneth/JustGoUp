package org.clc.justgoup.ui.climbingSessionDetail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.SessionComparison
import org.clc.justgoup.testsupport.FakeClimbingSessionRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SessionDetailViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init populates session and stats computed against other sessions at the same gym`() {
        val current = session(id = "current", boulders = listOf(boulder(sent = true), boulder(sent = false)))
        val others = (1..3).map { session(id = "other-$it", boulders = listOf(boulder(sent = true))) }
        val repository = FakeClimbingSessionRepository(sessionsToExport = listOf(current) + others)

        val viewModel = SessionDetailViewModel(repository, "current")

        assertEquals(current, viewModel.session.value)
        assertEquals(0.5, viewModel.sessionStats.value?.sendRate)
        assertEquals(SessionComparison.WORSE, viewModel.sessionStats.value?.comparisonToUsual)
    }

    @Test
    fun `deleting a boulder refreshes both the session and its stats without a manual refresh`() {
        val doomedBoulder = boulder(sent = false)
        val current = session(id = "current", boulders = listOf(boulder(sent = true), doomedBoulder))
        val repository = FakeClimbingSessionRepository(sessionsToExport = listOf(current))

        val viewModel = SessionDetailViewModel(repository, "current")
        assertEquals(2, viewModel.session.value?.totalBoulders)
        assertEquals(0.5, viewModel.sessionStats.value?.sendRate)

        viewModel.deleteBoulder(doomedBoulder.id)

        assertEquals(1, viewModel.session.value?.totalBoulders)
        assertEquals(1.0, viewModel.sessionStats.value?.sendRate)
    }

    @Test
    fun `comparison is null without enough other sessions at the same gym`() {
        val current = session(id = "current", boulders = listOf(boulder(sent = true)))
        val repository = FakeClimbingSessionRepository(sessionsToExport = listOf(current))

        val viewModel = SessionDetailViewModel(repository, "current")

        assertNull(viewModel.sessionStats.value?.comparisonToUsual)
    }

    private fun session(id: String, boulders: List<Boulder> = emptyList()) = ClimbingSession(
        id = id,
        location = "Gym A",
        startTime = LocalDateTime.parse("2026-01-01T09:00:00"),
        boulders = boulders
    )

    private fun boulder(sent: Boolean) = Boulder(grade = Grade.None, attempts = 1, sent = sent)
}
