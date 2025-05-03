package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetail(
    val id: Int,
    val adult: Boolean,
    val budget: Long = 0,
    val runtime: Long = 0,
    val revenue: Long = 0,
    val title: String = "",
    val popularity: Double,
    val overview: String = "",
    val genres: List<Genre> = emptyList(),
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("vote_count") val voteCount: Long = 0,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("original_language") val originalLanguage: String = "",
    @SerialName("origin_country") val originCountry: List<String> = emptyList(),
    @SerialName("production_companies") val productionCompanies: List<Production> = emptyList(),
)