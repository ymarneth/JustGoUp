package org.clc.justgoup.climbingSession

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.clc.justgoup.boulder.Boulder
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
data class ClimbingSession(
    val id: String = Uuid.random().toString(),
    val location: String,
    val startTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val notes: String? = null,
    val boulders: List<Boulder> = emptyList()
) {
    val totalBoulders: Int get() = boulders.size
    val totalSends: Int get() = boulders.count { it.sent }
    val totalFlashes: Int get() = boulders.count { it.flash }
}

data class RecentClimbingSession(
    val id: String,
    val title: String,
    val location: String,
    val date: LocalDateTime,
    val boulders: Int
)

fun sessionTitleFor(startTime: LocalDateTime): String = when (startTime.hour) {
    in 5..11 -> "Morning Session"
    in 12..16 -> "Afternoon Session"
    in 17..21 -> "Evening Session"
    else -> "Night Session"
}

fun ClimbingSession.toRecent(): RecentClimbingSession = RecentClimbingSession(
    id = id,
    title = sessionTitleFor(startTime),
    location = location,
    date = startTime,
    boulders = totalBoulders
)
