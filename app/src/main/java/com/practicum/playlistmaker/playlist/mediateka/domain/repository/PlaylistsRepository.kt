package com.practicum.playlistmaker.playlist.mediateka.domain.repository

import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist

interface PlaylistsRepository {
    fun getPlaylists(): List<Playlist>
    fun createPlaylist(playlist: Playlist)
    fun deletePlaylist(playlistId: String)
}