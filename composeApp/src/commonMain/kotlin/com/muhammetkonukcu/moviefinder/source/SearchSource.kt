package com.muhammetkonukcu.moviefinder.source

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import com.muhammetkonukcu.moviefinder.util.ContentType
import com.muhammetkonukcu.moviefinder.remote.TmdbClient
import com.muhammetkonukcu.moviefinder.remote.entity.Movie

class SearchSource(
    private val tmdbClient: TmdbClient,
    private val contentType: ContentType,
    private val genres: List<Int>,
    private val year: Int?,
    private val query: String,
    private val sortBy: String?
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
            val response = if (query.isNotBlank()) {
                when (contentType) {
                    ContentType.Movie  -> tmdbClient.searchMovies(
                        query = query,
                        genres = genres,
                        year = year,
                        sortBy = sortBy,
                        page = page
                    )
                    ContentType.Series -> tmdbClient.searchTv(
                        query = query,
                        genres = genres,
                        year = year,
                        sortBy = sortBy,
                        page = page)
                }
            } else {
                tmdbClient.discover(
                    contentType = contentType,
                    genres = genres,
                    year = year,
                    sortBy = sortBy,
                    page = page
                )
            }
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
