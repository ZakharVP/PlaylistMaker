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
    fun execute(query: String, callback: (Result<List<Track>>) -> Unit) {
        if (!networkChecker.isNetworkAvailable()) {
            callback(Result.failure(IOException("Нет интернет-соединения")))
            return
        }

        thread {
            try {
                val result = repository.searchTracks(query)
                callback(Result.success(result))
            } catch (e: IOException) {
                callback(Result.failure(IOException("Ошибка сети: ${e.message}")))
            } catch (e: Exception) {
                callback(Result.failure(Exception("Ошибка сервера: ${e.message}")))
            }
        }
    }
}
