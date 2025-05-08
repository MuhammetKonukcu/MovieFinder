package com.muhammetkonukcu.moviefinder.room.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import app.cash.paging.PagingData
import com.muhammetkonukcu.moviefinder.room.dao.HistoryDao
import com.muhammetkonukcu.moviefinder.room.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepositoryImpl(private val historyDao: HistoryDao) : SearchHistoryRepository {
    override suspend fun insertMovie(entity: HistoryEntity) = historyDao.insertMovie(entity)

    override suspend fun removeFromHistory(id: Int, mediaType: String) =
        historyDao.removeFromHistory(id, mediaType)

    override fun getHistory(pageSize: Int): Flow<PagingData<HistoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { historyDao.getHistoryPaging() }
        ).flow
    }
}