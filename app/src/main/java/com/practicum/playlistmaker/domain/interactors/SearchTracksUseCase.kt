package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.repositary.TrackRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchTracksUseCase(private val repository: TrackRepository) {
    fun execute(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}