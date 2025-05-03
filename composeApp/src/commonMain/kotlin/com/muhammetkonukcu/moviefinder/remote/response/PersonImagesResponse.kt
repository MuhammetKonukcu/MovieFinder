package com.muhammetkonukcu.moviefinder.remote.response

import com.muhammetkonukcu.moviefinder.remote.entity.Image
import kotlinx.serialization.Serializable

@Serializable
data class PersonImagesResponse(
    val profiles: List<Image>
)