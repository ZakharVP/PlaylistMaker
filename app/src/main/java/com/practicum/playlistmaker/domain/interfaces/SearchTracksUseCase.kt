package com.practicum.playlistmaker.domain.interfaces

import com.practicum.playlistmaker.domain.models.Track

interface SearchTracksUseCase {
    fun execute(query: String): List<Track>
}