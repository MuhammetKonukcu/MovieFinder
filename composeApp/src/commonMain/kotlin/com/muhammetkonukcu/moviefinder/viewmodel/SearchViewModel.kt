package com.muhammetkonukcu.moviefinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import com.muhammetkonukcu.moviefinder.model.Filters
import com.muhammetkonukcu.moviefinder.remote.entity.Movie
import com.muhammetkonukcu.moviefinder.repository.MovieRepository
import com.muhammetkonukcu.moviefinder.room.entity.HistoryEntity
import com.muhammetkonukcu.moviefinder.room.repository.SearchHistoryRepository
import com.muhammetkonukcu.moviefinder.util.ContentType
import com.muhammetkonukcu.moviefinder.util.currentTimeMillis
import com.muhammetkonukcu.moviefinder.util.genreIds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.map

class SearchViewModel(
    private val repo: MovieRepository,
    private val historyRepo: SearchHistoryRepository
) : ViewModel() {
    val historyFlow = historyRepo
        .getHistory()
        .cachedIn(viewModelScope)

    fun addHistory(movie: Movie) {
        viewModelScope.launch {
            val now: Long = currentTimeMillis()
            historyRepo.insertMovie(
                entity = HistoryEntity(
                    id = movie.id,
                    createdAt = now,
                    mediaType = if (!movie.title.isNullOrBlank()) "movie" else "tv",
                    voteAverage = movie.voteAverage,
                    title = if (!movie.title.isNullOrBlank()) movie.title else movie.name,
                    adult = movie.adult,
                    overview = movie.overview,
                    posterPath = movie.posterPath,
                    releaseDate = if (!movie.title.isNullOrBlank()) movie.releaseDate else movie.firstAirDate,
                    genreIds = movie.genreIds
                )
            )
        }
    }

    fun removeFromHistory(id: Int, mediaType: String) {
        viewModelScope.launch {
            historyRepo.removeFromHistory(id = id, mediaType = mediaType)
        }
    }

    private val defaultGenreStates = genreIds.associateWith { false }
    val defaultFilters = Filters(
        contentTypes = mapOf("Movie" to true, "Series" to false),
        genres = defaultGenreStates,
        year = null,
        query = "",
        sortBy = null
    )

    private val _filters = MutableStateFlow(defaultFilters)
    val filters: StateFlow<Filters> = _filters.asStateFlow()

    val hasFilterChanges: StateFlow<Boolean> = _filters
        .map { current ->
            current != defaultFilters
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchFlow: Flow<PagingData<Movie>> = combine(
        _filters,
        hasFilterChanges
    ) { filters, hasChanges ->
        filters to hasChanges
    }.flatMapLatest { (f, hasChanges) ->
        if (!hasChanges) {
            flowOf(PagingData.empty())
        } else {
            repo.searchPager(
                contentType = when {
                    f.contentTypes["Series"] == true && f.contentTypes["Movie"] == true ->
                        ContentType.Movie

                    f.contentTypes["Series"] == true -> ContentType.Series
                    else -> ContentType.Movie
                },
                genres = f.genres.filterValues { it }.keys.toList(),
                year = f.year,
                query = f.query,
                sortBy = f.sortBy
            )
        }
    }.cachedIn(viewModelScope)

    fun updateFilters(new: Filters) {
        _filters.value = new
    }

    override fun onCleared() {
        super.onCleared()
        println("Cleared")
    }
}