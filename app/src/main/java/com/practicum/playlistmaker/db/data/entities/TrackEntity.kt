package com.practicum.playlistmaker.db.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class TrackEntity (
    @PrimaryKey
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillisString: String,
    val artworkUrl: String,
    val collectionName: String?,
    val releaseYear: String,
    val genre: String,
    val country: String,
    val previewUrl: String,
    val trackTime: String,
    val addedDate: Long = System.currentTimeMillis()
)
