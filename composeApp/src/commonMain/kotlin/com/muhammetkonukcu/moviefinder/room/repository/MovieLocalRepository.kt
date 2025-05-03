package com.muhammetkonukcu.moviefinder.room.repository

import app.cash.paging.PagingData
import com.muhammetkonukcu.moviefinder.room.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

interface MovieLocalRepository {
    suspend fun insertMovie(entity: MovieEntity)
    suspend fun isMovieFavorite(id: Int, mediaType: String): Boolean
    suspend fun removeFavoriteMovies(id: Int, mediaType: String)
    fun getAllMovies(pageSize: Int = 20): Flow<PagingData<MovieEntity>>
}