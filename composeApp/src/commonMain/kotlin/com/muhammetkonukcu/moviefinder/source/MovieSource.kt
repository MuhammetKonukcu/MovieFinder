package com.muhammetkonukcu.moviefinder.source

import androidx.paging.PagingState
import app.cash.paging.PagingSource
import com.muhammetkonukcu.moviefinder.remote.entity.Movie
import com.muhammetkonukcu.moviefinder.remote.response.MovieResponse

class MovieSource(
    private val loadPage: suspend (Int) -> MovieResponse
) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.run {
                prevKey?.plus(1) ?: nextKey?.minus(1)
            }
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
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