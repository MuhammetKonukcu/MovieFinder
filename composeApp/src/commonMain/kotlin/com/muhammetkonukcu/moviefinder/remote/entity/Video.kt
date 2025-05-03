package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
)