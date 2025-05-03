package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Int,
    val gender: Int,
    val name: String,
    val adult: Boolean,
    val popularity: Double,
    val deathday: String? = null,
    val birthday: String? = null,
    val biography: String? = null,
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("profile_path") val profilePath: String? = null,
    @SerialName("place_of_birth") val placeOfBirth: String? = null,
    @SerialName("known_for_department") val department: String? = null
)