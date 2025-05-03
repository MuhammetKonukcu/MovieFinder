package com.muhammetkonukcu.moviefinder.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val createdAt: Long,
    val mediaType: String,
    val voteAverage: Double,
    val title: String? = null,
    val adult: Boolean = false,
    val overview: String? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val genreIds: List<Int> = emptyList(),
)
