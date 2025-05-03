package com.muhammetkonukcu.moviefinder.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Creator(
    val id: Int,
    val gender: Int,
    val name: String,
    @SerialName("credit_id") val creditId: String?,
    @SerialName("profile_path") val profilePath: String?,
    @SerialName("original_name") val originalName: String?,
)