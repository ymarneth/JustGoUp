package org.clc.justgoup.preferences

import org.clc.justgoup.boulder.GradingSystem

interface GradingSystemPreference {
    fun get(): GradingSystem
    fun set(value: GradingSystem)
}
