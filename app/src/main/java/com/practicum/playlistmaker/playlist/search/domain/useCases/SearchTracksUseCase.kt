package com.practicum.playlistmaker.playlist.search.domain.useCases

import com.practicum.playlistmaker.playlist.search.domain.repository.TrackRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.util.NetworkChecker
import kotlin.concurrent.thread
import java.io.IOException

class SearchTracksUseCase(
    private val repository: TrackRepository,
    private val networkChecker: NetworkChecker
) {
    suspend fun execute(query: String): Result<List<Track>> {
        if (!networkChecker.isNetworkAvailable()) {
            return Result.failure(IOException("Нет интернет-соединения"))
        }
        return repository.searchTracks(query)
    }
}
