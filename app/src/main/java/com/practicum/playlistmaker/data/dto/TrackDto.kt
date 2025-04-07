package com.practicum.playlistmaker.data.dto

data class TrackDto (

    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val trackId: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String

)