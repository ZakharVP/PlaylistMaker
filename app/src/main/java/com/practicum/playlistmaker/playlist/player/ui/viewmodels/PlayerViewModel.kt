package com.practicum.playlistmaker.playlist.player.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.player.domain.repository.PlayerRepository
import java.util.Timer
import java.util.TimerTask

class PlayerViewModel(
    private val repository: PlayerRepository
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentPosition = MutableLiveData<Int>(0)
    val currentPosition: LiveData<Int> = _currentPosition

    private var updateTimer: Timer? = null

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
        updateTimer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    _currentPosition.postValue(repository.getCurrentPosition())
                }
            }, 0, 300) // Обновление каждые 300 мс
        }
    }

    private fun stopProgressUpdates() {
        updateTimer?.cancel()
        updateTimer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        repository.releasePlayer()
    }
}