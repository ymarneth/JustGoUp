package org.clc.justgoup.preferences

import org.clc.justgoup.boulder.GradingSystem
import platform.Foundation.NSUserDefaults

class IOSGradingSystemPreference : GradingSystemPreference {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun get(): GradingSystem =
        defaults.stringForKey(KEY_GRADING_SYSTEM)
            ?.let { runCatching { GradingSystem.valueOf(it) }.getOrNull() }
            ?: GradingSystem.FRENCH

    override fun set(value: GradingSystem) {
        defaults.setObject(value.name, forKey = KEY_GRADING_SYSTEM)
    }

    private companion object {
        const val KEY_GRADING_SYSTEM = "last_grading_system"
    }
}
