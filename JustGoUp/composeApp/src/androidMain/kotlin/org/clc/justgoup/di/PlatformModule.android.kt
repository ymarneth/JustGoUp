package org.clc.justgoup.di

import org.clc.justgoup.cache.AndroidDatabaseDriverFactory
import org.clc.justgoup.cache.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
}
