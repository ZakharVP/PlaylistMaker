package com.practicum.playlistmaker.playlist.search.ui.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.playlist.search.domain.useCases.HistoryUseCase
import com.practicum.playlistmaker.playlist.search.domain.useCases.SearchTracksUseCase
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.util.NetworkChecker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.concurrent.thread

class SearchViewModel(
    private val searchUseCase: SearchTracksUseCase,
    private val historyUseCase: HistoryUseCase,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _state = MutableStateFlow<SearchState>(SearchState.Empty)
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var lastSearchQuery: String = ""

    private var _currentQuery = MutableStateFlow("")
    val currentQuery: StateFlow<String> = _currentQuery

    fun setCurrentQuery(query: String) {
        _currentQuery.value = query
    }

    fun searchDebounced(query: String) {

        setCurrentQuery(query)

        if (query.isEmpty()) {
            showHistory()
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)

            if (_currentQuery.value != query) return@launch

            _state.value = SearchState.Loading

            if (!networkChecker.isNetworkAvailable()) {
                _state.value = SearchState.NetworkError("Нет интернет соединения")
                return@launch
            }
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        try {
            val result = searchUseCase.execute(query)
            result.onSuccess { tracks ->
                _state.value = if (tracks.isEmpty()) SearchState.Empty
                else SearchState.Content(tracks)
                _tracks.value = tracks
            }.onFailure { e ->
                _state.value = when (e) {
                    is IOException -> SearchState.NetworkError("Ошибка сети: ${e.message}")
                    else -> SearchState.NetworkError("Ошибка сервера: ${e.message}")
                }
            }
        } catch (e: Exception) {
            _state.value = SearchState.NetworkError("Неизвестная ошибка: ${e.message}")
        }
    }

    fun showHistory() {
        viewModelScope.launch {
            try {
                val history = historyUseCase.getHistory()
                _tracks.value = history
                _state.value = if (history.isNotEmpty()) SearchState.History(history)
                else SearchState.EmptyHistory
            } catch (e: Exception) {
                _state.value = SearchState.EmptyHistory
            }
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            try {
                historyUseCase.addToHistory(track)
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Ошибка при добавлении трека: ${e.message}", e)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                historyUseCase.clearHistory()
                showHistory()
            } catch (e: Exception) {
                _state.value = SearchState.EmptyHistory
            }
        }
    }

    fun retryLastSearch() {
        if (lastSearchQuery.isNotEmpty()) {
            searchDebounced(lastSearchQuery)
        } else {
            showHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
