package com.practicum.playlistmaker.playlist.search.ui.viewmodels

import com.practicum.playlistmaker.playlist.sharing.data.models.Track

sealed class SearchState {
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    object Empty : SearchState() // Нет результатов поиска (но сеть есть)
    data class NetworkError(val message: String) : SearchState() // Ошибка сети
    data class History(val tracks: List<Track>) : SearchState()
    object EmptyHistory : SearchState()
}