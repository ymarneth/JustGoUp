package org.clc.justgoup.ui.home

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
import kotlin.test.assertFalse

// Mirrors HomeViewModel's own private PAGE_SIZE constant.
private const val PAGE_SIZE = 20

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load fetches only the first page`() {
        val repository = FakeClimbingSessionRepository(sampleSessions(count = 25))
        val viewModel = HomeViewModel(repository)

        assertEquals(PAGE_SIZE, viewModel.recentSessions.value.size)
        assertEquals(listOf(0 to PAGE_SIZE), repository.findRecentSessionsCalls)
    }

    @Test
    fun `loadMore appends the next page`() {
        val repository = FakeClimbingSessionRepository(sampleSessions(count = 25))
        val viewModel = HomeViewModel(repository)

        viewModel.loadMore()

        assertEquals(25, viewModel.recentSessions.value.size)
        assertEquals(listOf(0 to PAGE_SIZE, PAGE_SIZE to PAGE_SIZE), repository.findRecentSessionsCalls)
    }

    @Test
    fun `loadMore stops fetching once a page comes back shorter than the page size`() {
        val repository = FakeClimbingSessionRepository(sampleSessions(count = 25))
        val viewModel = HomeViewModel(repository)

        viewModel.loadMore() // fetches the remaining 5, reaches the end
        viewModel.loadMore() // should be a no-op now
        viewModel.loadMore()

        assertEquals(25, viewModel.recentSessions.value.size)
        assertEquals(2, repository.findRecentSessionsCalls.size)
    }

    @Test
    fun `deleteSession removes the session from the list immediately`() {
        val repository = FakeClimbingSessionRepository(sampleSessions(count = 25))
        val viewModel = HomeViewModel(repository)
        val toDelete = viewModel.recentSessions.value.first()

        viewModel.deleteSession(toDelete.id)

        assertFalse(viewModel.recentSessions.value.any { it.id == toDelete.id })
        assertEquals(listOf(toDelete.id), repository.deletedIds)
    }

    @Test
    fun `loadMore after a delete does not skip or repeat sessions`() {
        val repository = FakeClimbingSessionRepository(sampleSessions(count = 25))
        val viewModel = HomeViewModel(repository)
        val toDelete = viewModel.recentSessions.value[5]

        viewModel.deleteSession(toDelete.id)
        viewModel.loadMore()

        val ids = viewModel.recentSessions.value.map { it.id }
        assertEquals(24, ids.size)
        assertEquals(ids.distinct().size, ids.size)
        assertFalse(ids.contains(toDelete.id))
    }
}

private fun sampleSessions(count: Int): List<RecentClimbingSession> =
    (0 until count).map { index ->
        val day = (28 - index).toString().padStart(2, '0')
        RecentClimbingSession(
            id = "s$index",
            title = "Session",
            location = "Crag",
            date = LocalDateTime.parse("2026-01-${day}T09:00:00"),
            boulders = 0
        )
    }
