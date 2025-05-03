package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Int,
    val adult: Boolean,
    val popularity: Double,
    val name: String? = null,
    val title: String? = null,
    val overview: String? = null,
    @SerialName("vote_count") val voteCount: Long,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList(),
)