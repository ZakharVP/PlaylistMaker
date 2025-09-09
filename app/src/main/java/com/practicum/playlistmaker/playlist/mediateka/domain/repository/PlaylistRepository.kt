package com.practicum.playlistmaker.playlist.mediateka.domain.repository

import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Long): Flow<Playlist?>
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)
    suspend fun refreshPlaylists()
    fun getTracksForPlaylist(trackIds: List<String>): Flow<List<Track>>
    suspend fun getTracksForPlaylistIds(trackIds: List<String>): List<Track>
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun getPlaylistByIdSync(id: Long): Playlist?
}