package com.muhammetkonukcu.moviefinder.remote.response

import com.muhammetkonukcu.moviefinder.remote.entity.Image
import kotlinx.serialization.Serializable

@Serializable
data class ImagesResponse(
   val backdrops: List<Image>
)