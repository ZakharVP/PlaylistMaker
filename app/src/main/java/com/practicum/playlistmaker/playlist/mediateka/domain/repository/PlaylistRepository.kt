package com.practicum.playlistmaker.playlist.mediateka.domain.repository

import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)
}