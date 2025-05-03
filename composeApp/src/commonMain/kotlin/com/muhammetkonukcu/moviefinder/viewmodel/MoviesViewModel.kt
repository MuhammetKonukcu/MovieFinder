package com.muhammetkonukcu.moviefinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import com.muhammetkonukcu.moviefinder.remote.entity.Movie
import com.muhammetkonukcu.moviefinder.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class MoviesViewModel(
    repo: MovieRepository
) : ViewModel() {
    val popularMovies: Flow<PagingData<Movie>> =
        repo.popularMoviesPager().cachedIn(viewModelScope)

    val topRatedMovies: Flow<PagingData<Movie>> =
        repo.topRatedMoviesPager().cachedIn(viewModelScope)

    val nowPlayingMovies: Flow<PagingData<Movie>> =
        repo.nowPlayingMoviesPager().cachedIn(viewModelScope)

    val upcomingMovies: Flow<PagingData<Movie>> =
        repo.upcomingMoviesPager().cachedIn(viewModelScope)

    val dayTrendingMovies: Flow<PagingData<Movie>> =
        repo.dayTrendingMoviesPager().cachedIn(viewModelScope)

    val weekTrendingMovies: Flow<PagingData<Movie>> =
        repo.weekTrendingMoviesPager().cachedIn(viewModelScope)

    val freeToWatchMovies: Flow<PagingData<Movie>> =
        repo.freeToWatchMoviesPager().cachedIn(viewModelScope)
}
