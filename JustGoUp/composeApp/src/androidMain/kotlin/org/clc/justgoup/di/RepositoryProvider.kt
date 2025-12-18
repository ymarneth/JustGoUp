package org.clc.justgoup.di

import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

actual fun provideClimbingSessionRepository(): ClimbingSessionRepository {
    // Koin must be started in Application or MainActivity
    return object : KoinComponent {
        fun repo(): ClimbingSessionRepository = get()
    }.repo()
}
