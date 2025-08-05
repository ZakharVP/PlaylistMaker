package com.practicum.playlistmaker.playlist.player.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.player.domain.useCases.FavoritesUseCase
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private val _isFavorite = MutableStateFlow<Boolean>(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun checkFavorite(trackId: String) {
        viewModelScope.launch {
            _isFavorite.value = favoritesUseCase.isFavorite(trackId)
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            favoritesUseCase.toggleFavorite(track)
            _isFavorite.value = favoritesUseCase.isFavorite(track.trackId)
        }
    }
}