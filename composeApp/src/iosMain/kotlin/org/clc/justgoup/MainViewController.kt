package org.clc.justgoup

import androidx.compose.ui.window.ComposeUIViewController
import org.clc.justgoup.di.initKoin
import org.clc.justgoup.ui.App

fun MainViewController() = ComposeUIViewController({
    initKoin()
    App()
})
