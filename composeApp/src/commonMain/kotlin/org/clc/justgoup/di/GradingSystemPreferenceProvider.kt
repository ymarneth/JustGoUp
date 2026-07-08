package org.clc.justgoup.di

import org.clc.justgoup.preferences.GradingSystemPreference
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

fun provideGradingSystemPreference(): GradingSystemPreference =
    object : KoinComponent {
        fun preference(): GradingSystemPreference = get()
    }.preference()
