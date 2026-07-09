package org.clc.justgoup.export

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.cache.GradeAdapter
import org.clc.justgoup.climbingSession.ClimbingSession

@Serializable
data class BoulderBackup(
    val id: String,
    val gradeType: String,
    val gradeValue: String?,
    val attempts: Int,
    val sent: Boolean,
    val flash: Boolean,
    val repeated: Boolean,
    val color: String?,
    val notes: String?,
    val createdAt: String
)

@Serializable
data class SessionBackup(
    val id: String,
    val location: String,
    val startTime: String,
    val notes: String?,
    val boulders: List<BoulderBackup>
)

const val BACKUP_FORMAT_VERSION = 1

@Serializable
data class BackupPayload(
    val version: Int = BACKUP_FORMAT_VERSION,
    val exportedAt: String,
    val sessions: List<SessionBackup>
)

fun ClimbingSession.toBackup(): SessionBackup = SessionBackup(
    id = id,
    location = location,
    startTime = startTime.toString(),
    notes = notes,
    boulders = boulders.map { it.toBackup() }
)

fun Boulder.toBackup(): BoulderBackup {
    val (gradeType, gradeValue) = GradeAdapter.encode(grade)
    return BoulderBackup(
        id = id,
        gradeType = gradeType,
        gradeValue = gradeValue,
        attempts = attempts,
        sent = sent,
        flash = flash,
        repeated = repeated,
        color = color?.name,
        notes = notes,
        createdAt = createdAt.toString()
    )
}

fun SessionBackup.toDomain(): ClimbingSession = ClimbingSession(
    id = id,
    location = location,
    startTime = LocalDateTime.parse(startTime),
    notes = notes,
    boulders = boulders.map { it.toDomain() }
)

fun BoulderBackup.toDomain(): Boulder = Boulder(
    id = id,
    grade = GradeAdapter.decode(gradeType, gradeValue),
    attempts = attempts,
    sent = sent,
    flash = flash,
    repeated = repeated,
    color = color?.let(HoldColor::valueOf),
    notes = notes,
    createdAt = LocalDateTime.parse(createdAt)
)
