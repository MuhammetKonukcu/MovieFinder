package com.muhammetkonukcu.moviefinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import com.muhammetkonukcu.moviefinder.remote.entity.Series
import com.muhammetkonukcu.moviefinder.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class SeriesViewModel(
    repo: MovieRepository
): ViewModel() {
    val popularSeries: Flow<PagingData<Series>> =
        repo.popularSeriesPager().cachedIn(viewModelScope)

    val topRatedSeries: Flow<PagingData<Series>> =
        repo.topRatedSeriesPager().cachedIn(viewModelScope)

    val nowPlayingSeries: Flow<PagingData<Series>> =
        repo.nowPlayingSeriesPager().cachedIn(viewModelScope)

    val dayTrendingSeries: Flow<PagingData<Series>> =
        repo.dayTrendingSeriesPager().cachedIn(viewModelScope)

    val weekTrendingSeries: Flow<PagingData<Series>> =
        repo.weekTrendingSeriesPager().cachedIn(viewModelScope)

    val freeToWatchSeries: Flow<PagingData<Series>> =
        repo.freeToWatchSeriesPager().cachedIn(viewModelScope)
}
