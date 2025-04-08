package com.practicum.playlistmaker.domain.impl

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.sharedPreferences.TrackManager
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repositary.HistoryRepository

class HistoryRepositoryImpl(private val context: Context) : HistoryRepository {
    private val gson = Gson()

    override fun getHistory(): List<Track> {
        return TrackManager.getHistoryTrack(context).mapNotNull {
            gson.fromJson(it, Track::class.java)
        }.reversed()
    }

    override fun clearHistory() {
        TrackManager.clearTrackFromPreferences(context)
    }
}