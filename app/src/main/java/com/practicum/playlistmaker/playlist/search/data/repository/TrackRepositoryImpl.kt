package com.practicum.playlistmaker.playlist.search.data.repository

import android.util.Log
import com.practicum.playlistmaker.playlist.search.data.dto.SongRequest
import com.practicum.playlistmaker.playlist.search.data.dto.SongResponse
import com.practicum.playlistmaker.playlist.search.data.network.NetworkClient
import com.practicum.playlistmaker.playlist.search.domain.repository.TrackRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override suspend fun searchTracks(expression: String): Result<List<Track>> {
        return try {
            val response = networkClient.doRequest(SongRequest(expression))
            if (response.resultCode == 200) {
                Log.d("TrackRepositoryImpl","return good list")
                Result.success((response as SongResponse).results.mapNotNull { Track.fromDto(it)
                })
            } else {
                Result.failure(Exception("Ошибка сервера: ${response.resultCode}"))
            }
        } catch (e: Exception) {
            Log.d("TrackRepositoryImpl","return empty list")
            Result.failure(e)
        }
    }
}