package com.practicum.playlistmaker.domain.repositary

import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearSearchHistory()
}