package com.muhammetkonukcu.moviefinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import com.muhammetkonukcu.moviefinder.room.entity.MovieEntity
import com.muhammetkonukcu.moviefinder.room.repository.MovieLocalRepository
import kotlinx.coroutines.flow.Flow

class BookmarkViewModel(
    localRepo: MovieLocalRepository
) : ViewModel() {
    val favoriteMoviesPaging: Flow<PagingData<MovieEntity>> =
        localRepo.getAllMovies().cachedIn(viewModelScope)
}