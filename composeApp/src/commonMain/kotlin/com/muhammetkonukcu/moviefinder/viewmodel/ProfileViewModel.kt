package com.muhammetkonukcu.moviefinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkonukcu.moviefinder.remote.entity.CombinedCast
import com.muhammetkonukcu.moviefinder.remote.entity.CombinedCrew
import com.muhammetkonukcu.moviefinder.remote.entity.Image
import com.muhammetkonukcu.moviefinder.remote.entity.Person
import com.muhammetkonukcu.moviefinder.remote.response.CombinedCreditResponse
import com.muhammetkonukcu.moviefinder.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: MovieRepository
) : ViewModel() {
    private val _personDetail = MutableStateFlow<Person?>(null)
    val personDetail: StateFlow<Person?> = _personDetail

    private val _combinedCredits = MutableStateFlow<CombinedCreditResponse?>(null)

    val personCast: StateFlow<List<CombinedCast>?> = _combinedCredits
        .map { it?.cast }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val personCrew: StateFlow<List<CombinedCrew>?> = _combinedCredits
        .map { it?.crew }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _personImages = MutableStateFlow<List<Image>?>(null)
    val personImages: StateFlow<List<Image>?> = _personImages


    fun loadProfile(itemId: Int) {
        viewModelScope.launch {
            try {
                _personDetail.value = repo.getPerson(itemId)
                _combinedCredits.value = repo.getPersonCredits(itemId)
                _personImages.value = repo.getPersonImages(itemId).profiles
            } catch (_: Exception) {
                //TODO: To be handle later
            }
        }
    }
}