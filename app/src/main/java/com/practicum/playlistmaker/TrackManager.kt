package com.practicum.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.ConstantsApp.PLAYLIST_SETTINGS
import com.practicum.playlistmaker.ConstantsApp.PLAYLIST_SONGS
import com.practicum.playlistmaker.domain.Track

object TrackManager {

    fun saveTrackToPreferences(context: Context, track: Track) {

        val gson = Gson()
        val trackJson = gson.toJson(track)
        val trackId = track.trackId

        val existTrack = getHistoryTrack(context).toMutableList()

        val trackToRemove =  existTrack.find{
            gson.fromJson(it, Track::class.java).trackId == trackId
        }

        if (trackToRemove != null) {
            existTrack.remove(trackToRemove)
        }

        if (existTrack.size >= 10) {
            existTrack.removeAt(0)
        }

        existTrack.add(trackJson)
        val trackHistoryString = existTrack.joinToString("|")

        val sharedPreferences = context.getSharedPreferences(PLAYLIST_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(PLAYLIST_SONGS, trackHistoryString)
            .apply()

    }

    fun clearTrackFromPreferences(context: Context) {
        val trackHistoryString = ""
        val sharedPreferences = context.getSharedPreferences(PLAYLIST_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(PLAYLIST_SONGS,  trackHistoryString)
            .apply()
    }

    fun getHistoryTrack(context: Context) : List<String> {
        val sharedPreferences = context.getSharedPreferences(PLAYLIST_SETTINGS, Context.MODE_PRIVATE)
        val trackHistoryString = sharedPreferences.getString(PLAYLIST_SONGS, "") ?: ""
        val songs = trackHistoryString.split("|")
            .filter { it.isNotEmpty()}
        return songs
    }

}