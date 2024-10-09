package com.practicum.playlistmaker

data class SongResponse(
        val resultCount: Int,
        val results: List<Track>
    )
