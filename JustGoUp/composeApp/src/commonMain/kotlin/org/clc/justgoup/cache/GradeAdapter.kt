package org.clc.justgoup.cache

import org.clc.justgoup.boulder.FrenchGrade
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.VGrade

object GradeAdapter {
    // ---------- DECODE ----------
    fun decode(type: String, value: String?): Grade =
        when (type) {
            "NONE" -> Grade.None

            "V" -> Grade.VScale(decodeVGrade(value))

            "FRENCH" -> Grade.French(decodeFrenchGrade(value))

            else -> error("Unknown gradeType: $type")
        }

    // ---------- ENCODE ----------
    fun encode(grade: Grade): Pair<String, String?> =
        when (grade) {
            is Grade.None ->
                "NONE" to null

            is Grade.VScale ->
                "V" to encodeVGrade(grade.value)

            is Grade.French ->
                "FRENCH" to encodeFrenchGrade(grade.value)
        }

    // ---------- V GRADE ----------
    private fun encodeVGrade(v: VGrade): String =
        when {
            v.beginner -> "B"
            v.plus -> "${v.value}+"
            else -> v.value.toString()
        }

    private fun decodeVGrade(value: String?): VGrade {
        requireNotNull(value) { "V grade must not be null" }

        return when {
            value == "B" ->
                VGrade(value = 0, beginner = true)

            value.endsWith("+") ->
                VGrade(value = value.dropLast(1).toInt(), plus = true)

            else ->
                VGrade(value = value.toInt())
        }
    }

    // ---------- FRENCH GRADE ----------
    private fun encodeFrenchGrade(grade: FrenchGrade): String =
        buildString {
            append(grade.number)
            grade.letter?.let { append(it) }
            when (grade.modifier) {
                FrenchGrade.Modifier.Plus -> append("+")
                FrenchGrade.Modifier.Minus -> append("−")
                null -> Unit
            }
        }

    private fun decodeFrenchGrade(value: String?): FrenchGrade {
        requireNotNull(value) { "French grade must not be null" }

        val regex = Regex("""(\d)([abc]?)([+\-−]?)""")
        val match = regex.matchEntire(value)
            ?: error("Invalid French grade: $value")

        val (number, letter, modifier) = match.destructured

        return FrenchGrade(
            number = number.toInt(),
            letter = letter.takeIf { it.isNotEmpty() }?.first(),
            modifier = when (modifier) {
                "+" -> FrenchGrade.Modifier.Plus
                "-", "−" -> FrenchGrade.Modifier.Minus
                else -> null
            }
        )
    }
}
