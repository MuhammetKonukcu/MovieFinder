package com.muhammetkonukcu.moviefinder.remote.response

import com.muhammetkonukcu.moviefinder.remote.entity.Actor
import kotlinx.serialization.Serializable

@Serializable
data class ActorsResponse(
    val cast: List<Actor>
)