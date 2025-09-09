package com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.mediateka.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val context: Context
) : ViewModel() {

    private val _createPlaylistState = MutableStateFlow<CreatePlaylistState>(CreatePlaylistState.Idle)
    val createPlaylistState: StateFlow<CreatePlaylistState> = _createPlaylistState

    private var coverUri: Uri? = null
    private var editingPlaylistId: Long? = null

    fun setCoverUri(uri: Uri?) {
        coverUri = uri
    }

    fun setEditingPlaylistId(playlistId: Long?) {
        editingPlaylistId = playlistId
    }

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            _createPlaylistState.value = CreatePlaylistState.Loading
            try {
                val coverPath = coverUri?.let { copyImageToInternalStorage(it) }
                val playlistId = playlistInteractor.createPlaylist(name, description, coverPath)
                _createPlaylistState.value = CreatePlaylistState.Success(playlistId)
            } catch (e: Exception) {
                _createPlaylistState.value = CreatePlaylistState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun updatePlaylist(name: String, description: String) {
        viewModelScope.launch {
            _createPlaylistState.value = CreatePlaylistState.Loading
            try {
                editingPlaylistId?.let { playlistId ->
                    val coverPath = coverUri?.let { copyImageToInternalStorage(it) }
                    playlistInteractor.updatePlaylist(playlistId, name, description, coverPath)
                    _createPlaylistState.value = CreatePlaylistState.Success(playlistId)
                } ?: run {
                    _createPlaylistState.value = CreatePlaylistState.Error("ID плейлиста не указан")
                }
            } catch (e: Exception) {
                _createPlaylistState.value = CreatePlaylistState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistInteractor.getPlaylistByIdSync(playlistId)
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            // Создаем уникальное имя файла
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "playlist_cover_$timeStamp.jpg"

            // Создаем файл во внутреннем хранилище
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            // Копируем данные
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // Возвращаем путь к скопированному файлу
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    sealed interface CreatePlaylistState {
        object Idle : CreatePlaylistState
        object Loading : CreatePlaylistState
        data class Success(val playlistId: Long) : CreatePlaylistState
        data class Error(val message: String) : CreatePlaylistState
    }
}