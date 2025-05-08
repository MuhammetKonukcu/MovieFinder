package com.muhammetkonukcu.moviefinder.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.cash.paging.PagingSource
import com.muhammetkonukcu.moviefinder.room.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(entity: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :id AND mediaType = :mediaType")
    suspend fun removeFromHistory(id: Int, mediaType: String)

    @Query("SELECT * FROM history ORDER BY createdAt DESC")
    fun getHistoryPaging(): PagingSource<Int, HistoryEntity>
}