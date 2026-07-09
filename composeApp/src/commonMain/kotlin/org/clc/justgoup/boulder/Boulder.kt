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

private val FRENCH_STEPS: List<Pair<Char, Boolean>> = listOf(
    'a' to false, 'a' to true,
    'b' to false, 'b' to true,
    'c' to false, 'c' to true
)
private const val FRENCH_MIN_NUMBER = 3
private const val FRENCH_MAX_NUMBER = 9

fun FrenchGrade.stepUp(): FrenchGrade? {
    val index = FRENCH_STEPS.indexOf((letter ?: 'a') to (modifier == FrenchGrade.Modifier.Plus)).coerceAtLeast(0)
    return when {
        index < FRENCH_STEPS.lastIndex -> {
            val (nextLetter, nextPlus) = FRENCH_STEPS[index + 1]
            copy(letter = nextLetter, modifier = if (nextPlus) FrenchGrade.Modifier.Plus else null)
        }

        number < FRENCH_MAX_NUMBER -> {
            val (nextLetter, nextPlus) = FRENCH_STEPS.first()
            FrenchGrade(number = number + 1, letter = nextLetter, modifier = if (nextPlus) FrenchGrade.Modifier.Plus else null)
        }

        else -> null
    }
}

fun FrenchGrade.stepDown(): FrenchGrade? {
    val index = FRENCH_STEPS.indexOf((letter ?: 'a') to (modifier == FrenchGrade.Modifier.Plus)).coerceAtLeast(0)
    return when {
        index > 0 -> {
            val (prevLetter, prevPlus) = FRENCH_STEPS[index - 1]
            copy(letter = prevLetter, modifier = if (prevPlus) FrenchGrade.Modifier.Plus else null)
        }

        number > FRENCH_MIN_NUMBER -> {
            val (prevLetter, prevPlus) = FRENCH_STEPS.last()
            FrenchGrade(number = number - 1, letter = prevLetter, modifier = if (prevPlus) FrenchGrade.Modifier.Plus else null)
        }

        else -> null
    }
}

private const val V_MIN = 0
private const val V_MAX = 10

fun VGrade.stepUp(): VGrade? = when {
    beginner -> copy(value = V_MIN, beginner = false)
    value < V_MAX -> copy(value = value + 1)
    else -> null
}

fun VGrade.stepDown(): VGrade? = when {
    beginner -> null
    value > V_MIN -> copy(value = value - 1)
    else -> copy(beginner = true)
}

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
