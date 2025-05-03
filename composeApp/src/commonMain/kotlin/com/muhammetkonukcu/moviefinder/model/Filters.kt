package com.muhammetkonukcu.moviefinder.model

data class Filters(
    val year: Int?,
    val query: String,
    val sortBy: String? = null,
    val genres: Map<Int, Boolean>,
    val contentTypes: Map<String, Boolean>
)