package com.practicum.playlistmaker.playlist.mediateka.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.ConstantsApp.BundleConstants
import com.practicum.playlistmaker.ConstantsApp.BundleConstants.TRACK_EXTRA
import com.practicum.playlistmaker.ConstantsApp.Config
import com.practicum.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.playlist.mediateka.ui.adapters.FavoritesAdapter
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.FavoritesViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Track

class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModel()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoritesAdapter

    private val TAG = "${Config.BASE_TAG}_FavoritesFragment"

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

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter { track ->
            val bundle = bundleOf(TRACK_EXTRA to track)

            parentFragment?.findNavController()?.navigate(
                R.id.action_mediatekaFragment_to_playerFragment,
                bundle
            )
        }

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            if (favorites.isEmpty()) {
                showPlaceholder()
            } else {
                showFavorites(favorites)
            }
        }
    }

    private fun showPlaceholder() {
        binding.favoritesRecyclerView.visibility = View.GONE
        binding.placeholderGroup.visibility = View.VISIBLE
    }

    private fun showFavorites(favorites: List<Track>) {
        binding.placeholderGroup.visibility = View.GONE
        binding.favoritesRecyclerView.visibility = View.VISIBLE
        adapter.submitList(favorites)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}