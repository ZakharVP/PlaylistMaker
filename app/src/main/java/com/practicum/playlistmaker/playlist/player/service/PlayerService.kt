package com.practicum.playlistmaker.playlist.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.root.ui.views.RootActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

class PlayerService : Service(), IPlayerService {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    private val _playerState = MutableStateFlow(PlayerState.DEFAULT)
    override fun playerState(): StateFlow<PlayerState> = _playerState

    private val _currentPosition = MutableStateFlow(0)
    override fun currentPosition(): StateFlow<Int> = _currentPosition

    private var mediaPlayer: MediaPlayer? = null
    private var currentUrl: String? = null
    private var artistName: String? = null
    private var trackTitle: String? = null
    private var progressJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener {
            onPlaybackCompleted()
        }
        mediaPlayer?.setOnErrorListener { _, _, _ ->
            _playerState.value = PlayerState.ERROR
            true
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        // Получаем данные трека из intent при bindService
        intent?.let {
            currentUrl = it.getStringExtra(EXTRA_URL)
            artistName = it.getStringExtra(EXTRA_ARTIST)
            trackTitle = it.getStringExtra(EXTRA_TITLE)
        }
        return binder
    }

    // Реализация IPlayerService:
    override fun prepare(url: String, artist: String?, title: String?) {
        currentUrl = url
        artistName = artist
        trackTitle = title
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setOnPreparedListener {
                _playerState.value = PlayerState.PREPARED
                _currentPosition.value = 0
            }
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
        } catch (e: IOException) {
            _playerState.value = PlayerState.ERROR
        }
    }

    override fun play() {
        mediaPlayer?.start()
        _playerState.value = PlayerState.PLAYING
        startProgressUpdates()
    }

    override fun pause() {
        mediaPlayer?.pause()
        _playerState.value = PlayerState.PAUSED
        stopProgressUpdates()
    }

    override fun reset() {
        mediaPlayer?.reset()
        _playerState.value = PlayerState.DEFAULT
        _currentPosition.value = 0
        stopProgressUpdates()
    }

    override fun release() {
        stopProgressUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val pos = mediaPlayer?.currentPosition ?: 0
                _currentPosition.value = pos

                // Останавливаем обновление если трек завершился
                if (pos >= 30000) {
                    onPlaybackCompleted()
                    break
                }

                delay(300)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun onPlaybackCompleted() {
        pause()
        _currentPosition.value = 0
        // Скрываем уведомление при завершении трека
        hideForegroundNotification()

        // Подготовим трек к следующему запуску
        currentUrl?.let { url ->
            prepare(url, artistName, trackTitle)
        }
    }

    // Foreground notification
    override fun showForegroundNotification() {
        val notification = buildNotification(artistName, trackTitle)
        // указываем тип foreground-сервиса: media playback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIF_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIF_ID, notification)
        }
    }

    override fun hideForegroundNotification() {
        // скрываем уведомление, но сервис может продолжать работать
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    private fun buildNotification(artist: String?, title: String?): Notification {
        val contentText = listOfNotNull(artist, title).joinToString(" - ")
            .ifEmpty { "Воспроизведение трека" }

        val intent = Intent(this, RootActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pi = PendingIntent.getActivity(this, 0, intent, flags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_media)
            .setContentIntent(pi)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID,
                "Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Уведомление о воспроизведении музыки"
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        // Если никто не связан — останавливаем воспроизведение и сервис
        pause()
        hideForegroundNotification()
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    companion object {
        const val CHANNEL_ID = "player_channel"
        const val NOTIF_ID = 1

        // Константы для передачи данных
        const val EXTRA_URL = "extra_url"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_TITLE = "extra_title"
    }
}