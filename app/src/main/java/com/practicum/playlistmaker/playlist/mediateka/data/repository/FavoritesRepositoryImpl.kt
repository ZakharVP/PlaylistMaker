package com.practicum.playlistmaker.playlist.mediateka.data.repository

import com.practicum.playlistmaker.db.data.TrackDao
import com.practicum.playlistmaker.db.data.entities.TrackEntity
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.FavoritesRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val trackDao: TrackDao
) : FavoritesRepository {

    override fun getAllFavorites(): Flow<List<Track>> {
        return trackDao.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun isFavorite(trackId: String): Boolean {
        return trackDao.getTrackById(trackId) != null
    }

    override suspend fun addToFavorites(track: Track) {
        trackDao.insert(track.toEntity())
    }

    override suspend fun removeFromFavorites(track: Track) {
        trackDao.delete(track.toEntity())
    }

    override suspend fun getAllFavoriteIds(): List<String> {
        return trackDao.getAllFavoriteIds()
    }

    private fun TrackEntity.toDomain() = Track(
        trackName = trackName,
        artistName = artistName,
        trackTimeMillisString = trackTimeMillisString,
        trackId = trackId,
        artworkUrl = artworkUrl,
        collectionName = collectionName,
        releaseYear = releaseYear,
        genre = genre,
        country = country,
        previewUrl = previewUrl,
        trackTime = trackTime
    )

    private fun Track.toEntity() = TrackEntity(
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