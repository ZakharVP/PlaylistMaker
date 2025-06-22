package com.practicum.playlistmaker.playlist.search.domain.repository

import com.practicum.playlistmaker.playlist.sharing.data.models.Track

interface HistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearSearchHistory()
}