package com.practicum.playlistmaker.playlist.sharing.data.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverUri: String?,
    val tracks: List<Track>,
    val tracksCount: Int
)