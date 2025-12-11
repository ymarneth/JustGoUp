package org.clc.justgoup.boulder

data class Boulder(
    val id: String,
    val grade: Grade,
    val attempts: Int,
    val sent: Boolean,
    val flash: Boolean = false,
    val color: HoldColor? = null,
    val notes: String? = null
)

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
