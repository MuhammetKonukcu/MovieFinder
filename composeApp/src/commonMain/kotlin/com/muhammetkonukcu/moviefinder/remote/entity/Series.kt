package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Series(
    val id: Int,
    val adult: Boolean,
    val name: String = "",
    val popularity: Double,
    val overview: String = "",
    @SerialName("vote_count") val voteCount: Long,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("original_name") val originalName: String = "",
    @SerialName("first_air_date") val firstAirDate: String = "",
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList(),
)