package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track

data class SongResponse(
        val resultCount: Int,
        val expression: String,
        val results: List<TrackDto>
    ) : Response()
