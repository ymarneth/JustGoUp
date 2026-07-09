package org.clc.justgoup.boulder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GradeSteppingTest {

    @Test
    fun `french grade steps up through letter and plus before rolling to the next number`() {
        val grade = FrenchGrade(number = 6, letter = 'a', modifier = null)

        assertEquals(FrenchGrade(6, 'a', FrenchGrade.Modifier.Plus), grade.stepUp())
        assertEquals(FrenchGrade(6, 'b', null), FrenchGrade(6, 'a', FrenchGrade.Modifier.Plus).stepUp())
        assertEquals(FrenchGrade(7, 'a', null), FrenchGrade(6, 'c', FrenchGrade.Modifier.Plus).stepUp())
    }

    @Test
    fun `french grade steps down through letter and plus before rolling to the previous number`() {
        assertEquals(FrenchGrade(6, 'a', null), FrenchGrade(6, 'a', FrenchGrade.Modifier.Plus).stepDown())
        assertEquals(FrenchGrade(6, 'a', FrenchGrade.Modifier.Plus), FrenchGrade(6, 'b', null).stepDown())
        assertEquals(FrenchGrade(5, 'c', FrenchGrade.Modifier.Plus), FrenchGrade(6, 'a', null).stepDown())
    }

    @Test
    fun `french grade has no step up beyond the top of the supported range`() {
        val top = FrenchGrade(number = 9, letter = 'c', modifier = FrenchGrade.Modifier.Plus)
        assertNull(top.stepUp())
    }

    @Test
    fun `french grade has no step down below the bottom of the supported range`() {
        val bottom = FrenchGrade(number = 3, letter = 'a', modifier = null)
        assertNull(bottom.stepDown())
    }

    @Test
    fun `french grade with a null letter is normalized to a for stepping`() {
        val grade = FrenchGrade(number = 6, letter = null, modifier = null)
        assertEquals(FrenchGrade(6, 'a', FrenchGrade.Modifier.Plus), grade.stepUp())
    }

    @Test
    fun `v-scale steps up and down by one preserving the plus flag`() {
        val grade = VGrade(value = 5, plus = true)
        assertEquals(VGrade(value = 6, plus = true), grade.stepUp())
        assertEquals(VGrade(value = 4, plus = true), grade.stepDown())
    }

    @Test
    fun `v-scale has no step up beyond the top of the supported range`() {
        assertNull(VGrade(value = 10).stepUp())
    }

    @Test
    fun `v-scale steps down from V0 to beginner and has no step below that`() {
        val v0 = VGrade(value = 0)
        val beginner = v0.stepDown()

        assertEquals(VGrade(value = 0, beginner = true), beginner)
        assertNull(beginner?.stepDown())
    }

    @Test
    fun `v-scale steps up from beginner to V0`() {
        val beginner = VGrade(value = 0, beginner = true)
        assertEquals(VGrade(value = 0, beginner = false), beginner.stepUp())
    }
}
