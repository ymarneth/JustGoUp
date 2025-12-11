package org.clc.justgoup.climbingSession

import kotlinx.datetime.LocalDateTime

data class ClimbingSession (
    val id: String,
    val title: String,
    val location: String,
    val date: LocalDateTime,
    val boulder: Int
)

data class RecentClimbingSession(
    val id: String,
    val title: String,
    val location: String,
    val date: LocalDateTime,
    val boulders: Int
)
