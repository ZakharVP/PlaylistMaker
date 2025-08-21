package com.practicum.playlistmaker.playlist.mediateka.domain.repository

import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(): Flow<List<Track>>
    suspend fun isFavorite(trackId: String): Boolean
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    suspend fun getAllFavoriteIds(): List<String>
}