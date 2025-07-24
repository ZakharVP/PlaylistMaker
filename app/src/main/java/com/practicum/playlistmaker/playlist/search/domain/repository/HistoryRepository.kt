package com.practicum.playlistmaker.playlist.search.domain.repository

import com.practicum.playlistmaker.playlist.sharing.data.models.Track

interface HistoryRepository {
    suspend fun getSearchHistory(): List<Track>
    suspend fun addToHistory(track: Track)
    suspend fun clearSearchHistory()
}