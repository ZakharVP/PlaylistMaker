package com.practicum.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.db.entities.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Важно: IGNORE!
    suspend fun insert(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: String): PlaylistTrackEntity?

    @Query("SELECT * FROM playlist_tracks")
    fun getAllPlaylistTracks(): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:trackIds)")
    fun getTracksByIds(trackIds: List<String>): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIdsSync(trackIds: List<String>): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: String)

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getTrackUsageCount(trackId: String): Int
}