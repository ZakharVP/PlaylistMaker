package com.practicum.playlistmaker.playlist.player.service

import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface IPlayerService {
    fun prepare(url: String, artist: String?, title: String?)
    fun play()
    fun pause()
    fun reset()
    fun release()
    fun isPlaying(): Boolean

    // Возвращаем StateFlow для подписки
    fun playerState(): StateFlow<PlayerState>
    fun currentPosition(): StateFlow<Int>

    // Управление foreground-уведомлением
    fun showForegroundNotification()
    fun hideForegroundNotification()
}