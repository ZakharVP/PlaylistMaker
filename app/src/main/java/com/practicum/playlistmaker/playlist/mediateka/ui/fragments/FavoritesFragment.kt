package com.practicum.playlistmaker.playlist.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.FavoritesViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModel()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            if (favorites.isEmpty()) {
                showPlaceholder()
            } else {
                showFavorites(favorites)
            }
        }
    }

    private fun showPlaceholder() {
    }

    private fun showFavorites(favorites: List<Track>) {
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}