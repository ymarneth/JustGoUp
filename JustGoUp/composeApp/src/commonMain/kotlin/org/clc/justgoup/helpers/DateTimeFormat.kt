package org.clc.justgoup.helpers

import kotlinx.datetime.*
import kotlinx.datetime.number

fun LocalDateTime.asShortDate(): String =
    "${day.toString().padStart(2, '0')}." +
            "${month.number.toString().padStart(2, '0')}." +
            "$year"

fun LocalDateTime.asFullDate(): String =
    "$day ${month.name.lowercase().replaceFirstChar { it.uppercase() }} $year"
