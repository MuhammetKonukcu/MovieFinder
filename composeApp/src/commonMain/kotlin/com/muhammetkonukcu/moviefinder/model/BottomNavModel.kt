package com.muhammetkonukcu.moviefinder.model

import androidx.compose.ui.graphics.painter.Painter

data class BottomNavModel(
    val title: String,
    val route: String,
    val hasNews: Boolean,
    val selectedIcon: Painter,
    val unselectedIcon: Painter
)
