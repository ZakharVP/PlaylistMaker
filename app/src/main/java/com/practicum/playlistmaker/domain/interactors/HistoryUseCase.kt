package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repositary.HistoryRepository

class HistoryUseCase(private val repository: HistoryRepository) {
    fun getHistory(): List<Track> = repository.getSearchHistory()
    fun addToHistory(track: Track) = repository.addToHistory(track)
    fun clearHistory() = repository.clearSearchHistory()
}