package com.practicum.playlistmaker.playlist.search.ui.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.playlist.search.domain.useCases.HistoryUseCase
import com.practicum.playlistmaker.playlist.search.domain.useCases.SearchTracksUseCase
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.util.NetworkChecker
import java.io.IOException
import kotlin.concurrent.thread

class SearchViewModel(
    private val searchUseCase: SearchTracksUseCase,
    private val historyUseCase: HistoryUseCase,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private val mainHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var lastSearchQuery: String = ""

    fun searchDebounced(query: String) {
        // Если запрос пустой - показываем историю
        if (query.isEmpty()) {
            showHistory()
            return
        }

        lastSearchQuery = query
        searchRunnable?.let { mainHandler.removeCallbacks(it) }

        searchRunnable = Runnable {
            _state.postValue(SearchState.Loading)

            if (!networkChecker.isNetworkAvailable()) {
                mainHandler.postDelayed({
                    if (!networkChecker.isNetworkAvailable()) {
                        _state.postValue(SearchState.NetworkError("Нет интернет-соединения"))
                    } else {
                        performSearch(query)
                    }
                }, 500)
                return@Runnable
            }

            performSearch(query)
        }

        mainHandler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch(query: String) {
        if (!networkChecker.isNetworkAvailable()) {
            _state.postValue(SearchState.NetworkError("Нет интернет-соединения"))
            return
        }

        searchUseCase.execute(query) { result ->
            result.onSuccess { tracks ->
                _state.postValue(
                    if (tracks.isEmpty()) SearchState.Empty
                    else SearchState.Content(tracks)
                )
            }.onFailure { e ->
                _state.postValue(
                    if (e is IOException) {
                        SearchState.NetworkError("Ошибка сети: ${e.message}")
                    } else {
                        SearchState.NetworkError("Ошибка сервера: ${e.message}")
                    }
                )
            }
        }
    }

    fun isNetworkAvailable(): Boolean {
        return try {
            networkChecker.isNetworkAvailable()
        } catch (e: Exception) {
            Log.e("NetworkCheck", "Error checking network", e)
            true
        }
    }

    fun showHistory() {
        thread {
            try {
                historyUseCase.getHistory { history ->
                    mainHandler.post {
                        _tracks.value = history
                        _state.value = if (history.isNotEmpty()) {
                            SearchState.History(history)
                        } else {
                            SearchState.EmptyHistory
                        }
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    _state.value = SearchState.EmptyHistory
                }
            }
        }
    }

    fun addToHistory(track: Track) {
        thread {
            try {
                historyUseCase.addToHistory(track)
            } catch (e: Exception) {
                // Логирование ошибки
            }
        }
    }

    fun clearHistory() {
        thread {
            try {
                historyUseCase.clearHistory()
                showHistory()
            } catch (e: Exception) {
                mainHandler.post {
                    _state.value = SearchState.EmptyHistory
                }
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
        searchRunnable?.let { mainHandler.removeCallbacks(it) }
    }
}
