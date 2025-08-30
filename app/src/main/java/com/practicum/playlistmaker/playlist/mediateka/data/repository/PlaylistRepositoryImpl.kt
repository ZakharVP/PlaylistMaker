package com.practicum.playlistmaker.playlist.mediateka.data.repository

import com.google.gson.Gson
import com.practicum.playlistmaker.db.dao.PlaylistDao
import com.practicum.playlistmaker.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.db.entities.PlaylistEntity
import com.practicum.playlistmaker.db.entities.PlaylistTrackEntity
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        return playlistDao.insert(playlist.toEntity(gson))
    }

    private fun Playlist.toEntity(gson: Gson): PlaylistEntity {
        return PlaylistEntity(
            id = id,
            name = name,
            description = description,
            coverUri = coverUri,
            trackIds = gson.toJson(tracks.map { it.trackId }),
            tracksCount = tracksCount
        )
    }

    private fun PlaylistEntity.toDomain(gson: Gson): Playlist {
        val trackIdList: List<String> = try {
            gson.fromJson(trackIds, Array<String>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        return Playlist(
            id = id,
            name = name,
            description = description,
            coverUri = coverUri,
            tracks = emptyList(),
            tracksCount = tracksCount
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.update(playlist.toEntity(gson))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toDomain(gson)
                } catch (e: Exception) {
                    null // Пропускаем некорректные плейлисты
                }
            }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return try {
            playlistDao.getPlaylistById(id)?.toDomain(gson)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        // 1. Сохранить трек в таблицу треков плейлистов
        playlistTrackDao.insert(track.toPlaylistTrackEntity())

        // 2. Получить плейлист и обновить его
        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return

        val currentTrackIds = gson.fromJson(playlistEntity.trackIds, Array<String>::class.java)?.toList() ?: emptyList()
        if (currentTrackIds.contains(track.trackId)) return // Проверка на дубликат

        val updatedTrackIds = currentTrackIds + track.trackId
        val updatedEntity = playlistEntity.copy(
            trackIds = gson.toJson(updatedTrackIds),
            tracksCount = updatedTrackIds.size
        )

        playlistDao.update(updatedEntity)
    }

    private fun Track.toPlaylistTrackEntity(): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillisString = trackTimeMillisString,
            artworkUrl = artworkUrl, // проверьте правильное название поля!
            collectionName = collectionName,
            releaseYear = releaseYear.take(4),
            genre = genre,
            country = country,
            previewUrl = previewUrl,
            trackTime = trackTime,
            addedDate = System.currentTimeMillis()
        )
    }

}