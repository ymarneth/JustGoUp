package org.clc.justgoup.ui.helpers

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun LocalDateTime.asShortDateTime(): String {
    val local = this.toInstant(TimeZone.UTC).toLocalDateTime(TimeZone.currentSystemDefault())

    val dayStr = local.day.toString().padStart(2, '0')
    val monthStr = local.month.number.toString().padStart(2, '0')
    val hourStr = local.hour.toString().padStart(2, '0')
    val minuteStr = local.minute.toString().padStart(2, '0')

    return "$dayStr.$monthStr.${local.year}, $hourStr:$minuteStr"
}
