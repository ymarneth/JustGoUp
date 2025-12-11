package org.clc.justgoup.climbingSession

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.boulder.VGrade

class ClimbingSessionRepository {
    private val sampleSessions = listOf(
        ClimbingSession(
            id = "1",
            location = "Boulderbar Linz",
            startTime = LocalDateTime(2025, 12, 5, 18, 30),
            endTime = LocalDateTime(2025, 12, 5, 20, 0),
            boulders = listOf(
                Boulder(
                    grade = Grade.VScale(VGrade(3)),
                    attempts = 1,
                    sent = true,
                    flash = true,
                    color = HoldColor.BLUE
                ),
                Boulder(
                    grade = Grade.VScale(VGrade(4)),
                    attempts = 2,
                    sent = true,
                    color = HoldColor.RED
                )
            )
        ),
        ClimbingSession(
            id = "2",
            location = "der Steinbock Linz",
            startTime = LocalDateTime(2025, 12, 3, 9, 0),
            endTime = LocalDateTime(2025, 12, 3, 10, 30),
            boulders = listOf(
                Boulder(
                    grade = Grade.VScale(VGrade(2)),
                    attempts = 1,
                    sent = true,
                    color = HoldColor.GREEN
                ),
                Boulder(grade = Grade.VScale(VGrade(3)), attempts = 2, sent = true, flash = true)
            )
        )
    )

    fun getRecentSessions(): Flow<List<RecentClimbingSession>> = flow {
        emit(sampleSessions.map { it.toRecent() })
    }
}
