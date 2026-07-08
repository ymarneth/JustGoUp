package org.clc.justgoup.export

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.FrenchGrade
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.boulder.VGrade
import org.clc.justgoup.climbingSession.ClimbingSession
import kotlin.test.Test
import kotlin.test.assertEquals

class BackupTest {

    @Test
    fun `boulder with french grade round-trips through backup mapping`() {
        val boulder = Boulder(
            id = "b1",
            grade = Grade.French(FrenchGrade(number = 6, letter = 'a', modifier = FrenchGrade.Modifier.Plus)),
            attempts = 3,
            sent = true,
            flash = false,
            repeated = true,
            color = HoldColor.BLUE,
            notes = "crimpy",
            createdAt = LocalDateTime.parse("2026-07-08T10:15:30")
        )

        assertEquals(boulder, boulder.toBackup().toDomain())
    }

    @Test
    fun `boulder with v-scale grade round-trips through backup mapping`() {
        val boulder = Boulder(
            id = "b2",
            grade = Grade.VScale(VGrade(value = 4, beginner = false, plus = true)),
            attempts = 1,
            sent = true,
            flash = true,
            repeated = false,
            color = null,
            notes = null,
            createdAt = LocalDateTime.parse("2026-01-01T00:00:00")
        )

        assertEquals(boulder, boulder.toBackup().toDomain())
    }

    @Test
    fun `boulder with no grade round-trips through backup mapping`() {
        val boulder = Boulder(
            id = "b3",
            grade = Grade.None,
            attempts = 1,
            sent = false,
            createdAt = LocalDateTime.parse("2026-01-01T00:00:00")
        )

        assertEquals(boulder, boulder.toBackup().toDomain())
    }

    @Test
    fun `climbing session with multiple boulders round-trips through backup mapping`() {
        val session = ClimbingSession(
            id = "s1",
            location = "Boulder Gym",
            startTime = LocalDateTime.parse("2026-07-08T09:00:00"),
            notes = "Good session",
            boulders = listOf(
                Boulder(
                    id = "b1",
                    grade = Grade.French(FrenchGrade(number = 5, letter = 'c')),
                    attempts = 2,
                    sent = true,
                    createdAt = LocalDateTime.parse("2026-07-08T09:10:00")
                ),
                Boulder(
                    id = "b2",
                    grade = Grade.VScale(VGrade(value = 2)),
                    attempts = 5,
                    sent = false,
                    createdAt = LocalDateTime.parse("2026-07-08T09:20:00")
                )
            )
        )

        assertEquals(session, session.toBackup().toDomain())
    }

    @Test
    fun `backup payload survives a json encode-decode round trip`() {
        val session = ClimbingSession(
            id = "s1",
            location = "Crag",
            startTime = LocalDateTime.parse("2026-07-08T09:00:00"),
            notes = null,
            boulders = listOf(
                Boulder(
                    id = "b1",
                    grade = Grade.French(FrenchGrade(number = 7)),
                    attempts = 1,
                    sent = true,
                    flash = true,
                    createdAt = LocalDateTime.parse("2026-07-08T09:05:00")
                )
            )
        )

        val payload = BackupPayload(
            exportedAt = "2026-07-08T12:00:00",
            sessions = listOf(session.toBackup())
        )

        val json = Json.encodeToString(payload)
        val decoded = Json.decodeFromString<BackupPayload>(json)

        assertEquals(payload, decoded)
        assertEquals(BACKUP_FORMAT_VERSION, decoded.version)
    }

    @Test
    fun `decoding a payload without a version field defaults to the current format version`() {
        val json = """{"exportedAt":"2026-07-08T12:00:00","sessions":[]}"""

        val decoded = Json.decodeFromString<BackupPayload>(json)

        assertEquals(BACKUP_FORMAT_VERSION, decoded.version)
    }
}
