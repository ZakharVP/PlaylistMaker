package com.practicum.playlistmaker.playlist.player.domain.repository

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onError: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): Int
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun reset()
}