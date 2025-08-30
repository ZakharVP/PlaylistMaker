package com.practicum.playlistmaker.db.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.db.dao.PlaylistDao
import com.practicum.playlistmaker.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.db.dao.TrackDao
import com.practicum.playlistmaker.db.entities.PlaylistEntity
import com.practicum.playlistmaker.db.entities.PlaylistTrackEntity
import com.practicum.playlistmaker.db.entities.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}