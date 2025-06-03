package com.practicum.playlistmaker.domain.repositories

import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepositoryInter {
    fun getSearchHistory(): List<Track>  // Как получить данные
    fun addSearchToHistory(track: Track) // Как сохранить
    fun clearSearchHistory()             // Как удалить
}