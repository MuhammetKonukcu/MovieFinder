package com.muhammetkonukcu.moviefinder.remote

import com.muhammetkonukcu.moviefinder.remote.entity.MovieDetail
import com.muhammetkonukcu.moviefinder.remote.entity.Person
import com.muhammetkonukcu.moviefinder.remote.entity.SeriesDetail
import com.muhammetkonukcu.moviefinder.remote.entity.Video
import com.muhammetkonukcu.moviefinder.remote.response.ActorsResponse
import com.muhammetkonukcu.moviefinder.remote.response.CombinedCreditResponse
import com.muhammetkonukcu.moviefinder.remote.response.ImagesResponse
import com.muhammetkonukcu.moviefinder.remote.response.MovieResponse
import com.muhammetkonukcu.moviefinder.remote.response.PersonImagesResponse
import com.muhammetkonukcu.moviefinder.remote.response.SeriesResponse
import com.muhammetkonukcu.moviefinder.remote.response.VideosResponse
import com.muhammetkonukcu.moviefinder.util.ContentType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.collections.component1
import kotlin.collections.component2

class TmdbClient(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val defaultLanguage: String,
    private val defaultRegion: String
) {
    private val baseUrl = "api.themoviedb.org"

    suspend fun getPopularMovies(
        page: Int = 1,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "movie/popular",
        page = page,
        language = language
    ).body()

    suspend fun getTopRatedMovies(
        page: Int = 1,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "movie/top_rated",
        page = page,
        language = language
    ).body()

    suspend fun getNowPlayingMovies(
        page: Int = 1,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "movie/now_playing",
        page = page,
        language = language
    ).body()

    suspend fun getUpcomingMovies(
        page: Int = 1,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "movie/upcoming",
        page = page,
        language = language
    ).body()

    suspend fun getDayTrendingMovies(
        page: Int = 1,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "trending/movie/day",
        page = page,
        language = language
    ).body()

    suspend fun getWeekTrendingMovies(
        page: Int = 1,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "trending/movie/week",
        page = page,
        language = language
    ).body()

    suspend fun getFreeToWatchMovies(
        page: Int = 1,
        region: String = defaultRegion,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "discover/movie",
        page = page,
        language = language,
        extraParams = mapOf(
            "with_watch_monetization_types" to "free",
            "watch_region" to region
        )
    ).body()

    suspend fun getPopularSeries(
        page: Int = 1,
        language: String = defaultLanguage
    ): SeriesResponse = sendRequest(
        path = "tv/popular",
        page = page,
        language = language
    ).body()

    suspend fun getTopRatedSeries(
        page: Int = 1,
        language: String = defaultLanguage
    ): SeriesResponse = sendRequest(
        path = "tv/top_rated",
        page = page,
        language = language
    ).body()

    suspend fun getNowPlayingSeries(
        page: Int = 1,
        language: String = defaultLanguage
    ): SeriesResponse = sendRequest(
        path = "tv/on_the_air",
        page = page,
        language = language
    ).body()

    suspend fun getDayTrendingSeries(
        page: Int = 1,
        language: String = defaultLanguage
    ): SeriesResponse = sendRequest(
        path = "tv/airing_today",
        page = page,
        language = language
    ).body()

    suspend fun getWeekTrendingSeries(
        page: Int = 1,
        language: String = defaultLanguage
    ): SeriesResponse = sendRequest(
        path = "trending/tv/week",
        page = page,
        language = language
    ).body()

    suspend fun getFreeToWatchSeries(
        page: Int = 1,
        region: String = defaultRegion,
        language: String = defaultLanguage
    ): SeriesResponse = sendRequest(
        path = "discover/tv",
        page = page,
        language = language,
        extraParams = mapOf(
            "with_watch_monetization_types" to "free",
            "watch_region" to region
        )
    ).body()

    suspend fun searchMovies(
        query: String,
        genres: List<Int>,
        year: Int?,
        page: Int = 1,
        sortBy: String? = null,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "search/movie",
        page = page,
        sortBy = sortBy,
        language = language,
        extraParams = buildMap {
            put("query", query)
            if (genres.isNotEmpty()) put("with_genres", genres.joinToString(","))
            year?.let { put("primary_release_year", it.toString()) }
        }
    ).body()

    suspend fun searchTv(
        query: String,
        genres: List<Int>,
        year: Int?,
        page: Int = 1,
        sortBy: String? = null,
        language: String = defaultLanguage
    ): MovieResponse = sendRequest(
        path = "search/tv",
        page = page,
        language = language,
        sortBy = sortBy,
        extraParams = buildMap {
            put("query", query)
            if (genres.isNotEmpty()) put("with_genres", genres.joinToString(","))
            year?.let { put("first_air_date_year", it.toString()) }
        }
    ).body()

    suspend fun discover(
        contentType: ContentType,
        genres: List<Int>,
        year: Int?,
        language: String = defaultLanguage,
        sortBy: String? = null,
        page: Int = 1
    ): MovieResponse = sendRequest(
        path = when (contentType) {
            ContentType.Movie -> "discover/movie"
            ContentType.Series -> "discover/tv"
        },
        page = page,
        language = language,
        sortBy = sortBy,
        extraParams = buildMap {
            if (genres.isNotEmpty()) put("with_genres", genres.joinToString(","))
            year?.let {
                put(
                    if (contentType == ContentType.Movie) "primary_release_year" else "first_air_date_year",
                    it.toString()
                )
            }
        }
    ).body()

    suspend fun getDetails(
        id: Int
    ): MovieDetail = sendRequest(path = "movie/$id", language = defaultLanguage).body()

    suspend fun getSeriesDetails(
        id: Int
    ): SeriesDetail = sendRequest(path = "tv/$id", language = defaultLanguage).body()

    suspend fun getVideos(
        contentType: ContentType,
        id: Int,
    ): List<Video> = coroutineScope {
        val path = if (contentType == ContentType.Movie) "movie" else "tv"
        val languages: List<String> = listOf(defaultLanguage, "en-US").distinct()
        languages.map { lang ->
            async {
                sendRequest(path = "$path/$id/videos", language = lang)
                    .body<VideosResponse>()
                    .results
            }
        }
            .awaitAll()
            .flatten()
            .distinctBy { it.id }
    }

    suspend fun getImages(
        contentType: ContentType,
        id: Int
    ): ImagesResponse {
        val path = if (contentType == ContentType.Movie) "movie" else "tv"
        return sendRequest(path = "$path/$id/images", language = "").body()
    }

    suspend fun getActors(
        contentType: ContentType,
        id: Int
    ): ActorsResponse {
        val path = if (contentType == ContentType.Movie) "movie" else "tv"
        return sendRequest(path = "$path/$id/credits", language = defaultLanguage).body()
    }

    suspend fun getPerson(id: Int): Person =
        sendRequest("person/$id", language = defaultLanguage).body()

    suspend fun getPersonCredits(id: Int): CombinedCreditResponse =
        sendRequest(path = "person/$id/combined_credits", language = defaultLanguage).body()

    suspend fun getPersonImages(id: Int): PersonImagesResponse =
        sendRequest(path = "person/$id/images", language = defaultLanguage).body()

    private suspend fun sendRequest(
        path: String,
        page: Int? = null,
        language: String,
        sortBy: String? = null,
        extraParams: Map<String, String> = emptyMap()
    ): HttpResponse {
        val call = httpClient.get {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
                encodedPath = "/3/$path"
                parameter("api_key", apiKey)
                parameter("language", language)
                sortBy?.let { parameter("sort_by", it) }
                page?.let { parameter("page", it.toString()) }
                extraParams.forEach { (k, v) -> parameter(k, v) }
            }
        }
        if (!call.status.isSuccess()) {
            throw RuntimeException("TMDB \"$path\" error: HTTP ${call.status.value}")
        }
        return call
    }
}