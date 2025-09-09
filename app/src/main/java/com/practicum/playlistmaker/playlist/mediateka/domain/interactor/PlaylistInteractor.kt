package com.practicum.playlistmaker.playlist.mediateka.domain.interactor

import com.practicum.playlistmaker.playlist.mediateka.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PlaylistInteractor(
    private val repository: PlaylistRepository
) {

    suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long {
        val playlist = Playlist(
            id = 0,
            name = name,
            description = description,
            coverUri = coverPath,
            tracks = emptyList(),
            tracksCount = 0
        )
        return repository.createPlaylist(playlist)
    }

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    fun getPlaylistById(id: Long): Flow<Playlist?> {
        return repository.getPlaylistById(id)
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    suspend fun getPlaylistsCount(): Int {
        return repository.getAllPlaylists().first().size
    }

    suspend fun refreshPlaylists() {
        repository.refreshPlaylists()
    }

    suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        repository.addTrackToPlaylist(playlistId, track)
    }

    fun getTracksForPlaylist(trackIds: List<String>): Flow<List<Track>> {
        return repository.getTracksForPlaylist(trackIds)
    }

    suspend fun getPlaylistByIdSync(id: Long): Playlist? {
        return repository.getPlaylistById(id).first()
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        repository.removeTrackFromPlaylist(playlistId, trackId)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }


    suspend fun updatePlaylist(playlistId: Long, name: String, description: String, coverPath: String?) {
        // Получаем текущий плейлист
        val currentPlaylist = repository.getPlaylistByIdSync(playlistId)

        // Создаем обновленный плейлист
        val updatedPlaylist = currentPlaylist?.copy(
            name = name,
            description = description,
            coverUri = coverPath ?: currentPlaylist.coverUri
        )

        // Сохраняем изменения
        if (updatedPlaylist != null) {
            repository.updatePlaylist(updatedPlaylist)
        }
    }

}