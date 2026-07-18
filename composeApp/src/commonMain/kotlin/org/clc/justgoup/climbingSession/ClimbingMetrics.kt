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
    val averageBouldersPerSession: Double,
    val hardestSentBySystem: Map<GradingSystem, Grade>,
    val workingGradeBySystem: Map<GradingSystem, Grade>,
    val trend: GymTrend?
)

enum class GymTrend { UP, DOWN, STEADY }

/**
 * Stats for a single session, plus how it compares to the climber's other sessions at the
 * same gym.
 */
data class SessionStats(
    val sendRate: Double?,
    val flashRate: Double?,
    val hardestSentBySystem: Map<GradingSystem, Grade>,
    val comparisonToUsual: SessionComparison?
)

enum class SessionComparison { BETTER, WORSE, TYPICAL }

private const val RECENT_WINDOW_SESSIONS = 5
private const val MIN_TOTAL_SESSIONS_FOR_TREND = 8
private const val MIN_BOULDERS_IN_WINDOW = 3
private const val TREND_FLAT_BAND = 0.05
private const val WORKING_GRADE_SEND_RATE_THRESHOLD = 0.75
private const val MIN_BOULDERS_AT_GRADE = 3
private const val MIN_SESSIONS_AT_GRADE = 2
private const val MIN_OTHER_SESSIONS_FOR_COMPARISON = 3
private const val SESSION_COMPARISON_FLAT_BAND = 0.05

/**
 * Computes per distinct [ClimbingSession.location] found in [sessions].
 */
fun computeGymStats(sessions: List<ClimbingSession>): List<GymStats> =
    sessions
        .groupBy { it.location.trim() }
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
        averageBouldersPerSession = boulders.size.toDouble() / size,
        hardestSentBySystem = hardestSentBySystem(boulders),
        workingGradeBySystem = workingGradeBySystem(this),
        trend = computeTrend(newestFirst)
    )
}

/**
 * Stats for [session] alone, compared against [otherSessionsAtSameGym] to say whether this
 * session ran better, worse, or typical for this gym. [session] itself is excluded from the
 * comparison even if present in [otherSessionsAtSameGym].
 */
fun computeSessionStats(session: ClimbingSession, otherSessionsAtSameGym: List<ClimbingSession>): SessionStats {
    val boulders = session.boulders
    val sendCount = boulders.count { it.sent }
    val flashCount = boulders.count { it.flash }
    val sendRate = boulders.takeIf { it.isNotEmpty() }?.let { sendCount.toDouble() / it.size }
    val others = otherSessionsAtSameGym.filterNot { it.id == session.id }

    return SessionStats(
        sendRate = sendRate,
        flashRate = sendCount.takeIf { it > 0 }?.let { flashCount.toDouble() / it },
        hardestSentBySystem = hardestSentBySystem(boulders),
        comparisonToUsual = compareToUsualPerformance(sendRate, others)
    )
}

private fun compareToUsualPerformance(
    sessionSendRate: Double?,
    otherSessionsAtSameGym: List<ClimbingSession>
): SessionComparison? {
    if (sessionSendRate == null) return null
    if (otherSessionsAtSameGym.size < MIN_OTHER_SESSIONS_FOR_COMPARISON) return null

    val otherBoulders = otherSessionsAtSameGym.flatMap { it.boulders }
    if (otherBoulders.isEmpty()) return null

    val usualSendRate = otherBoulders.count { it.sent }.toDouble() / otherBoulders.size
    val delta = sessionSendRate - usualSendRate

    return when {
        delta > SESSION_COMPARISON_FLAT_BAND -> SessionComparison.BETTER
        delta < -SESSION_COMPARISON_FLAT_BAND -> SessionComparison.WORSE
        else -> SessionComparison.TYPICAL
    }
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

// The working grade is the hardest exact grade (`+`/`-` counts as its own grade)
// sent at least WORKING_GRADE_SEND_RATE_THRESHOLD of the time, across at least
// MIN_SESSIONS_AT_GRADE sessions and MIN_BOULDERS_AT_GRADE attempts - unlike hardestSentBySystem
// (the occasional "project" send), this needs every attempt, not just the sends.
private fun workingGradeBySystem(sessions: List<ClimbingSession>): Map<GradingSystem, Grade> {
    val frenchEntries = sessions.flatMap { session ->
        session.boulders.mapNotNull {
            (it.grade as? Grade.French)?.value?.let { grade ->
                Attempt(
                    grade,
                    it.sent,
                    session.id
                )
            }
        }
    }
    val vEntries = sessions.flatMap { session ->
        session.boulders.mapNotNull {
            (it.grade as? Grade.VScale)?.value?.let { grade ->
                Attempt(
                    grade,
                    it.sent,
                    session.id
                )
            }
        }
    }

    val workingFrench = frenchEntries.workingGrade { it.rankScore() }?.let { Grade.French(it) }
    val workingV = vEntries.workingGrade { it.rankScore() }?.let { Grade.VScale(it) }

    return buildMap {
        workingFrench?.let { put(GradingSystem.FRENCH, it) }
        workingV?.let { put(GradingSystem.V_SCALE, it) }
    }
}

private data class Attempt<G>(val grade: G, val sent: Boolean, val sessionId: String)

private fun <G> List<Attempt<G>>.workingGrade(rankOf: (G) -> Double): G? =
    groupBy { it.grade }
        .filterValues { attempts -> attempts.size >= MIN_BOULDERS_AT_GRADE }
        .filterValues { attempts -> attempts.map { it.sessionId }.distinct().size >= MIN_SESSIONS_AT_GRADE }
        .filterValues { attempts ->
            attempts.count { it.sent }.toDouble() / attempts.size >= WORKING_GRADE_SEND_RATE_THRESHOLD
        }
        .keys
        .maxByOrNull(rankOf)

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
