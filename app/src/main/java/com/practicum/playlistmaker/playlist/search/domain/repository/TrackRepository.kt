package com.practicum.playlistmaker.playlist.search.domain.repository

import com.practicum.playlistmaker.playlist.sharing.data.models.Track

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>
}