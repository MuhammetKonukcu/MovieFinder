package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val id: Int,
    val name: String
)
