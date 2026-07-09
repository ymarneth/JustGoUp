package org.clc.justgoup.di

import org.clc.justgoup.cache.DatabaseDriverFactory
import org.clc.justgoup.cache.IOSDatabaseDriverFactory
import org.clc.justgoup.preferences.GradingSystemPreference
import org.clc.justgoup.preferences.IOSGradingSystemPreference
import org.clc.justgoup.preferences.IOSLastGradePreference
import org.clc.justgoup.preferences.LastGradePreference
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
    single<GradingSystemPreference> { IOSGradingSystemPreference() }
    single<LastGradePreference> { IOSLastGradePreference() }
}
