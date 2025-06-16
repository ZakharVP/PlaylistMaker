package com.practicum.playlistmaker.playlist.player.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.playlist.player.domain.repository.PlayerRepository

class PlayerViewModelFactory(
    private val repository: PlayerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(repository) as T
    }
}