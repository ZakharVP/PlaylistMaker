package com.practicum.playlistmaker.playlist.mediateka.domain.repository

import com.practicum.playlistmaker.playlist.sharing.data.models.Track

interface FavoritesRepository {
    fun getFavorites(): List<Track>
    fun addToFavorites(track: Track)
    fun removeFromFavorites(trackId: String)
}