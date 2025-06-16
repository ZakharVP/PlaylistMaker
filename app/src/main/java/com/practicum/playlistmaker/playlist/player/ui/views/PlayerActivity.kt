package com.practicum.playlistmaker.playlist.player.ui.views

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.playlist.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.PlayerViewModel
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.PlayerViewModelFactory
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_EXTRA = "track_extra"
    }

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var viewModel: PlayerViewModel
    private var isNightMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Определяем текущую тему
        isNightMode = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            else -> false
        }

        // Инициализация ViewModel с фабрикой
        val repository = PlayerRepositoryImpl()
            viewModel = ViewModelProvider(
                this,
                PlayerViewModelFactory(repository)
            )[PlayerViewModel::class.java]

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_EXTRA) as? Track
        } ?: run {
            finish()
            return
        }

        setupViews(track)
        setupPlayer(track.previewUrl)
        setupObservers()
    }

    private fun setupViews(track: Track) {
        binding.toolBarAudioPlayer.setNavigationOnClickListener { finish() }

        Glide.with(this)
            .load(track.getBigArtUrl())
            .transform(RoundedCorners(100))
            .error(R.drawable.no_image_placeholder)
            .into(binding.imageSingle)

        binding.nameSingle.text = track.trackName
        binding.authorSingle.text = track.artistName
        binding.playButton.setOnClickListener { viewModel.playbackControl() }
    }

    private fun setupPlayer(url: String) {
        viewModel.preparePlayer(url)
    }

    private fun setupObservers() {
        viewModel.playerState.observe(this) { state ->
            updatePlayButton(state)
        }

        viewModel.currentPosition.observe(this) { position ->
            binding.timeTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(position)
        }
    }

    private fun updatePlayButton(state: PlayerState) {
        val iconRes = when {
            state == PlayerState.PLAYING && isNightMode -> R.drawable.pause_dark
            state == PlayerState.PLAYING -> R.drawable.pause_light
            isNightMode -> R.drawable.play_button_dark
            else -> R.drawable.play_button_light
        }
        binding.playButton.setImageResource(iconRes)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        // ViewModel очистится автоматически через onCleared()
    }
}