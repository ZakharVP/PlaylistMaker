package com.practicum.playlistmaker.domain.repositories

import com.practicum.playlistmaker.domain.models.Track

interface TrackRepositoryInter {
    fun searchTracks(expression: String): List<Track>
}