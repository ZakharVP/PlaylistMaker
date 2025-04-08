package com.practicum.playlistmaker.domain.repositary

import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun getHistory(): List<Track>
    fun clearHistory()
}