package com.practicum.playlistmaker.domain.repositary

import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(query: String) : List<Track>
}