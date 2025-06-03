package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.interfaces.HistoryUseCase
import com.practicum.playlistmaker.domain.repositories.HistoryRepositoryInter

class HistoryUseCaseImpl(private val repository: HistoryRepositoryInter): HistoryUseCase {
    override fun getHistory(): List<Track> = repository.getSearchHistory()
    override fun addToHistory(track: Track) = repository.addSearchToHistory(track)
    override fun clearHistory() = repository.clearSearchHistory()
}