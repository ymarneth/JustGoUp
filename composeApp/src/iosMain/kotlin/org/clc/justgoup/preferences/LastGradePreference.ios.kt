package org.clc.justgoup.preferences

import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.GradingSystem
import org.clc.justgoup.cache.GradeAdapter
import platform.Foundation.NSUserDefaults

class IOSLastGradePreference : LastGradePreference {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getLastGrade(system: GradingSystem): Grade? {
        val stored = defaults.stringForKey(keyFor(system)) ?: return null
        return runCatching { GradeAdapter.decode(typeFor(system), stored) }.getOrNull()
    }

    override fun setLastGrade(grade: Grade) {
        val (_, value) = GradeAdapter.encode(grade)
        if (value == null) return

        val system = grade.toGradingSystemOrNull() ?: return
        defaults.setObject(value, forKey = keyFor(system))
    }

    private fun keyFor(system: GradingSystem) = when (system) {
        GradingSystem.FRENCH -> KEY_LAST_FRENCH_GRADE
        GradingSystem.V_SCALE -> KEY_LAST_V_GRADE
    }

    private fun typeFor(system: GradingSystem) = when (system) {
        GradingSystem.FRENCH -> "FRENCH"
        GradingSystem.V_SCALE -> "V"
    }

    private companion object {
        const val KEY_LAST_FRENCH_GRADE = "last_french_grade"
        const val KEY_LAST_V_GRADE = "last_v_grade"
    }
}
