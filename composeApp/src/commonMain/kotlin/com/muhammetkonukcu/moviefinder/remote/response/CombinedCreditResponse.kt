package com.muhammetkonukcu.moviefinder.remote.response

import com.muhammetkonukcu.moviefinder.remote.entity.CombinedCast
import com.muhammetkonukcu.moviefinder.remote.entity.CombinedCrew
import kotlinx.serialization.Serializable

@Serializable
data class CombinedCreditResponse(
    val cast: List<CombinedCast>,
    val crew: List<CombinedCrew>,
)