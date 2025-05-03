package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CombinedCrew(
    // movie only
    val title: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,

    // series only
    val name: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,

    // common fields
    val id: Int,
    val adult: Boolean,
    val popularity: Double,
    val job: String? = null,
    val overview: String? = null,
    val department: String? = null,
    @SerialName("vote_count") val voteCount: Long,
    @SerialName("media_type") val mediaType: String,
    @SerialName("genre_ids") val genreIds: List<Int>,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("credit_id") val creditId: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("original_language") val originalLanguage: String,
) {
    val displayTitle: String get() = title ?: name ?: ""
}