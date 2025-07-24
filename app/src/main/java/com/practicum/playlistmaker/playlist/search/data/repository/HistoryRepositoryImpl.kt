package com.practicum.playlistmaker.playlist.search.data.repository

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.playlist.search.data.sharedprefs.SearchHistoryStorage
import com.practicum.playlistmaker.playlist.search.domain.repository.HistoryRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepositoryImpl(
    private val searchHistoryStorage: SearchHistoryStorage,
    private val gson: Gson
) : HistoryRepository {

    override suspend fun getSearchHistory(): List<Track> =
        withContext(Dispatchers.IO) {
            searchHistoryStorage.getHistoryTrack()
                .mapNotNull { gson.fromJson(it, Track::class.java)}
                .reversed()
    }

    override suspend fun addToHistory(track: Track) =
        withContext(Dispatchers.IO) {
        searchHistoryStorage.saveTrackToPreferences(track)
    }

    override suspend fun clearSearchHistory() =
        withContext(Dispatchers.IO) {
        searchHistoryStorage.clearTrackFromPreferences()
    }
}