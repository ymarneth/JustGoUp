package org.clc.justgoup.ui.helpers

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

fun LocalDateTime.asShortDate(): String =
    "${day.toString().padStart(2, '0')}." +
            "${month.number.toString().padStart(2, '0')}." +
            "$year"

fun LocalDateTime.asFullDate(): String =
    "$day ${month.name.lowercase().replaceFirstChar { it.uppercase() }} $year"
