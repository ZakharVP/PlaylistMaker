package com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        _playlists.value = playlistsRepository.getPlaylists()
    }

    fun createPlaylist(playlist: Playlist) {
        playlistsRepository.createPlaylist(playlist)
        loadPlaylists()
    }

    fun deletePlaylist(playlistId: String) {
        playlistsRepository.deletePlaylist(playlistId)
        loadPlaylists()
    }
}