package org.clc.justgoup.ui.settings

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.FrenchGrade
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.export.BACKUP_FORMAT_VERSION
import org.clc.justgoup.export.BackupPayload
import org.clc.justgoup.export.toBackup
import org.clc.justgoup.testsupport.FakeClimbingSessionRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SettingsViewModelTest {

    private val sampleSession = ClimbingSession(
        id = "s1",
        location = "Crag",
        startTime = LocalDateTime.parse("2026-07-08T09:00:00"),
        notes = null,
        boulders = listOf(
            Boulder(
                id = "b1",
                grade = Grade.French(FrenchGrade(number = 6)),
                attempts = 2,
                sent = true,
                createdAt = LocalDateTime.parse("2026-07-08T09:05:00")
            )
        )
    )

    @Test
    fun `exportToJson serializes every session from the repository`() = runBlocking {
        val repository = FakeClimbingSessionRepository(sessionsToExport = listOf(sampleSession))
        val viewModel = SettingsViewModel(repository)

        val json = viewModel.exportToJson()
        val payload = Json.decodeFromString<BackupPayload>(json)

        assertEquals(BACKUP_FORMAT_VERSION, payload.version)
        assertEquals(1, payload.sessions.size)
        assertEquals(sampleSession.id, payload.sessions.first().id)
        assertEquals(sampleSession.boulders.first().id, payload.sessions.first().boulders.first().id)
    }

    @Test
    fun `importFromJson restores sessions mapped back to domain objects`() = runBlocking {
        val repository = FakeClimbingSessionRepository(restoreResult = 1)
        val viewModel = SettingsViewModel(repository)
        val payload = BackupPayload(
            exportedAt = "2026-07-08T12:00:00",
            sessions = listOf(sampleSession.toBackup())
        )

        val result = viewModel.importFromJson(Json.encodeToString(payload))

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
        assertEquals(listOf(sampleSession), repository.restoredWith)
    }

    @Test
    fun `importFromJson fails on malformed json without touching the repository`() = runBlocking {
        val repository = FakeClimbingSessionRepository()
        val viewModel = SettingsViewModel(repository)

        val result = viewModel.importFromJson("not json")

        assertTrue(result.isFailure)
        assertNull(repository.restoredWith)
    }

    @Test
    fun `importFromJson rejects a backup from a newer format version`() = runBlocking {
        val repository = FakeClimbingSessionRepository()
        val viewModel = SettingsViewModel(repository)
        val futureJson = """{"version":${BACKUP_FORMAT_VERSION + 1},"exportedAt":"2026-07-08T12:00:00","sessions":[]}"""

        val result = viewModel.importFromJson(futureJson)

        assertTrue(result.isFailure)
        assertNull(repository.restoredWith)
    }
}
