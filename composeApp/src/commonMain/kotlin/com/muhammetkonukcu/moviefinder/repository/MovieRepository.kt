package com.muhammetkonukcu.moviefinder.repository

import androidx.paging.PagingData
import com.muhammetkonukcu.moviefinder.util.ContentType
import com.muhammetkonukcu.moviefinder.remote.entity.Movie
import com.muhammetkonukcu.moviefinder.remote.entity.MovieDetail
import com.muhammetkonukcu.moviefinder.remote.entity.Person
import com.muhammetkonukcu.moviefinder.remote.entity.Series
import com.muhammetkonukcu.moviefinder.remote.entity.SeriesDetail
import com.muhammetkonukcu.moviefinder.remote.entity.Video
import com.muhammetkonukcu.moviefinder.remote.response.ActorsResponse
import com.muhammetkonukcu.moviefinder.remote.response.CombinedCreditResponse
import com.muhammetkonukcu.moviefinder.remote.response.ImagesResponse
import com.muhammetkonukcu.moviefinder.remote.response.PersonImagesResponse
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun popularMoviesPager(): Flow<PagingData<Movie>>
    fun topRatedMoviesPager(): Flow<PagingData<Movie>>
    fun upcomingMoviesPager(): Flow<PagingData<Movie>>
    fun nowPlayingMoviesPager(): Flow<PagingData<Movie>>
    fun dayTrendingMoviesPager(): Flow<PagingData<Movie>>
    fun freeToWatchMoviesPager(): Flow<PagingData<Movie>>
    fun weekTrendingMoviesPager(): Flow<PagingData<Movie>>

    fun popularSeriesPager(): Flow<PagingData<Series>>
    fun topRatedSeriesPager(): Flow<PagingData<Series>>
    fun nowPlayingSeriesPager(): Flow<PagingData<Series>>
    fun dayTrendingSeriesPager(): Flow<PagingData<Series>>
    fun freeToWatchSeriesPager(): Flow<PagingData<Series>>
    fun weekTrendingSeriesPager(): Flow<PagingData<Series>>

    suspend fun getPerson(id: Int): Person
    suspend fun getDetails(id: Int): MovieDetail
    suspend fun getSeriesDetails(id: Int): SeriesDetail
    suspend fun getPersonImages(id: Int): PersonImagesResponse
    suspend fun getPersonCredits(id: Int): CombinedCreditResponse
    suspend fun getVideos(contentType: ContentType, id: Int): List<Video>
    suspend fun getImages(contentType: ContentType, id: Int): ImagesResponse
    suspend fun getActors(contentType: ContentType, id: Int): ActorsResponse

    fun searchPager(
        contentType: ContentType,
        genres: List<Int>,
        year: Int?,
        query: String,
        sortBy: String? = null,
    ): Flow<PagingData<Movie>>
}