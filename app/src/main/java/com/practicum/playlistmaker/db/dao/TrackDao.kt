package com.practicum.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.db.entities.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(track: TrackEntity)

    @Delete
    suspend fun delete(track: TrackEntity)

    @Query("Select * FROM favorite_tracks")
    fun getAllFavorites(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getAllFavoriteIds(): List<String>

    @Query("SELECT * FROM favorite_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: String): TrackEntity?
}