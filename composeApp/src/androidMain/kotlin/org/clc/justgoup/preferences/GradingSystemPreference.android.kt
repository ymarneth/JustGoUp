package org.clc.justgoup.preferences

import android.content.Context
import org.clc.justgoup.boulder.GradingSystem

class AndroidGradingSystemPreference(context: Context) : GradingSystemPreference {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun get(): GradingSystem =
        prefs.getString(KEY_GRADING_SYSTEM, null)
            ?.let { runCatching { GradingSystem.valueOf(it) }.getOrNull() }
            ?: GradingSystem.FRENCH

    override fun set(value: GradingSystem) {
        prefs.edit().putString(KEY_GRADING_SYSTEM, value.name).apply()
    }

    private companion object {
        const val PREFS_NAME = "justgoup_prefs"
        const val KEY_GRADING_SYSTEM = "last_grading_system"
    }
}
