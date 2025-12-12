package org.clc.justgoup.ui.helpers

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

fun LocalDateTime.asShortDate(): String =
    "${day.toString().padStart(2, '0')}." +
            "${month.number.toString().padStart(2, '0')}." +
            "$year"

fun LocalDateTime.asFullDate(): String =
    "$day ${month.name.lowercase().replaceFirstChar { it.uppercase() }} $year"

@OptIn(ExperimentalTime::class)
fun durationMinutes(
    start: LocalDateTime,
    end: LocalDateTime
): Long {
    val zone = TimeZone.currentSystemDefault()
    val startInstant = start.toInstant(zone)
    val endInstant = end.toInstant(zone)

    return (endInstant - startInstant).inWholeMinutes
}

fun formatDuration(minutes: Long): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours > 0 -> "${hours}h ${mins}m"
        else -> "${mins}m"
    }
}

fun LocalDateTime.formatTime(): String {
    val h = time.hour.toString().padStart(2, '0')
    val m = time.minute.toString().padStart(2, '0')
    return "$h:$m"
}
