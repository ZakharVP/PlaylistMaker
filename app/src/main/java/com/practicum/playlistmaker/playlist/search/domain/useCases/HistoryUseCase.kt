package com.practicum.playlistmaker.playlist.search.domain.useCases

import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.playlist.search.domain.repository.HistoryRepository
import kotlin.concurrent.thread

class HistoryUseCase(private val repository: HistoryRepository) {
    fun getHistory(callback: (List<Track>) -> Unit) {
        thread {
            val history = repository.getSearchHistory()
            callback(history)
        }
    }

    fun addToHistory(track: Track) {
        thread {
            repository.addToHistory(track)
        }
    }

    fun clearHistory() {
        thread {
            repository.clearSearchHistory()
        }
    }
}