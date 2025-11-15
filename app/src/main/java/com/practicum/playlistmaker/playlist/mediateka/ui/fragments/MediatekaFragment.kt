package com.practicum.playlistmaker.playlist.mediateka.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.ConstantsApp.BundleConstants.TRACK_EXTRA
import com.practicum.playlistmaker.playlist.mediateka.ui.compose.MediatekaScreen
import com.practicum.playlistmaker.playlist.settings.ui.SettingsViewModel
import com.practicum.playlistmaker.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.ui.ObserveAppTheme


class MediatekaFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModel(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ObserveAppTheme { isDarkTheme ->
                    PlaylistMakerTheme(darkTheme = isDarkTheme) {
                        MediatekaScreen(
                            onCreatePlaylist = { onCreatePlaylist() },
                            onPlaylistClick = { playlist -> onPlaylistClick(playlist) },
                            onTrackClick = { track -> onTrackClick(track) }
                        )
                    }
                }
            }
        }
    }

    private fun onCreatePlaylist() {
        // Навигация к созданию плейлиста
        findNavController().navigate(R.id.action_mediatekaFragment_to_newPlaylistFragment)
    }

    private fun onPlaylistClick(playlist: Playlist) {
        val bundle = Bundle().apply {
            putLong("playlistId", playlist.id)
        }
        findNavController().navigate(R.id.playlistDetailFragment, bundle)
    }

    private fun onTrackClick(track: Track) {
        val bundle = bundleOf(TRACK_EXTRA to track)
        findNavController().navigate(
            R.id.action_mediatekaFragment_to_playerFragment,
            bundle
        )
    }

    private fun onBackClick() {
        requireActivity().onBackPressed()
    }

    companion object {
        fun newInstance() = MediatekaFragment()
    }
}