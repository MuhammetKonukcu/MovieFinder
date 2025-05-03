package com.muhammetkonukcu.moviefinder.room.repository

import androidx.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.muhammetkonukcu.moviefinder.room.dao.MovieDao
import com.muhammetkonukcu.moviefinder.room.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

class MovieLocalRepositoryImpl(private val movieDao: MovieDao) : MovieLocalRepository {
    override suspend fun insertMovie(entity: MovieEntity) = movieDao.insertMovie(entity)

    override suspend fun isMovieFavorite(id: Int, mediaType: String): Boolean =
        movieDao.getMovie(id, mediaType)

    override suspend fun removeFavoriteMovies(id: Int, mediaType: String) =
        movieDao.removeFavoriteMovies(id, mediaType)

    override fun getAllMovies(pageSize: Int): Flow<PagingData<MovieEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { movieDao.getAllMoviesPaging() }
        ).flow
    }
}