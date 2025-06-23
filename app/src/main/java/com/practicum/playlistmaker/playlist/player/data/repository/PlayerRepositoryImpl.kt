package com.practicum.playlistmaker.playlist.player.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.playlist.player.domain.repository.PlayerRepository
import java.io.IOException

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onError: () -> Unit) {

        mediaPlayer.apply {
            try {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { onPrepared() }
                setOnErrorListener { _, _, _ ->
                    onError()
                    true
                }
            } catch (e: IOException) {
                onError()
            }
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition ?: 0

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying ?: false
}