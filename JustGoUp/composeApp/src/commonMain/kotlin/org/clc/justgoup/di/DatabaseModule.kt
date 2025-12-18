package org.clc.justgoup.di

import org.clc.justgoup.cache.Database
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.ClimbingSessionRepositoryImpl
import org.koin.dsl.module

val databaseModule = module {
    single { Database(get()) }
    single<ClimbingSessionRepository> { ClimbingSessionRepositoryImpl(get()) }
}
