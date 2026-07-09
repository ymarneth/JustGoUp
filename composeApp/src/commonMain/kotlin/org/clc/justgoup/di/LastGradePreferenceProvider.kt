package org.clc.justgoup.di

import org.clc.justgoup.preferences.LastGradePreference
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

fun provideLastGradePreference(): LastGradePreference =
    object : KoinComponent {
        fun preference(): LastGradePreference = get()
    }.preference()
