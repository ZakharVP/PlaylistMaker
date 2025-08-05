package com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.FavoritesRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    val favorites: LiveData<List<Track>> = favoritesRepository.getAllFavorites().asLiveData()

    fun addToFavorites(track: Track) {
        viewModelScope.launch {
            favoritesRepository.addToFavorites(track)
        }
    }

    fun removeFromFavorites(track: Track) {
        viewModelScope.launch {
            favoritesRepository.removeFromFavorites(track)
        }
    }
}