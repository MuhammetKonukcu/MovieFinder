package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeriesDetail(
    val id: Int,
    val type: String,
    val adult: Boolean,
    val runtime: Long = 0,
    val popularity: Double,
    val name: String? = null,
    val status: String? = null,
    val tagline: String? = null,
    val homepage: String? = null,
    val overview: String? = null,
    val genres: List<Genre> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val networks: List<Production>? = emptyList(),
    @SerialName("vote_count") val voteCount: Long = 0,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("in_production") val inProduction: Boolean,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("number_of_seasons") val numberOfSeasons: Int,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int,
    @SerialName("last_air_date") val lastAirDate: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("created_by") val createdBy: List<Creator> = emptyList(),
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("last_episode_to_air") val lastEpisodeToAir: Episode? = null,
    @SerialName("origin_country") val originCountry: List<String>? = emptyList(),
    @SerialName("production_companies") val productionCompanies: List<Production>? = emptyList(),
)