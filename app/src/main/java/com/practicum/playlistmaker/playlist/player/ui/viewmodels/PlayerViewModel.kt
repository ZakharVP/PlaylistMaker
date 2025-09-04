package com.practicum.playlistmaker.playlist.player.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.mediateka.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.playlist.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val repository: PlayerRepository,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentPosition = MutableLiveData<Int>(0)
    val currentPosition: LiveData<Int> = _currentPosition

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private var progressUpdateJob: Job? = null
    private var currentUrl: String? = null

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                _playlists.postValue(playlists.toList())
            }
        }
    }

    init {
        // Для PlayerRepositoryImpl с поддержкой completion listener
        (repository as? PlayerRepositoryImpl)?.setOnCompletionListener {
            onPlaybackCompleted()
        }
    }

    fun preparePlayer(url: String) {
        currentUrl = url
        repository.preparePlayer(
            url = url,
            onPrepared = {
                _playerState.postValue(PlayerState.PREPARED)
                _currentPosition.postValue(0)
            },
            onError = { _playerState.postValue(PlayerState.ERROR) }
        )
    }

    fun playbackControl() {
        when (_playerState.value) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {} // Добавлен default case
        }
    }

    private fun startPlayer() {
        repository.startPlayer()
        _playerState.postValue(PlayerState.PLAYING)
        startProgressUpdates()
    }

    fun pausePlayer() {
        repository.pausePlayer()
        _playerState.postValue(PlayerState.PAUSED)
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                val position = repository.getCurrentPosition()
                _currentPosition.postValue(position)

                if (position >= 30000) {
                    onPlaybackCompleted()
                    break
                }

                delay(300)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    private fun onPlaybackCompleted() {
        pausePlayer()
        _currentPosition.postValue(0)
        stopProgressUpdates()

        currentUrl?.let { url ->
            repository.preparePlayer(
                url = url,
                onPrepared = { _playerState.postValue(PlayerState.PREPARED) },
                onError = { _playerState.postValue(PlayerState.ERROR) }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        repository.releasePlayer()
    }
}