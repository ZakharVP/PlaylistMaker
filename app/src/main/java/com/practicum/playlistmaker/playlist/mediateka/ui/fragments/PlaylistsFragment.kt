package com.practicum.playlistmaker.playlist.mediateka.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.playlist.mediateka.ui.adapters.PlaylistAdapter
import com.practicum.playlistmaker.playlist.mediateka.ui.decorations.GridSpacingItemDecoration
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.PlaylistsViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private val viewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupButtonListener()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        Log.i("PlaylistsFragment", "onViewCreated called")
        playlistAdapter = PlaylistAdapter { playlist ->
            val bundle = Bundle().apply {
                putLong("playlistId", playlist.id)
            }
            findNavController().navigate(R.id.playlistDetailFragment, bundle)
        }

        // Используем GridLayoutManager с 2 колонками
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.layoutManager = gridLayoutManager
        binding.playlistsRecyclerView.adapter = playlistAdapter

        // Добавляем отступы 8dp между элементами
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        binding.playlistsRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 2,
                spacing = spacingInPixels,
                includeEdge = true
            )
        )

        // Убедитесь, что у RecyclerView тоже есть padding
        binding.playlistsRecyclerView.setPadding(0, 0, 0, 0)
    }

    private fun observeViewModel() {
        Log.i("PlaylistsFragment", "observeViewModel subscribed")
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                showPlaylistsPlaceholder()
            } else {
                showPlaylistsList(playlists)
            }
            Log.i("PlaylistsFragment", "emit playlists size=${playlists.size} ids=${playlists.map { it.id }}")
            Log.i("PlaylistsFragment", "adapter current size=${playlistAdapter.currentList.size} ids=${playlistAdapter.currentList.map { it.id }}")
            playlistAdapter.submitList(playlists.toList()) {
                Log.i("PlaylistsFragment", "submitList committed")
            }
        }
    }

    private fun showPlaylistsPlaceholder() {
        binding.playlistsRecyclerView.visibility = View.GONE
        binding.playlistsImage.visibility = View.VISIBLE
        binding.playlistsText.visibility = View.VISIBLE
    }

    private fun showPlaylistsList(playlists: List<Playlist>) {
        binding.playlistsRecyclerView.visibility = View.VISIBLE
        binding.playlistsImage.visibility = View.GONE
        binding.playlistsText.visibility = View.GONE
    }

    private fun setupButtonListener() {
        binding.buttonUpdate.setOnClickListener {
            navigateToNewPlaylist()
        }
    }

    fun navigateToNewPlaylist() {
        // Используем Navigation Component для навигации
        val navController = findNavController()
        navController.navigate(R.id.action_mediatekaFragment_to_newPlaylistFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}