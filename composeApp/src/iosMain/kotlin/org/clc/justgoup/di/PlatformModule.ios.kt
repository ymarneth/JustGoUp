package org.clc.justgoup.di

import org.clc.justgoup.cache.DatabaseDriverFactory
import org.clc.justgoup.cache.IOSDatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
}
