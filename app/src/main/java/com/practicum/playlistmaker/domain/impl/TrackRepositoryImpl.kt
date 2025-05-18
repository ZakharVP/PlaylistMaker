package com.practicum.playlistmaker.domain.impl

import android.util.Log
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.SongRequest
import com.practicum.playlistmaker.data.dto.SongResponse
import com.practicum.playlistmaker.domain.repositary.TrackRepository
import com.practicum.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(SongRequest(expression))
        if (response.resultCode == 200) {
            Log.d("TrackRepositoryImpl","return good list")
            return (response as SongResponse).results.mapNotNull { Track.fromDto(it) }
        } else {
            Log.d("TrackRepositoryImpl","return empty list")
            return emptyList()
        }
    }

}