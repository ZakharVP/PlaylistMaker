package com.practicum.playlistmaker.playlist.sharing.data.models

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val coverUrl: String?,
    val tracks: List<Track>
)