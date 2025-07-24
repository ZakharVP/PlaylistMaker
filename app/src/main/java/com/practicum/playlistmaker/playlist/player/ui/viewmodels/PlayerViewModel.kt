package com.practicum.playlistmaker.playlist.player.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.player.domain.repository.PlayerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val repository: PlayerRepository
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentPosition = MutableLiveData<Int>(0)
    val currentPosition: LiveData<Int> = _currentPosition

    private var progressUpdateJob: Job? = null

    fun preparePlayer(url: String) {
        repository.preparePlayer(
            url = url,
            onPrepared = { _playerState.postValue(PlayerState.PREPARED) },
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
        _playerState.postValue(PlayerState.PLAYING) // Используем postValue для фонового потока
        startProgressUpdates()
    }

    fun pausePlayer() {
        repository.pausePlayer()
        _playerState.postValue(PlayerState.PAUSED)
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        stopProgressUpdates() // Останавливаем предыдущий таймер перед созданием нового
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                _currentPosition.postValue(repository.getCurrentPosition())
                delay(300)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        repository.releasePlayer()
    }
}