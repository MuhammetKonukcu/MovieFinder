package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CombinedCast(
    // movie only
    val title: String? = null,
    @SerialName("release_date") val movieReleaseDate: String? = null,
    @SerialName("original_title") val movieOriginalTitle: String? = null,

    // series only
    val name: String? = null,
    @SerialName("original_name") val tvOriginalName: String? = null,
    @SerialName("first_air_date") val tvFirstAirDate: String? = null,

    // common fields
    val id: Int,
    val adult: Boolean,
    val popularity: Double,
    val order: Int?  = null,
    val overview: String? = null,
    val character: String? = null,
    @SerialName("vote_count") val voteCount: Long,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("credit_id") val creditId: String? = null,
    @SerialName("media_type") val mediaType: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList()
) {
    val displayTitle: String get() = title ?: name ?: ""
}