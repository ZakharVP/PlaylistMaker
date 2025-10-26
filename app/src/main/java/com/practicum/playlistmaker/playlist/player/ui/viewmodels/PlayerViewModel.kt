package com.practicum.playlistmaker.playlist.player.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.mediateka.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.player.service.IPlayerService
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentPosition = MutableLiveData<Int>(0)
    val currentPosition: LiveData<Int> = _currentPosition

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private var playerService: IPlayerService? = null
    private var currentTrackArtist: String? = null
    private var currentTrackTitle: String? = null

    private var stateJob: Job? = null
    private var posJob: Job? = null

    fun setPlayerService(service: IPlayerService, artist: String?, title: String?) {
        // Отменяем старые подписки если есть
        stateJob?.cancel()
        posJob?.cancel()

        this.playerService = service
        this.currentTrackArtist = artist
        this.currentTrackTitle = title

        stateJob = viewModelScope.launch {
            service.playerState().collect { state ->
                _playerState.postValue(state)

                // Автоматически скрываем уведомление когда трек не в PLAYING
                if (state != PlayerState.PLAYING) {
                    service.hideForegroundNotification()
                }
            }
        }

        posJob = viewModelScope.launch {
            service.currentPosition().collect { position ->
                _currentPosition.postValue(position)
            }
        }
    }

    fun clearPlayerService() {
        stateJob?.cancel()
        posJob?.cancel()
        stateJob = null
        posJob = null

        playerService = null
        currentTrackArtist = null
        currentTrackTitle = null
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                _playlists.postValue(playlists.toList())
            }
        }
    }

    fun preparePlayer(url: String) {
        playerService?.prepare(url, currentTrackArtist, currentTrackTitle)
    }

    fun playbackControl() {
        when (_playerState.value) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        playerService?.play()
        // Уведомление показывается в сервисе при старте воспроизведения
    }

    fun pausePlayer() {
        playerService?.pause()
        // Уведомление скрывается автоматически через StateFlow колбэк
    }

    fun showNotification() {
        if (_playerState.value == PlayerState.PLAYING) {
            playerService?.showForegroundNotification()
        }
    }

    fun hideNotification() {
        playerService?.hideForegroundNotification()
    }

    override fun onCleared() {
        super.onCleared()
        stateJob?.cancel()
        posJob?.cancel()
    }

}