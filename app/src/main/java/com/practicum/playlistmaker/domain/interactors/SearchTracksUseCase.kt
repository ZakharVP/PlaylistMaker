package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.models.Track

class SearchTracksUseCase(private val repository: TrackRepositoryImpl) {
    fun execute(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}