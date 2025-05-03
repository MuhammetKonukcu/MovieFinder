package com.muhammetkonukcu.moviefinder.remote.response

import com.muhammetkonukcu.moviefinder.remote.entity.Video
import kotlinx.serialization.Serializable

@Serializable
data class VideosResponse(
    val results: List<Video>
)