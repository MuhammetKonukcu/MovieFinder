package com.muhammetkonukcu.moviefinder

import androidx.compose.ui.window.ComposeUIViewController
import com.muhammetkonukcu.moviefinder.di.initKoin
import com.muhammetkonukcu.moviefinder.screen.MainScreen

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    MainScreen()
}