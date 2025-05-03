package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @SerialName("file_path") val filePath: String,
    @SerialName("aspect_ratio") val aspectRatio: Double
)