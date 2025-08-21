package com.practicum.playlistmaker.playlist.player.domain.useCases

import com.practicum.playlistmaker.playlist.mediateka.domain.repository.FavoritesRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class FavoritesUseCase(
    private val repository: FavoritesRepository
) {
    suspend fun toggleFavorite(track: Track) {
        if (repository.isFavorite(track.trackId)) {
            repository.removeFromFavorites(track)
        } else {
            repository.addToFavorites(track)
        }
    }

    suspend fun isFavorite(trackId: String): Boolean {
        return repository.isFavorite(trackId)
    }
}
