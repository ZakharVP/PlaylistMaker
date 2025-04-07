package com.practicum.playlistmaker.Activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ThemeManager
import com.practicum.playlistmaker.data.Track
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayer: AppCompatActivity() {

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
    private var mediaPlayer = MediaPlayer()
    var url = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/ac/c7/d1/acc7d13f-6634-495f-caf6-491eccb505e8/mzaf_4002676889906514534.plus.aac.p.m4a"

    lateinit var timeTrackView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var changeTextRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        isNightMode = ThemeManager.getThemeFromPreferences(this)

        // *** Блок инициализации View. Начало *** //
        val buttonAddSingle = findViewById<ImageButton>(R.id.buttonAddSingle)
        playButton = findViewById<ImageButton>(R.id.playButton)
        val buttonLikeSingle = findViewById<ImageButton>(R.id.buttonLikeSingle)

        val tool_bar_button_back = findViewById<Toolbar>(R.id.toolBarAudioPlayer)
        val imageSingleView = findViewById<ImageView>(R.id.imageSingle)

        timeTrackView = findViewById<TextView>(R.id.timeTrack)

        val nameSingle = findViewById<TextView>(R.id.nameSingle)
        val authorSingle = findViewById<TextView>(R.id.authorSingle)
        val durationDataView = findViewById<TextView>(R.id.durationData)
        val durationNameView = findViewById<TextView>(R.id.durationName)
        val albomDataView = findViewById<TextView>(R.id.albomData)
        val albomNameView = findViewById<TextView>(R.id.albomName)
        val yearDataView = findViewById<TextView>(R.id.yearData)
        val yearNameView = findViewById<TextView>(R.id.yearName)
        val genreDataView = findViewById<TextView>(R.id.genreData)
        val genreNameView = findViewById<TextView>(R.id.genreName)
        val countryDataView = findViewById<TextView>(R.id.countryData)
        val countryNameView = findViewById<TextView>(R.id.countryName)
        // *** Блок инициализации View. Окончание *** //

        val track = Track(
            trackName = intent.getStringExtra("trackName") ?: "",
            artistName = intent.getStringExtra("artistName") ?: "",
            trackTimeMillis = intent.getLongExtra("trackTimeMillis",0) ?: 0,
            trackTimeMillisString = getDuration(intent.getLongExtra("trackTimeMillis",0)) ?: "",
            trackId = intent.getStringExtra("trackId") ?: "",
            artworkUrl100 = intent.getStringExtra("artworkUrl100") ?: "",
            collectionName = intent.getStringExtra("collectionName") ?: "",
            releaseDate = intent.getStringExtra("year") ?: "",
            primaryGenreName = intent.getStringExtra("primaryGenreName") ?: "",
            country = intent.getStringExtra("country") ?: "",
            previewUrl = intent.getStringExtra("previewUrl") ?: ""
        )

        // *** Блок установки внешних данных. Начало *** //
        nameSingle.text = track.trackName
        authorSingle.text = track.artistName

        url = track.previewUrl

        if (track.trackTimeMillisString.isNotEmpty()) {
            durationDataView.text = track.trackTimeMillisString
        } else {
            durationDataView.visibility = View.GONE
            durationNameView.visibility = View.GONE
        }

        if (track.collectionName.isNotEmpty()) {
            albomDataView.text = track.collectionName
        } else {
            albomDataView.visibility = View.GONE
            albomNameView.visibility = View.GONE
        }
        yearDataView.text = "2000"

        if (track.primaryGenreName.isNotEmpty()) {
            genreDataView.text = track.primaryGenreName
        } else {
            genreNameView.visibility = View.GONE
            genreDataView.visibility = View.GONE
        }

        if (track.releaseDate.isNotEmpty()){
            yearDataView.text = track.releaseDate
        } else {
            yearDataView.visibility = View.GONE
            yearNameView.visibility = View.GONE
        }

        if (track.country.isNotEmpty()){
            countryDataView.text = track.country
        } else {
            countryDataView.visibility = View.GONE
            countryNameView.visibility = View.GONE
        }

        changeTextRunnable = Runnable {
            if (mediaPlayer.isPlaying) {
                val currentTime = mediaPlayer.currentPosition
                Log.d("CurrentTime",currentTime.toString())
                val currentTextTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTime)
                timeTrackView.setText(currentTextTime)
                handler.postDelayed(changeTextRunnable, CHANGE_TIME_DELAY)
            } else {
                timeTrackView.setText("00:00")
                if (isNightMode) {
                    playButton.setImageResource(R.drawable.play_button_dark)
                } else {
                    playButton.setImageResource(R.drawable.play_button_light)
                }
            }

        }


        // *** Блок установки внешних данных. Окончание *** //

        // *** Блок установки картинок для тем (светлой и темной). Начало *** //
        if (isNightMode) {
            buttonAddSingle.setImageResource(R.drawable.add_single_dark)
            playButton.setImageResource(R.drawable.play_button_dark)
            buttonLikeSingle.setImageResource(R.drawable.like_button_dark)
        } else {
            buttonAddSingle.setImageResource(R.drawable.add_single_light)
            playButton.setImageResource(R.drawable.play_button_light)
            buttonLikeSingle.setImageResource(R.drawable.like_button_light)
        }
        // *** Блок установки картинок для тем (светлой и темной). Окончание *** //


        val artBigArtUrl = track.artworkUrl100?.let { getBigArtUrl(track.artworkUrl100) }

        tool_bar_button_back.setNavigationOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(artBigArtUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(100)))
                .error(R.drawable.no_image_placeholder)
            .placeholder(R.drawable.no_image_placeholder)
            .into(imageSingleView)

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdateProgress()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
    }

    private fun startUpdateProgress() {
        handler.post(changeTextRunnable)
    }

    private fun stopUpdateProgress() {
        handler.removeCallbacks(changeTextRunnable)
    }

    private fun startPlayer() {

        mediaPlayer.start()

        if (isNightMode) {
            playButton.setImageResource(R.drawable.pause_dark)
        } else {
            playButton.setImageResource(R.drawable.pause_light)
        }
        playerState = STATE_PLAYING

        startUpdateProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        if (isNightMode) {
            playButton.setImageResource(R.drawable.play_button_dark)
        } else {
            playButton.setImageResource(R.drawable.play_button_light)
        }
        stopUpdateProgress()
    }

    private fun playbackControl () {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    fun getBigArtUrl(originalUrl: String): String{
        return originalUrl.replace("100x100bb.jpg", "512x512bb.jpg")
    }

    fun getDuration(duration: Long): String {
        val totalSeconds = duration / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}