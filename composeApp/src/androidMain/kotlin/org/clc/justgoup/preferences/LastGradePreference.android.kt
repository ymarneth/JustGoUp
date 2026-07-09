package org.clc.justgoup.preferences

import android.content.Context
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.GradingSystem
import org.clc.justgoup.cache.GradeAdapter

class AndroidLastGradePreference(context: Context) : LastGradePreference {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getLastGrade(system: GradingSystem): Grade? {
        val stored = prefs.getString(keyFor(system), null) ?: return null
        return runCatching { GradeAdapter.decode(typeFor(system), stored) }.getOrNull()
    }

    override fun setLastGrade(grade: Grade) {
        val (_, value) = GradeAdapter.encode(grade)
        if (value == null) return

        val system = grade.toGradingSystemOrNull() ?: return
        prefs.edit().putString(keyFor(system), value).apply()
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
        const val PREFS_NAME = "justgoup_prefs"
        const val KEY_LAST_FRENCH_GRADE = "last_french_grade"
        const val KEY_LAST_V_GRADE = "last_v_grade"
    }
}
