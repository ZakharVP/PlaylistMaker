package com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.mediateka.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist.asStateFlow()

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _totalDuration = MutableStateFlow("")
    val totalDuration: StateFlow<String> = _totalDuration.asStateFlow()

    private val _playlistId = MutableStateFlow<Long?>(null)

    fun loadPlaylistData(playlistId: Long) {
        _playlistId.value = playlistId
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                _playlist.value = playlist
                playlist?.let {
                    loadTracks(it.tracks.map { track -> track.trackId })
                }
            }
        }
    }

    private fun loadTracks(trackIds: List<String>) {
        viewModelScope.launch {
            playlistInteractor.getTracksForPlaylist(trackIds).collect { tracks ->
                _tracks.value = tracks
                calculateTotalDuration(tracks)
            }
        }
    }

    private fun calculateTotalDuration(tracks: List<Track>) {
        val totalMillis = tracks.sumOf { track ->
            parseTrackTimeToMillis(track.trackTimeMillisString)
        }
        val minutes = totalMillis / (1000 * 60)
        _totalDuration.value = formatMinutes(minutes)
    }

    private fun formatMinutes(minutes: Long): String {
        return when {
            minutes % 10 == 1L && minutes % 100 != 11L -> "$minutes минута"
            minutes % 10 in 2..4 && minutes % 100 !in 12..14 -> "$minutes минуты"
            else -> "$minutes минут"
        }
    }

    private fun parseTrackTimeToMillis(trackTime: String): Long {
        return try {
            val parts = trackTime.split(":")
            val minutes = parts[0].toLong()
            val seconds = parts[1].toLong()
            (minutes * 60 + seconds) * 1000
        } catch (e: Exception) {
            0L
        }
    }

    fun removeTrack(trackId: String) {
        viewModelScope.launch {
            _playlistId.value?.let { playlistId ->
                playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
                // Перезагружаем данные
                loadPlaylistData(playlistId)
            }
        }
    }

    // Добавляем метод для удаления плейлиста
    fun deletePlaylist() {
        viewModelScope.launch {
            _playlistId.value?.let { playlistId ->
                playlistInteractor.deletePlaylist(playlistId)
            }
        }
    }

    // Метод для получения текста для шаринга
    fun getPlaylistForSharing(): String {
        val currentPlaylist = _playlist.value
        val currentTracks = _tracks.value

        return if (currentTracks.isEmpty()) {
            ""
        } else {
            buildSharingText(currentPlaylist, currentTracks)
        }
    }

    // Формирование текста для шаринга
    private fun buildSharingText(playlist: Playlist?, tracks: List<Track>): String {
        val builder = StringBuilder()

        // Добавляем название плейлиста
        playlist?.name?.let { name ->
            builder.append("$name\n")
        }

        // Добавляем описание (если есть)
        playlist?.description?.takeIf { it.isNotBlank() }?.let { description ->
            builder.append("$description\n")
        }

        // Добавляем количество треков
        val trackCount = tracks.size
        val trackCountText = when {
            trackCount % 10 == 1 && trackCount % 100 != 11 -> "$trackCount трек"
            trackCount % 10 in 2..4 && trackCount % 100 !in 12..14 -> "$trackCount трека"
            else -> "$trackCount треков"
        }
        builder.append("$trackCountText\n\n")

        // Добавляем пронумерованный список треков
        tracks.forEachIndexed { index, track ->
            builder.append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillisString})\n")
        }

        return builder.toString()
    }

    // Метод для получения текста информации о плейлисте (для меню)
    fun getPlaylistInfoText(): String {
        val currentPlaylist = _playlist.value
        val currentTracks = _tracks.value

        return if (currentTracks.isEmpty()) {
            "Нет треков"
        } else {
            val trackCount = currentTracks.size
            val trackCountText = when {
                trackCount % 10 == 1 && trackCount % 100 != 11 -> "$trackCount трек"
                trackCount % 10 in 2..4 && trackCount % 100 !in 12..14 -> "$trackCount трека"
                else -> "$trackCount треков"
            }
            "$trackCountText"
        }
    }
}