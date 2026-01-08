package org.clc.justgoup.climbingSession

import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder

data class ClimbingSession(
    val id: String,
    val location: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
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

fun ClimbingSession.toRecent(): RecentClimbingSession {
    val title = when (startTime.hour) {
        in 5..11 -> "Morning Session"
        in 12..16 -> "Afternoon Session"
        in 17..21 -> "Evening Session"
        else -> "Night Session"
    }

    return RecentClimbingSession(
        id = id,
        title = title,
        location = location,
        date = startTime,
        boulders = totalBoulders
    )
}
