package org.clc.justgoup.preferences

import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.GradingSystem

interface LastGradePreference {
    fun getLastGrade(system: GradingSystem): Grade?
    fun setLastGrade(grade: Grade)
}

internal fun Grade.toGradingSystemOrNull(): GradingSystem? = when (this) {
    is Grade.French -> GradingSystem.FRENCH
    is Grade.VScale -> GradingSystem.V_SCALE
    is Grade.None -> null
}
