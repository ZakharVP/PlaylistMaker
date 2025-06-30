package com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.FavoritesRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<Track>>()
    val favorites: LiveData<List<Track>> = _favorites

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        _favorites.value = favoritesRepository.getFavorites()
    }

    fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
        loadFavorites()
    }

    fun removeFromFavorites(trackId: String) {
        favoritesRepository.removeFromFavorites(trackId)
        loadFavorites()
    }
}