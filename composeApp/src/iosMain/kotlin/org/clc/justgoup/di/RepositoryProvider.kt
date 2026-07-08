package org.clc.justgoup.di


import org.clc.justgoup.cache.Database
import org.clc.justgoup.cache.IOSDatabaseDriverFactory
import org.clc.justgoup.climbingSession.ClimbingSessionRepository

actual fun provideClimbingSessionRepository(): ClimbingSessionRepository {
    val database = Database(IOSDatabaseDriverFactory())
    return org.clc.justgoup.climbingSession.ClimbingSessionRepositoryImpl(database)
}
