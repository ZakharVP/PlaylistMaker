package com.practicum.playlistmaker.playlist.search.data.sharedprefs

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.ConstantsApp.Config.PLAYLIST_SETTINGS
import com.practicum.playlistmaker.ConstantsApp.Config.PLAYLIST_SONGS
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class SearchHistoryStorage(
    private val context: Context,
    private val gson: Gson
) {

    fun saveTrackToPreferences(track: Track) {

        val trackJson = gson.toJson(track)
        val trackId = track.trackId

        val existTrack = getHistoryTrack().toMutableList()

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

    fun clearTrackFromPreferences() {
        val trackHistoryString = ""
        val sharedPreferences = context.getSharedPreferences(PLAYLIST_SETTINGS, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(PLAYLIST_SONGS,  trackHistoryString)
            .apply()
    }

    fun getHistoryTrack() : List<String> {
        val sharedPreferences = context.getSharedPreferences(PLAYLIST_SETTINGS, Context.MODE_PRIVATE)
        val trackHistoryString = sharedPreferences.getString(PLAYLIST_SONGS, "") ?: ""
        val songs = trackHistoryString.split("|")
            .filter { it.isNotEmpty()}
        return songs
    }
}