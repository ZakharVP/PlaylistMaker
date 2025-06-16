package com.practicum.playlistmaker.playlist.search.data.dto

import com.practicum.playlistmaker.playlist.sharing.data.models.TrackDto

data class SongResponse(
    val resultCount: Int,
    val expression: String,
    val results: List<TrackDto>
) : Response()
