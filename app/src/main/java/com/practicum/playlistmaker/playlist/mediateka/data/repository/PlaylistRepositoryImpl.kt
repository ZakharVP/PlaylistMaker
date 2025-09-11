package com.practicum.playlistmaker.playlist.mediateka.data.repository

import com.google.gson.Gson
import com.practicum.playlistmaker.db.dao.PlaylistDao
import com.practicum.playlistmaker.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.db.entities.PlaylistEntity
import com.practicum.playlistmaker.db.entities.PlaylistTrackEntity
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val gson: Gson
) : PlaylistRepository {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshTrigger.emit(Unit)
        }
    }

    // Добавляем метод удаления плейлиста
    override suspend fun deletePlaylist(playlistId: Long) {
        // 1. Получаем плейлист для получения списка треков
        val playlistEntity = playlistDao.getPlaylistByIdSync(playlistId) ?: return

        // 2. Получаем список треков этого плейлиста
        val trackIds = try {
            gson.fromJson(playlistEntity.trackIds, Array<String>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        // 3. Удаляем сам плейлист
        playlistDao.deletePlaylist(playlistId)

        // 4. Очищаем неиспользуемые треки
        cleanupUnusedTracks(trackIds)

        // 5. Обновляем список плейлистов
        refreshPlaylists()
    }

    // Метод для очистки неиспользуемых треков
    private suspend fun cleanupUnusedTracks(trackIds: List<String>) {
        for (trackId in trackIds) {
            // Проверяем, используется ли трек в других плейлистах
            val isTrackUsed = isTrackUsedInOtherPlaylists(trackId)
            if (!isTrackUsed) {
                // Если трек больше нигде не используется - удаляем его
                playlistTrackDao.deleteTrack(trackId)
            }
        }
    }

    // Проверяет, используется ли трек в других плейлистах
    private suspend fun isTrackUsedInOtherPlaylists(trackId: String): Boolean {
        val allPlaylists = playlistDao.getAllPlaylists().first()
        return allPlaylists.any { playlistEntity ->
            try {
                val playlistTrackIds = gson.fromJson(playlistEntity.trackIds, Array<String>::class.java)
                    ?.toList() ?: emptyList()
                playlistTrackIds.contains(trackId)
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return refreshTrigger.flatMapLatest {
            playlistDao.getAllPlaylists().map { entities ->
                entities.mapNotNull { entity ->
                    try {
                        entity.toDomain(gson)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }

    override fun getPlaylistById(id: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(id).map { entity ->
            entity?.toDomain(gson)
        }
    }

    override fun getTracksForPlaylist(trackIds: List<String>): Flow<List<Track>> {
        return if (trackIds.isEmpty()) {
            flowOf(emptyList())
        } else {
            playlistTrackDao.getTracksByIds(trackIds).map { entities ->
                val trackMap = entities.associateBy { it.trackId }
                trackIds.mapNotNull { trackId -> trackMap[trackId]?.toDomain() }
            }
        }
    }

    override suspend fun refreshPlaylists() {
        refreshTrigger.emit(Unit)
    }

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val id = playlistDao.insert(playlist.toEntity(gson))
        refreshPlaylists()
        return id
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

    override suspend fun getTracksForPlaylistIds(trackIds: List<String>): List<Track> {
        return if (trackIds.isEmpty()) {
            emptyList()
        } else {
            val entities = playlistTrackDao.getTracksByIdsSync(trackIds)
            val trackMap = entities.associateBy { it.trackId }
            trackIds.mapNotNull { trackId -> trackMap[trackId]?.toDomain() }
        }
    }

    private suspend fun PlaylistEntity.toDomain(gson: Gson): Playlist {
        val trackIdList: List<String> = try {
            gson.fromJson(trackIds, Array<String>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val tracks = getTracksForPlaylistIds(trackIdList)

        return Playlist(
            id = id,
            name = name,
            description = description,
            coverUri = coverUri,
            tracks = tracks,
            tracksCount = tracks.size
        )
    }

    private fun PlaylistTrackEntity.toDomain(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillisString = trackTimeMillisString,
            artworkUrl = artworkUrl,
            collectionName = collectionName,
            releaseYear = releaseYear,
            genre = genre,
            country = country,
            previewUrl = previewUrl,
            trackTime = trackTime
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.update(playlist.toEntity(gson))
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        // 1. Сохранить трек в таблицу треков плейлистов
        playlistTrackDao.insert(track.toPlaylistTrackEntity())

        // 2. Получить плейлист и обновить его
        val playlistEntity = playlistDao.getPlaylistByIdSync(playlistId) ?: return

        val currentTrackIds = gson.fromJson(playlistEntity.trackIds, Array<String>::class.java)?.toList() ?: emptyList()
        if (currentTrackIds.contains(track.trackId)) return // Проверка на дубликат

        val updatedTrackIds = currentTrackIds + track.trackId
        val updatedEntity = playlistEntity.copy(
            trackIds = gson.toJson(updatedTrackIds),
            tracksCount = updatedTrackIds.size
        )

        playlistDao.update(updatedEntity)
        refreshPlaylists()
    }

    private fun Track.toPlaylistTrackEntity(): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillisString = trackTimeMillisString,
            artworkUrl = artworkUrl,
            collectionName = collectionName,
            releaseYear = releaseYear,
            genre = genre,
            country = country,
            previewUrl = previewUrl,
            trackTime = trackTime,
            addedDate = System.currentTimeMillis()
        )
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        // 1. Получаем плейлист
        val playlistEntity = playlistDao.getPlaylistByIdSync(playlistId) ?: return

        // 2. Удаляем trackId из списка
        val currentTrackIds = gson.fromJson(playlistEntity.trackIds, Array<String>::class.java)
            ?.toList() ?: emptyList()

        val updatedTrackIds = currentTrackIds.filter { it != trackId }
        val updatedEntity = playlistEntity.copy(
            trackIds = gson.toJson(updatedTrackIds),
            tracksCount = updatedTrackIds.size
        )

        // 3. Обновляем плейлист
        playlistDao.update(updatedEntity)

        // 4. Проверяем, используется ли трек в других плейлистах
        if (!isTrackUsedInOtherPlaylists(trackId)) {
            playlistTrackDao.deleteTrack(trackId)
        }

        refreshPlaylists()
    }

    override suspend fun getPlaylistByIdSync(id: Long): Playlist? {
        return playlistDao.getPlaylistByIdSync(id)?.let { entity ->

            val trackIdList: List<String> = try {
                gson.fromJson(entity.trackIds, Array<String>::class.java)?.toList() ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val tracks = getTracksForPlaylistIds(trackIdList)

            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                coverUri = entity.coverUri,
                tracks = tracks,
                tracksCount = tracks.size
            )
        }
    }

    private suspend fun PlaylistEntity.toPlaylist(): Playlist {
        val trackIdList: List<String> = try {
            gson.fromJson(trackIds, Array<String>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val tracks = getTracksForPlaylistIds(trackIdList)

        return Playlist(
            id = id,
            name = name,
            description = description,
            coverUri = coverUri,
            tracks = tracks,
            tracksCount = tracks.size
        )
    }
}