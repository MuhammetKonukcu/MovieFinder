package com.muhammetkonukcu.moviefinder.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.cash.paging.PagingSource
import com.muhammetkonukcu.moviefinder.room.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(entity: MovieEntity)

    @Query("""
        SELECT EXISTS(
            SELECT 1 
            FROM movies 
            WHERE id = :id AND mediaType = :mediaType
        )
    """)
    suspend fun getMovie(id: Int, mediaType: String): Boolean

    @Query("DELETE FROM movies WHERE id = :id AND mediaType = :mediaType")
    suspend fun removeFavoriteMovies(id: Int, mediaType: String)

    @Query("SELECT * FROM movies ORDER BY createdAt DESC")
    fun getAllMoviesPaging(): PagingSource<Int, MovieEntity>
}