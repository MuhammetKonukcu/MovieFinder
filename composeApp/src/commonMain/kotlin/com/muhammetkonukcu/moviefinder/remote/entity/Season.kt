package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val id: String,
    val name: String,
    val overview: String?,
    @SerialName("air_date") val airDate: String?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("season_number") val seasonNumber: Int?,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("episode_count") val episodeCount: String?,
)