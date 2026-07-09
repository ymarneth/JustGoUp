package org.clc.justgoup.boulder

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
data class Boulder(
    val id: String = Uuid.random().toString(),
    val grade: Grade,
    val attempts: Int,
    val sent: Boolean = false,
    val flash: Boolean = false,
    val repeated: Boolean = false,
    val color: HoldColor? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
)

enum class GradingSystem { FRENCH, V_SCALE }

sealed class Grade {
    data object None : Grade()
    data class French(val value: FrenchGrade) : Grade()
    data class VScale(val value: VGrade) : Grade()
}

data class FrenchGrade(
    val number: Int,             // 3–9+
    val letter: Char? = null,    // a, b, c
    val modifier: Modifier? = null
) {
    enum class Modifier { Plus, Minus }

    override fun toString(): String {
        return buildString {
            append(number)
            if (letter != null) append(letter)
            if (modifier == Modifier.Plus) append("+")
            if (modifier == Modifier.Minus) append("−")
        }
    }
}

data class VGrade(
    val value: Int,          // 0–17 etc
    val beginner: Boolean = false, // VB or V0−
    val plus: Boolean = false      // unofficial V3+
) {
    override fun toString(): String {
        return when {
            beginner -> "VB"
            value == 0 && plus -> "V0+"
            plus -> "V$value+"
            else -> "V$value"
        }
    }
}

enum class HoldColor {
    RED, PINK, BLUE, GREEN, TEAL, YELLOW, ORANGE, PURPLE, BLACK, WHITE, GREY
}

private const val FRENCH_MIN_NUMBER = 3
private const val FRENCH_MAX_NUMBER = 9
private val FRENCH_LETTERS = listOf('a', 'b', 'c')

/**
 * Every French number+letter combination in order, e.g. 3a, 3b, 3c, 4a, ... 9c.
 * The `+` modifier is intentionally not part of this sequence -- it's a separate,
 * non-standard toggle applied independently of the base grade.
 */
fun frenchGradeSequence(): List<FrenchGrade> =
    (FRENCH_MIN_NUMBER..FRENCH_MAX_NUMBER).flatMap { number ->
        FRENCH_LETTERS.map { letter -> FrenchGrade(number = number, letter = letter) }
    }

private const val V_MIN = 0
private const val V_MAX = 10

/** VB, then V0..V10 in order. The `plus` flag is applied independently of this sequence. */
fun vGradeSequence(): List<VGrade> =
    listOf(VGrade(value = V_MIN, beginner = true)) + (V_MIN..V_MAX).map { VGrade(value = it) }

fun Grade.toDisplayString(): String = when (this) {
    Grade.None -> "-"
    is Grade.French -> this.value.toString()
    is Grade.VScale -> this.value.toString()
}

fun HoldColor.toColor(): Color = when (this) {
    HoldColor.RED -> Color(0xFFE53935)
    HoldColor.PINK -> Color(0xFFF06292)
    HoldColor.BLUE -> Color(0xFF1E88E5)
    HoldColor.GREEN -> Color(0xFF43A047)
    HoldColor.TEAL -> Color(0xFF009688)
    HoldColor.YELLOW -> Color(0xFFFDD835)
    HoldColor.ORANGE -> Color(0xFFFB8C00)
    HoldColor.PURPLE -> Color(0xFF8E24AA)
    HoldColor.BLACK -> Color(0xFF212121)
    HoldColor.WHITE -> Color(0xFFFAFAFA)
    HoldColor.GREY -> Color(0xFF9E9E9E)
}
