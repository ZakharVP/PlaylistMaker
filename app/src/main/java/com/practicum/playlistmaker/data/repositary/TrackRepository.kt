package com.practicum.playlistmaker.data.repositary

import com.practicum.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>
}