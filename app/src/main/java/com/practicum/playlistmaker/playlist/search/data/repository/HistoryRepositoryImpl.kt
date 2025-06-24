package com.practicum.playlistmaker.playlist.search.data.repository

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.playlist.search.data.sharedprefs.SearchHistoryStorage
import com.practicum.playlistmaker.playlist.search.domain.repository.HistoryRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class HistoryRepositoryImpl(
    private val searchHistoryStorage: SearchHistoryStorage,
    private val gson: Gson
) : HistoryRepository {

    override fun getSearchHistory(): List<Track> {
        return searchHistoryStorage.getHistoryTrack().mapNotNull {
            gson.fromJson(it, Track::class.java)
        }.reversed()
    }

    override fun addToHistory(track: Track) {
        searchHistoryStorage.saveTrackToPreferences(track)
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clearTrackFromPreferences()
    }

}