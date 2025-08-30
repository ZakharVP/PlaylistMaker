package com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistRepository
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                playlistsRepository.getAllPlaylists().collect { playlistsList ->
                    _playlists.value = playlistsList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось загрузить плейлисты: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun refreshPlaylists() {
        loadPlaylists()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun getPlaylistCount(): Int {
        return _playlists.value?.size ?: 0
    }

    fun getPlaylistByPosition(position: Int): Playlist? {
        return _playlists.value?.getOrNull(position)
    }
}