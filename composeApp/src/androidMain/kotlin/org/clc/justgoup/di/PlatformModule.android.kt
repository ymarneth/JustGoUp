package org.clc.justgoup.di

import org.clc.justgoup.cache.AndroidDatabaseDriverFactory
import org.clc.justgoup.cache.DatabaseDriverFactory
import org.clc.justgoup.preferences.AndroidGradingSystemPreference
import org.clc.justgoup.preferences.AndroidLastGradePreference
import org.clc.justgoup.preferences.GradingSystemPreference
import org.clc.justgoup.preferences.LastGradePreference
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
    single<GradingSystemPreference> { AndroidGradingSystemPreference(get()) }
    single<LastGradePreference> { AndroidLastGradePreference(get()) }
}
