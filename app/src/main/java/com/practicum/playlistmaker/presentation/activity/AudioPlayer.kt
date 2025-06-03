package com.practicum.playlistmaker.presentation.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.sharedPreferences.ThemeManager
import com.practicum.playlistmaker.domain.models.Track
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayer : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val CHANGE_TIME_DELAY = 300L
    }

    private lateinit var playButton: ImageButton
    private var isNightMode = false
    private var playerState = STATE_DEFAULT
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var timeTrackView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var changeTextRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        isNightMode = ThemeManager.getThemeFromPreferences(this)
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener { onPlayerPrepared() }
            setOnErrorListener { _, what, extra -> onPlayerError(what, extra) }
        }

        initViews()
        setupTrackData()
        setupPlayerControls()
    }

    private fun initViews() {
        playButton = findViewById(R.id.playButton)
        timeTrackView = findViewById(R.id.timeTrack)

        // Инициализация остальных View
        findViewById<Toolbar>(R.id.toolBarAudioPlayer).setNavigationOnClickListener { finish() }
    }

    private fun setupTrackData() {
        val track = intent.extras?.let {
            Track(
                trackName = it.getString("trackName") ?: "",
                artistName = it.getString("artistName") ?: "",
                trackTimeMillisString = getDuration(it.getLong("trackTimeMillis", 0)),
                trackId = it.getString("trackId") ?: "",
                artworkUrl = it.getString("artworkUrl100") ?: "",
                collectionName = it.getString("collectionName") ?: "",
                releaseYear = it.getString("year") ?: "",
                genre = it.getString("primaryGenreName") ?: "",
                country = it.getString("country") ?: "",
                previewUrl = it.getString("previewUrl") ?: ""
            )
        } ?: run {
            finish()
            return
        }

        // Установка данных трека в UI
        findViewById<TextView>(R.id.nameSingle).text = track.trackName
        findViewById<TextView>(R.id.authorSingle).text = track.artistName

        // Загрузка обложки
        Glide.with(this)
            .load(track.artworkUrl?.replace("100x100bb.jpg", "512x512bb.jpg"))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(100)))
            .error(R.drawable.no_image_placeholder)
            .into(findViewById(R.id.imageSingle))

        // Инициализация плеера
        if (track.previewUrl.isNotEmpty()) {
            try {
                mediaPlayer.setDataSource(track.previewUrl)
                mediaPlayer.prepareAsync()
            } catch (e: IOException) {
                Log.d("AudioPlayer","Error loading audio")
                Toast.makeText(this, "Error loading audio", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Log.d("AudioPlayer","Audio not available")
            Toast.makeText(this, "Audio not available", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupPlayerControls() {
        changeTextRunnable = Runnable {
            if (mediaPlayer.isPlaying) {
                timeTrackView.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                handler.postDelayed(changeTextRunnable, CHANGE_TIME_DELAY)
            }
        }

        playButton.setOnClickListener {
            when (playerState) {
                STATE_PLAYING -> pausePlayer()
                STATE_PREPARED, STATE_PAUSED -> startPlayer()
            }
        }
    }

    private fun onPlayerPrepared() {
        playerState = STATE_PREPARED
        playButton.isEnabled = true
        if (isNightMode) {
            playButton.setImageResource(R.drawable.play_button_dark)
        } else {
            playButton.setImageResource(R.drawable.play_button_light)
        }
    }

    private fun onPlayerError(what: Int, extra: Int): Boolean {
        Log.d("AudioPlayer","Playback error")
        Toast.makeText(this, "Playback error", Toast.LENGTH_SHORT).show()
        finish()
        return true
    }

    private fun startPlayer() {
        try {
            mediaPlayer.start()
            playerState = STATE_PLAYING
            updatePlayButton(true)
            startUpdateProgress()
        } catch (e: IllegalStateException) {
            Log.d("AudioPlayer","Playback error")
            Toast.makeText(this, "Playback error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        updatePlayButton(false)
        stopUpdateProgress()
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        val resId = when {
            isPlaying && isNightMode -> R.drawable.pause_dark
            isPlaying -> R.drawable.pause_light
            isNightMode -> R.drawable.play_button_dark
            else -> R.drawable.play_button_light
        }
        playButton.setImageResource(resId)
    }

    private fun startUpdateProgress() {
        handler.post(changeTextRunnable)
    }

    private fun stopUpdateProgress() {
        handler.removeCallbacks(changeTextRunnable)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(changeTextRunnable)
        mediaPlayer.release()
    }

    private fun getDuration(duration: Long): String {
        val totalSeconds = duration / 1000
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60)
    }
}