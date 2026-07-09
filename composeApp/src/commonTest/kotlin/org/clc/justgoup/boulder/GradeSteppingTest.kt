package org.clc.justgoup.boulder

import kotlin.test.Test
import kotlin.test.assertEquals

class GradeSteppingTest {

    @Test
    fun `french grade sequence spans 3a through 9c with no plus modifier`() {
        val sequence = frenchGradeSequence()

        assertEquals(FrenchGrade(3, 'a'), sequence.first())
        assertEquals(FrenchGrade(9, 'c'), sequence.last())
        assertEquals(21, sequence.size)
        assertEquals(emptyList(), sequence.filter { it.modifier != null })
    }

    @Test
    fun `french grade sequence orders letters within a number before rolling to the next number`() {
        val sequence = frenchGradeSequence()
        val sixes = sequence.filter { it.number == 6 }

        assertEquals(listOf('a', 'b', 'c'), sixes.map { it.letter })

        val lastSix = sequence.indexOf(FrenchGrade(6, 'c'))
        assertEquals(FrenchGrade(7, 'a'), sequence[lastSix + 1])
    }

    @Test
    fun `v-scale sequence starts with beginner then spans V0 through V10`() {
        val sequence = vGradeSequence()

        assertEquals(VGrade(value = 0, beginner = true), sequence.first())
        assertEquals(VGrade(value = 0), sequence[1])
        assertEquals(VGrade(value = 10), sequence.last())
        assertEquals(12, sequence.size)
        assertEquals(emptyList(), sequence.filter { it.plus })
    }
}
