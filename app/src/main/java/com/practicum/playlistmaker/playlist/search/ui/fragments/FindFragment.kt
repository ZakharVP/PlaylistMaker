package com.practicum.playlistmaker.playlist.search.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.ConstantsApp.BundleConstants.TRACK_EXTRA
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.search.ui.compose.FindScreen
import com.practicum.playlistmaker.playlist.settings.ui.SettingsViewModel
import com.practicum.playlistmaker.ui.ObserveAppTheme
import com.practicum.playlistmaker.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class FindFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // Используем ObserveAppTheme для наблюдения за глобальной темой
                ObserveAppTheme { isDarkTheme ->
                    PlaylistMakerTheme(darkTheme = isDarkTheme) {
                        FindScreen(
                            viewModel = searchViewModel,
                            onTrackClick = { track -> onTrackClick(track) },
                            onBackClick = { onBackClick() }
                        )
                    }
                }
            }
        }
    }

    private fun onTrackClick(track: Track) {
        searchViewModel.addToHistory(track)
        findNavController().navigate(
            R.id.action_findFragment_to_playerFragment,
            bundleOf(TRACK_EXTRA to track)
        )
    }

    private fun onBackClick() {
        parentFragmentManager.popBackStack()
    }
}