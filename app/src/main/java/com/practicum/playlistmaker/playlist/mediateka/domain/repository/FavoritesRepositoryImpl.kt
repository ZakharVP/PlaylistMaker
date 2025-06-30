package com.practicum.playlistmaker.playlist.mediateka.domain.repository

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class FavoritesRepositoryImpl(
    private val context: Context,
    private val gson: Gson
) : FavoritesRepository {

    override fun getFavorites(): List<Track> { return emptyList() }
    override fun addToFavorites(track: Track) { }
    override fun removeFromFavorites(trackId: String) { }
}