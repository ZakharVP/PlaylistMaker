package com.practicum.playlistmaker.playlist.player.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.ConstantsApp.BundleConstants.TRACK_EXTRA
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioplayerBinding
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.FavoriteViewModel
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.PlayerViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!
    private var isNightMode: Boolean = false

    private val playerViewModel: PlayerViewModel by viewModel()
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    companion object {
        fun newInstance(track: Track): PlayerFragment {
            return PlayerFragment().apply {
                arguments = bundleOf(TRACK_EXTRA to track)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isNightMode = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            else -> false
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(TRACK_EXTRA) as? Track
        } ?: run {
            parentFragmentManager.popBackStack()
            return
        }

        setupViews(track)
        setupPlayer(track.previewUrl)
        setupObservers()
        setupFavoriteButton(track)
    }

    private fun setupViews(track: Track) {
        binding.toolBarAudioPlayer.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        Glide.with(this)
            .load(track.getBigArtUrl())
            .transform(RoundedCorners(100))
            .error(R.drawable.no_image_placeholder)
            .into(binding.imageSingle)

        binding.nameSingle.text = track.trackName
        binding.authorSingle.text = track.artistName
        binding.playButton.setOnClickListener { playerViewModel.playbackControl() }

        binding.durationData.text = track.trackTimeMillisString
        binding.albomData.text = track.collectionName
        binding.yearData.text = track.releaseYear
        binding.genreData.text = track.genre
        binding.countryData.text = track.country

    }

    private fun setupPlayer(url: String) {
        playerViewModel.preparePlayer(url)
    }

    private fun setupObservers() {
        playerViewModel.playerState.observe(viewLifecycleOwner) { state ->
            updatePlayButton(state)
        }

        playerViewModel.currentPosition.observe(viewLifecycleOwner) { position ->
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
        playerViewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupFavoriteButton(track: Track) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteViewModel.isFavorite.collect { isFavorite ->
                    updateFavoriteButtonIcon(isFavorite)
                }
            }
        }

        favoriteViewModel.checkFavorite(track.trackId)

        binding.buttonLikeSingle.setOnClickListener {
            favoriteViewModel.toggleFavorite(track)
        }
    }

    private fun updateFavoriteButtonIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.like_button_filled
        } else {
            R.drawable.like_button
        }
        binding.buttonLikeSingle.setImageResource(iconRes)
    }
}