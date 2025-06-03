package com.practicum.playlistmaker.domain.interfaces

import com.practicum.playlistmaker.domain.models.Track

interface HistoryUseCase {
    fun getHistory(): List<Track>  // Получить историю
    fun addToHistory(track: Track) // Добавить трек
    fun clearHistory()             // Очистить историю
}