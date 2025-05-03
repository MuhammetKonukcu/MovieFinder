package com.muhammetkonukcu.moviefinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkonukcu.moviefinder.util.ContentType
import com.muhammetkonukcu.moviefinder.remote.entity.Actor
import com.muhammetkonukcu.moviefinder.remote.entity.Image
import com.muhammetkonukcu.moviefinder.remote.entity.SeriesDetail
import com.muhammetkonukcu.moviefinder.remote.entity.Video
import com.muhammetkonukcu.moviefinder.repository.MovieRepository
import com.muhammetkonukcu.moviefinder.room.entity.MovieEntity
import com.muhammetkonukcu.moviefinder.room.repository.MovieLocalRepository
import com.muhammetkonukcu.moviefinder.util.currentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeriesDetailViewModel(
    private val repo: MovieRepository,
    private val localRepo: MovieLocalRepository
) : ViewModel() {
    private val _seriesDetail = MutableStateFlow<SeriesDetail?>(null)
    val seriesDetail: StateFlow<SeriesDetail?> = _seriesDetail

    private val _videos = MutableStateFlow<List<Video>?>(null)
    val videos: StateFlow<List<Video>?> = _videos

    private val _images = MutableStateFlow<List<Image>?>(null)
    val images: StateFlow<List<Image>?> = _images

    private val _actors = MutableStateFlow<List<Actor>?>(null)
    val actors: StateFlow<List<Actor>?> = _actors

    private val _isMovieFavorite = MutableStateFlow<Boolean>(false)
    val isMovieFavorite: StateFlow<Boolean> = _isMovieFavorite

    fun loadAll(contentType: ContentType, id: Int) {
        viewModelScope.launch {
            try {
                _seriesDetail.value = repo.getSeriesDetails(id)
                _videos.value = repo.getVideos(contentType, id)
                _images.value = repo.getImages(contentType, id).backdrops
                _actors.value = repo.getActors(contentType, id).cast
            } catch (_: Exception) {
                // TODO: To be handle later
            }
        }
    }

    fun isMovieFavorite(id: Int) {
        viewModelScope.launch {
            _isMovieFavorite.value = localRepo.isMovieFavorite(id, "tv")
        }
    }

    fun addFavoriteMovie(series: SeriesDetail) {
        viewModelScope.launch {
            _isMovieFavorite.emit(true)
            val now: Long = currentTimeMillis()
            localRepo.insertMovie(
                MovieEntity(
                    id = series.id,
                    createdAt = now,
                    mediaType = "tv",
                    voteAverage = series.voteAverage,
                    title = series.name,
                    adult = series.adult,
                    overview = series.overview,
                    posterPath = series.posterPath,
                    releaseDate = series.firstAirDate,
                    genreIds = series.genres.map { it.id }
                )
            )
        }
    }

    fun removeFavoriteMovies(id: Int) {
        viewModelScope.launch {
            _isMovieFavorite.emit(false)
            localRepo.removeFavoriteMovies(id, "tv")
        }
    }
}
