package com.practicum.playlistmaker.playlist.player.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioplayerBinding
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.PlayerViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    companion object {
        const val TRACK_EXTRA = "track_extra"

        fun newInstance(track: Track): PlayerFragment {
            return PlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TRACK_EXTRA, track)
                }
            }
        }
    }

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()
    private var isNightMode: Boolean = false

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
        binding.playButton.setOnClickListener { viewModel.playbackControl() }
    }

    private fun setupPlayer(url: String) {
        viewModel.preparePlayer(url)
    }

    private fun setupObservers() {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            updatePlayButton(state)
        }

        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
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
        _binding = null
    }
}