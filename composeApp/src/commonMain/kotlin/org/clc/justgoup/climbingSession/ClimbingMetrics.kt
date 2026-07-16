package org.clc.justgoup.climbingSession

import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.FrenchGrade
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.GradingSystem
import org.clc.justgoup.boulder.VGrade

/**
 * Per-gym stats derived from the climber's full session history at one location.
 */
data class GymStats(
    val gym: String,
    val sessionCount: Int,
    val boulderCount: Int,
    val sendCount: Int,
    val flashCount: Int,
    val sendRate: Double?,
    val flashRate: Double?,
    val hardestSentBySystem: Map<GradingSystem, Grade>,
    val trend: GymTrend?
)

enum class GymTrend { UP, DOWN, STEADY }

private const val RECENT_WINDOW_SESSIONS = 5
private const val MIN_TOTAL_SESSIONS_FOR_TREND = 8
private const val MIN_BOULDERS_IN_WINDOW = 3
private const val TREND_FLAT_BAND = 0.05

/**
 * Computes per distinct [ClimbingSession.location] found in [sessions].
 */
fun computeGymStats(sessions: List<ClimbingSession>): List<GymStats> =
    sessions
        .groupBy { it.location }
        .map { (gym, gymSessions) -> gymSessions.toGymStats(gym) }
        .sortedWith(compareByDescending<GymStats> { it.sessionCount }.thenBy { it.gym })

private fun List<ClimbingSession>.toGymStats(gym: String): GymStats {
    val boulders = flatMap { it.boulders }
    val sendCount = boulders.count { it.sent }
    val flashCount = boulders.count { it.flash }
    val newestFirst = sortedByDescending { it.startTime }

    return GymStats(
        gym = gym,
        sessionCount = size,
        boulderCount = boulders.size,
        sendCount = sendCount,
        flashCount = flashCount,
        sendRate = boulders.takeIf { it.isNotEmpty() }?.let { sendCount.toDouble() / it.size },
        flashRate = sendCount.takeIf { it > 0 }?.let { flashCount.toDouble() / it },
        hardestSentBySystem = hardestSentBySystem(boulders),
        trend = computeTrend(newestFirst)
    )
}

private fun hardestSentBySystem(boulders: List<Boulder>): Map<GradingSystem, Grade> {
    val sent = boulders.filter { it.sent }

    val hardestFrench = sent.mapNotNull { (it.grade as? Grade.French)?.value }
        .maxByOrNull { it.rankScore() }
        ?.let { Grade.French(it) }

    val hardestV = sent.mapNotNull { (it.grade as? Grade.VScale)?.value }
        .maxByOrNull { it.rankScore() }
        ?.let { Grade.VScale(it) }

    return buildMap {
        hardestFrench?.let { put(GradingSystem.FRENCH, it) }
        hardestV?.let { put(GradingSystem.V_SCALE, it) }
    }
}

// A `+`/`-` shifts the score by 0.5
private fun FrenchGrade.rankScore(): Double =
    number * 10.0 + (letter?.let { it - 'a' + 1 } ?: 0) + modifier.toOffset()

private fun FrenchGrade.Modifier?.toOffset(): Double = when (this) {
    FrenchGrade.Modifier.Plus -> 0.5
    FrenchGrade.Modifier.Minus -> -0.5
    null -> 0.0
}

private fun VGrade.rankScore(): Double = when {
    beginner -> -1.0
    plus -> value + 0.5
    else -> value.toDouble()
}

private fun computeTrend(sessionsAtGymNewestFirst: List<ClimbingSession>): GymTrend? {
    if (sessionsAtGymNewestFirst.size < MIN_TOTAL_SESSIONS_FOR_TREND) return null

    val recentBoulders = sessionsAtGymNewestFirst.take(RECENT_WINDOW_SESSIONS).flatMap { it.boulders }
    if (recentBoulders.size < MIN_BOULDERS_IN_WINDOW) return null

    val overallBoulders = sessionsAtGymNewestFirst.flatMap { it.boulders }
    if (overallBoulders.isEmpty()) return null

    val recentRate = recentBoulders.count { it.sent }.toDouble() / recentBoulders.size
    val overallRate = overallBoulders.count { it.sent }.toDouble() / overallBoulders.size
    val delta = recentRate - overallRate

    return when {
        delta > TREND_FLAT_BAND -> GymTrend.UP
        delta < -TREND_FLAT_BAND -> GymTrend.DOWN
        else -> GymTrend.STEADY
    }
}
