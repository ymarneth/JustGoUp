package org.clc.justgoup.climbingSession

import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.FrenchGrade
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.GradingSystem
import org.clc.justgoup.boulder.VGrade
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClimbingMetricsTest {

    @Test
    fun `computeGymStats on empty input returns empty list`() {
        assertEquals(emptyList(), computeGymStats(emptyList()))
    }

    @Test
    fun `a gym with boulders but zero sends shows a defined send rate and no flash rate or hardest sent`() {
        val sessions = listOf(
            session("Gym A", "2026-01-01T09:00:00", listOf(boulder(sent = false), boulder(sent = false)))
        )

        val stats = computeGymStats(sessions).single()

        assertEquals(0.0, stats.sendRate)
        assertNull(stats.flashRate)
        assertTrue(stats.hardestSentBySystem.isEmpty())
    }

    @Test
    fun `flash rate is flashes over sends not over all boulders`() {
        val boulders = List(10) { index ->
            when {
                index < 4 -> boulder(sent = true, flash = index < 2) // 4 sends, 2 of them flashes
                else -> boulder(sent = false)
            }
        }
        val stats = computeGymStats(listOf(session("Gym A", "2026-01-01T09:00:00", boulders))).single()

        assertEquals(0.4, stats.sendRate) // 4 / 10
        assertEquals(0.5, stats.flashRate) // 2 / 4
    }

    @Test
    fun `hardest sent is tracked independently per grading system`() {
        val boulders = listOf(
            boulder(grade = Grade.French(FrenchGrade(number = 6, letter = 'a')), sent = true),
            boulder(grade = Grade.VScale(VGrade(value = 4)), sent = true)
        )
        val stats = computeGymStats(listOf(session("Gym A", "2026-01-01T09:00:00", boulders))).single()

        assertEquals(
            Grade.French(FrenchGrade(number = 6, letter = 'a')),
            stats.hardestSentBySystem[GradingSystem.FRENCH]
        )
        assertEquals(Grade.VScale(VGrade(value = 4)), stats.hardestSentBySystem[GradingSystem.V_SCALE])
    }

    @Test
    fun `french ranking never lets a plus modifier outrank a genuinely higher grade`() {
        val boulders = listOf(
            boulder(
                grade = Grade.French(FrenchGrade(number = 6, letter = 'c', modifier = FrenchGrade.Modifier.Plus)),
                sent = true
            ),
            boulder(grade = Grade.French(FrenchGrade(number = 7, letter = 'a')), sent = true)
        )
        val stats = computeGymStats(listOf(session("Gym A", "2026-01-01T09:00:00", boulders))).single()

        val hardest = stats.hardestSentBySystem.getValue(GradingSystem.FRENCH) as Grade.French
        assertEquals(7, hardest.value.number)
        assertEquals('a', hardest.value.letter)
    }

    @Test
    fun `french ranking treats a plus as harder than the same grade without one`() {
        val boulders = listOf(
            boulder(grade = Grade.French(FrenchGrade(number = 6, letter = 'a')), sent = true),
            boulder(
                grade = Grade.French(FrenchGrade(number = 6, letter = 'a', modifier = FrenchGrade.Modifier.Plus)),
                sent = true
            )
        )
        val stats = computeGymStats(listOf(session("Gym A", "2026-01-01T09:00:00", boulders))).single()

        val hardest = stats.hardestSentBySystem.getValue(GradingSystem.FRENCH) as Grade.French
        assertEquals(FrenchGrade.Modifier.Plus, hardest.value.modifier)
    }

    @Test
    fun `french ranking treats a minus as easier than the same grade without one`() {
        val boulders = listOf(
            boulder(grade = Grade.French(FrenchGrade(number = 6, letter = 'a')), sent = true),
            boulder(
                grade = Grade.French(FrenchGrade(number = 6, letter = 'a', modifier = FrenchGrade.Modifier.Minus)),
                sent = true
            )
        )
        val stats = computeGymStats(listOf(session("Gym A", "2026-01-01T09:00:00", boulders))).single()

        val hardest = stats.hardestSentBySystem.getValue(GradingSystem.FRENCH) as Grade.French
        assertEquals(null, hardest.value.modifier)
    }

    @Test
    fun `v-scale beginner ranks below plain V0`() {
        val boulders = listOf(
            boulder(grade = Grade.VScale(VGrade(value = 0, beginner = true)), sent = true),
            boulder(grade = Grade.VScale(VGrade(value = 0)), sent = true)
        )
        val stats = computeGymStats(listOf(session("Gym A", "2026-01-01T09:00:00", boulders))).single()

        val hardest = stats.hardestSentBySystem.getValue(GradingSystem.V_SCALE) as Grade.VScale
        assertEquals(0, hardest.value.value)
        assertEquals(false, hardest.value.beginner)
    }

    @Test
    fun `v-scale ranking never lets a plus modifier outrank a genuinely higher grade`() {
        val boulders = listOf(
            boulder(grade = Grade.VScale(VGrade(value = 5, plus = true)), sent = true),
            boulder(grade = Grade.VScale(VGrade(value = 6)), sent = true)
        )
        val stats = computeGymStats(listOf(session("Gym A", "2026-01-01T09:00:00", boulders))).single()

        val hardest = stats.hardestSentBySystem.getValue(GradingSystem.V_SCALE) as Grade.VScale
        assertEquals(6, hardest.value.value)
    }

    @Test
    fun `v-scale ranking treats a plus as harder than the same grade without one`() {
        val boulders = listOf(
            boulder(grade = Grade.VScale(VGrade(value = 5)), sent = true),
            boulder(grade = Grade.VScale(VGrade(value = 5, plus = true)), sent = true)
        )
        val stats = computeGymStats(listOf(session("Gym A", "2026-01-01T09:00:00", boulders))).single()

        val hardest = stats.hardestSentBySystem.getValue(GradingSystem.V_SCALE) as Grade.VScale
        assertEquals(true, hardest.value.plus)
    }

    @Test
    fun `locations are grouped by exact string case-sensitively`() {
        val sessions = listOf(
            session("Boulder Gym", "2026-01-01T09:00:00"),
            session("boulder gym", "2026-01-02T09:00:00")
        )

        val stats = computeGymStats(sessions)

        assertEquals(2, stats.size)
    }

    @Test
    fun `trend is null with fewer than 8 total sessions at a gym`() {
        val sessions = (1..7).map { session("Gym A", "2026-01-0${it}T09:00:00", listOf(boulder(sent = true))) }

        assertNull(computeGymStats(sessions).single().trend)
    }

    @Test
    fun `trend is null when the recent window has fewer than 3 boulders`() {
        // 3 older sessions with boulders, 5 most recent sessions with none logged.
        val older = (1..3).map { session("Gym A", "2026-01-0${it}T09:00:00", listOf(boulder(sent = true))) }
        val recent = (4..8).map { session("Gym A", "2026-01-0${it}T09:00:00") }

        assertNull(computeGymStats(older + recent).single().trend)
    }

    @Test
    fun `trend is flat when the delta is small enough to stay inside the flat band`() {
        // Older 3 sessions: 1 sent out of 3. Recent 5 sessions: 2 sent out of 5.
        // Overall: 3/8 = 0.375, recent: 2/5 = 0.4, delta = 0.025 -- comfortably inside +/- 0.05,
        // with enough margin that this doesn't rely on hitting an exact floating-point boundary
        // (0.05 itself isn't bit-exact for doubles derived from fractions like 4/5 - 3/4).
        val older = listOf(
            session("Gym A", "2026-01-01T09:00:00", listOf(boulder(sent = true))),
            session("Gym A", "2026-01-02T09:00:00", listOf(boulder(sent = false))),
            session("Gym A", "2026-01-03T09:00:00", listOf(boulder(sent = false)))
        )
        val recent = listOf(
            session("Gym A", "2026-01-04T09:00:00", listOf(boulder(sent = true))),
            session("Gym A", "2026-01-05T09:00:00", listOf(boulder(sent = true))),
            session("Gym A", "2026-01-06T09:00:00", listOf(boulder(sent = false))),
            session("Gym A", "2026-01-07T09:00:00", listOf(boulder(sent = false))),
            session("Gym A", "2026-01-08T09:00:00", listOf(boulder(sent = false)))
        )

        assertEquals(GymTrend.STEADY, computeGymStats(older + recent).single().trend)
    }

    @Test
    fun `trend is up when the recent send rate is clearly higher than overall`() {
        val older = (1..3).map { session("Gym A", "2026-01-0${it}T09:00:00", listOf(boulder(sent = false))) }
        val recent = (4..8).map { session("Gym A", "2026-01-0${it}T09:00:00", listOf(boulder(sent = true))) }

        assertEquals(GymTrend.UP, computeGymStats(older + recent).single().trend)
    }

    @Test
    fun `trend is down when the recent send rate is clearly lower than overall`() {
        val older = (1..3).map { session("Gym A", "2026-01-0${it}T09:00:00", listOf(boulder(sent = true))) }
        val recent = (4..8).map { session("Gym A", "2026-01-0${it}T09:00:00", listOf(boulder(sent = false))) }

        assertEquals(GymTrend.DOWN, computeGymStats(older + recent).single().trend)
    }

    @Test
    fun `gyms are ordered by session count descending then alphabetically`() {
        val sessions =
            (1..3).map { session("Zeta", "2026-01-0${it}T09:00:00") } +
                    (1..3).map { session("Alpha", "2026-02-0${it}T09:00:00") } +
                    (1..5).map { session("Beta", "2026-03-0${it}T09:00:00") }

        val gymOrder = computeGymStats(sessions).map { it.gym }

        assertEquals(listOf("Beta", "Alpha", "Zeta"), gymOrder)
    }

    private fun session(location: String, startTime: String, boulders: List<Boulder> = emptyList()) = ClimbingSession(
        location = location,
        startTime = LocalDateTime.parse(startTime),
        boulders = boulders
    )

    private fun boulder(
        grade: Grade = Grade.None,
        sent: Boolean = false,
        flash: Boolean = false
    ) = Boulder(grade = grade, attempts = 1, sent = sent, flash = flash)
}
