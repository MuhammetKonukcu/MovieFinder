package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Actor(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String?,
    @SerialName("gender") val gender: Int,
    @SerialName("character") val characterName: String?,
    @SerialName("profile_path") val profilePath: String?,
    @SerialName("original_name") val originalName: String?,
)