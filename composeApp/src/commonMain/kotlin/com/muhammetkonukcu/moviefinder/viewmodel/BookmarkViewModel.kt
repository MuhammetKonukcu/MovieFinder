package com.muhammetkonukcu.moviefinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.cachedIn
import com.muhammetkonukcu.moviefinder.room.repository.MovieLocalRepository

class BookmarkViewModel(
    localRepo: MovieLocalRepository
) : ViewModel() {
    val favoriteMoviesPaging =
        localRepo.getAllMovies().cachedIn(viewModelScope)
}