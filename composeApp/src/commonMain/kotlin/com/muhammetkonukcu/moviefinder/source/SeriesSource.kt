package com.muhammetkonukcu.moviefinder.source

import androidx.paging.PagingState
import app.cash.paging.PagingSource
import com.muhammetkonukcu.moviefinder.remote.entity.Series
import com.muhammetkonukcu.moviefinder.remote.response.SeriesResponse

class SeriesSource(
    private val loadPage: suspend (Int) -> SeriesResponse
) : PagingSource<Int, Series>() {
    override fun getRefreshKey(state: PagingState<Int, Series>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Series> {
        return try {
            val page = params.key ?: 1
            val response = loadPage(page)
            LoadResult.Page(
                data = response.results,
                prevKey = (page - 1).takeIf { it >= 1 },
                nextKey = (page + 1).takeIf { it <= response.totalPages }
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}