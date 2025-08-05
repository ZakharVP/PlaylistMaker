package com.practicum.playlistmaker.playlist.mediateka.data.repository

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist

class PlaylistsRepositoryImpl(
    private val context: Context,
    private val gson: Gson
) : PlaylistsRepository {

    override fun getPlaylists(): List<Playlist> { return emptyList() }
    override fun createPlaylist(playlist: Playlist) {  }
    override fun deletePlaylist(playlistId: String) {  }
}