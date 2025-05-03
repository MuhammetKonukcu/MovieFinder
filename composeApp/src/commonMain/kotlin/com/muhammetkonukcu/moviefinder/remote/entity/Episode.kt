package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Episode(
    val id: String,
    val name: String,
    val runtime: Int? = null,
    val overview: String? = null,
    @SerialName("vote_count") val voteCount: Long,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("air_date") val airDate: String? = null,
    @SerialName("still_path") val stillPath: String? = null,
    @SerialName("season_number") val seasonNumber: Int? = null,
    @SerialName("episode_type") val episodeType: String? = null,
    @SerialName("episode_number") val episodeNumber: String? = null,
)