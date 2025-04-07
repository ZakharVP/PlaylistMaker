package com.practicum.playlistmaker

import com.practicum.playlistmaker.domain.Track

data class SongResponse(
        val resultCount: Int,
        val results: List<Track>
    )
