package com.muhammetkonukcu.moviefinder.remote.response

import com.muhammetkonukcu.moviefinder.remote.entity.Series
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeriesResponse(
    val page: Int,
    val results: List<Series>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)