package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.domain.interfaces.SearchTracksUseCase
import com.practicum.playlistmaker.domain.repositories.TrackRepositoryInter
import com.practicum.playlistmaker.domain.models.Track

class SearchTracksUseCaseImpl(private val repository: TrackRepositoryInter): SearchTracksUseCase {
    override fun execute(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}