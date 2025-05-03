package com.muhammetkonukcu.moviefinder.zoom

data class Settings(
    val zoomEnabled: Boolean = true,
    val enableOneFingerZoom: Boolean = true,
    val scrollGesturePropagation: ScrollGesturePropagation = ScrollGesturePropagation.ContentEdge,
    val initialScale: Float = 1f,
)