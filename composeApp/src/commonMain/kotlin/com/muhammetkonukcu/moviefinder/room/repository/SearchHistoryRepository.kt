package com.muhammetkonukcu.moviefinder.room.repository

import app.cash.paging.PagingData
import com.muhammetkonukcu.moviefinder.room.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    suspend fun insertMovie(entity: HistoryEntity)
    suspend fun removeFromHistory(id: Int, mediaType: String)
    fun getHistory(pageSize: Int = 20): Flow<PagingData<HistoryEntity>>
}