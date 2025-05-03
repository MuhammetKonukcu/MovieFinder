package com.muhammetkonukcu.moviefinder.repository

import androidx.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.muhammetkonukcu.moviefinder.util.ContentType
import com.muhammetkonukcu.moviefinder.remote.TmdbClient
import com.muhammetkonukcu.moviefinder.remote.entity.Movie
import com.muhammetkonukcu.moviefinder.remote.entity.MovieDetail
import com.muhammetkonukcu.moviefinder.remote.entity.Person
import com.muhammetkonukcu.moviefinder.remote.entity.Series
import com.muhammetkonukcu.moviefinder.remote.entity.SeriesDetail
import com.muhammetkonukcu.moviefinder.remote.entity.Video
import com.muhammetkonukcu.moviefinder.remote.response.ActorsResponse
import com.muhammetkonukcu.moviefinder.remote.response.CombinedCreditResponse
import com.muhammetkonukcu.moviefinder.remote.response.ImagesResponse
import com.muhammetkonukcu.moviefinder.remote.response.MovieResponse
import com.muhammetkonukcu.moviefinder.remote.response.PersonImagesResponse
import com.muhammetkonukcu.moviefinder.remote.response.SeriesResponse
import com.muhammetkonukcu.moviefinder.source.MovieSource
import com.muhammetkonukcu.moviefinder.source.SearchSource
import com.muhammetkonukcu.moviefinder.source.SeriesSource
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl(
    private val tmdbClient: TmdbClient
) : MovieRepository {
    override fun popularMoviesPager(): Flow<PagingData<Movie>> =
        createMoviePager { tmdbClient.getPopularMovies(it) }.flow

    override fun topRatedMoviesPager(): Flow<PagingData<Movie>> =
        createMoviePager { tmdbClient.getTopRatedMovies(it) }.flow

    override fun nowPlayingMoviesPager(): Flow<PagingData<Movie>> =
        createMoviePager { tmdbClient.getNowPlayingMovies(it) }.flow

    override fun upcomingMoviesPager(): Flow<PagingData<Movie>> =
        createMoviePager { tmdbClient.getUpcomingMovies(it) }.flow

    override fun dayTrendingMoviesPager(): Flow<PagingData<Movie>> =
        createMoviePager { tmdbClient.getDayTrendingMovies(page = it) }.flow

    override fun weekTrendingMoviesPager(): Flow<PagingData<Movie>> =
        createMoviePager { tmdbClient.getWeekTrendingMovies(page = it) }.flow

    override fun freeToWatchMoviesPager(): Flow<PagingData<Movie>> =
        createMoviePager { tmdbClient.getFreeToWatchMovies( page = it) }.flow

    override fun popularSeriesPager(): Flow<PagingData<Series>> =
        createSeriesPager { tmdbClient.getPopularSeries(it) }.flow

    override fun topRatedSeriesPager(): Flow<PagingData<Series>> =
        createSeriesPager { tmdbClient.getTopRatedSeries(it) }.flow

    override fun nowPlayingSeriesPager(): Flow<PagingData<Series>> =
        createSeriesPager { tmdbClient.getNowPlayingSeries(it) }.flow

    override fun dayTrendingSeriesPager(): Flow<PagingData<Series>> =
        createSeriesPager { tmdbClient.getDayTrendingSeries(page = it) }.flow

    override fun weekTrendingSeriesPager(): Flow<PagingData<Series>> =
        createSeriesPager { tmdbClient.getWeekTrendingSeries(page = it) }.flow

    override fun freeToWatchSeriesPager(): Flow<PagingData<Series>> =
        createSeriesPager { tmdbClient.getFreeToWatchSeries(page = it) }.flow

    override suspend fun getDetails(id: Int): MovieDetail =
        tmdbClient.getDetails(id = id)

    override suspend fun getSeriesDetails(id: Int): SeriesDetail =
        tmdbClient.getSeriesDetails(id = id)

    override suspend fun getVideos(contentType: ContentType, id: Int): List<Video> =
        tmdbClient.getVideos(contentType = contentType, id = id)

    override suspend fun getImages(contentType: ContentType, id: Int): ImagesResponse =
        tmdbClient.getImages(contentType = contentType, id = id)

    override suspend fun getActors(contentType: ContentType, id: Int): ActorsResponse =
        tmdbClient.getActors(contentType = contentType, id = id)

    override suspend fun getPerson(id: Int): Person =
        tmdbClient.getPerson(id = id)

    override suspend fun getPersonCredits(id: Int): CombinedCreditResponse =
        tmdbClient.getPersonCredits(id = id)

    override suspend fun getPersonImages(id: Int): PersonImagesResponse =
        tmdbClient.getPersonImages(id = id)

    override fun searchPager(
        contentType: ContentType,
        genres: List<Int>,
        year: Int?,
        query: String,
        sortBy: String?
    ): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchSource(
                tmdbClient = tmdbClient,
                contentType = contentType,
                genres = genres,
                year = year,
                query = query,
                sortBy = sortBy
            )
        }
    ).flow

    private fun createMoviePager(loadPage: suspend (Int) -> MovieResponse) = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { MovieSource(loadPage) }
    )

    private fun createSeriesPager(loadPage: suspend (Int) -> SeriesResponse) = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { SeriesSource(loadPage) }
    )
}
