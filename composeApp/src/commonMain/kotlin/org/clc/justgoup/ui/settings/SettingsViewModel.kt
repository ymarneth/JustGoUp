package org.clc.justgoup.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.export.BACKUP_FORMAT_VERSION
import org.clc.justgoup.export.BackupPayload
import org.clc.justgoup.export.toBackup
import org.clc.justgoup.export.toDomain
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SettingsViewModel(
    private val repository: ClimbingSessionRepository
) : ViewModel() {

    @OptIn(ExperimentalTime::class)
    suspend fun exportToJson(): String {
        val payload = BackupPayload(
            exportedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(),
            sessions = repository.exportAllSessions().map { it.toBackup() }
        )
        return Json.encodeToString(payload)
    }

    suspend fun importFromJson(jsonString: String): Result<Int> = runCatching {
        val payload = Json.decodeFromString<BackupPayload>(jsonString)

        require(payload.version <= BACKUP_FORMAT_VERSION) {
            "This backup was created with a newer version of JustGoUp and can't be read by this version of the app."
        }

        repository.restoreSessions(payload.sessions.map { it.toDomain() })
    }
}
