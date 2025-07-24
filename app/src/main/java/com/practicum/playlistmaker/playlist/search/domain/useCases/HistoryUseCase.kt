package com.practicum.playlistmaker.playlist.search.domain.useCases

import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.playlist.search.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class HistoryUseCase(private val repository: HistoryRepository) {
    suspend fun getHistory(): List<Track> {
        return withContext(Dispatchers.IO) {
            repository.getSearchHistory()
        }
    }

    suspend fun addToHistory(track: Track) {
        withContext(Dispatchers.IO) {
            repository.addToHistory(track)
        }
    }

    suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            repository.clearSearchHistory()
        }
    }
}