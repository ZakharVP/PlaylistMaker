package com.practicum.playlistmaker.playlist.search.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.playlist.search.domain.useCases.HistoryUseCase
import com.practicum.playlistmaker.playlist.search.domain.useCases.SearchTracksUseCase
import com.practicum.playlistmaker.util.NetworkChecker

class SearchViewModelFactory(
    private val searchUseCase: SearchTracksUseCase,
    private val historyUseCase: HistoryUseCase,
    private val networkChecker: NetworkChecker
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                searchUseCase = searchUseCase,
                historyUseCase = historyUseCase,
                networkChecker = networkChecker
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}