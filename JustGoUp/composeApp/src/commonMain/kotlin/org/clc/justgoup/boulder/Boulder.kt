package org.clc.justgoup.boulder

import androidx.compose.ui.graphics.Color

data class Boulder(
    val id: String,
    val grade: Grade,
    val attempts: Int,
    val sent: Boolean,
    val flash: Boolean = false,
    val color: HoldColor? = null,
    val notes: String? = null
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
