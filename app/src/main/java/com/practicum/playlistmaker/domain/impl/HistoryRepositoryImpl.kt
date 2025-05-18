package com.practicum.playlistmaker.domain.impl

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.sharedPreferences.TrackManager
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repositary.HistoryRepository

class HistoryRepositoryImpl(private val context: Context) : HistoryRepository {
    private val trackManager = TrackManager(context)
    private val gson = Gson()

    override fun getSearchHistory(): List<Track> {
        return trackManager.getHistoryTrack().mapNotNull {
            gson.fromJson(it, Track::class.java)
        }.reversed()
    }

    override fun addToHistory(track: Track) {
        trackManager.saveTrackToPreferences(track)
    }

    override fun clearSearchHistory() {
        trackManager.clearTrackFromPreferences()
    }
}