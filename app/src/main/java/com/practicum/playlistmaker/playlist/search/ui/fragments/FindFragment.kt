package com.practicum.playlistmaker.playlist.search.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentFindBinding
import com.practicum.playlistmaker.playlist.search.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchState
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import kotlinx.coroutines.launch

class FindFragment : Fragment() {

    private var _binding: FragmentFindBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()
    private val adapter = TrackAdapter { onTrackClick(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupObservers()
        setupListeners()

        viewModel.showHistory()
    }

    private fun setupToolbar() {
        binding.toolBarFind.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.findEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                s?.toString()?.let { viewModel.searchDebounced(it) }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.findEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.findEditText.text.toString()
                viewModel.searchDebounced(query)
                true
            } else {
                false
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.tracks.collect { tracks ->
                adapter.submitList(tracks)
            }
        }

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                Log.d("SearchState", "New state: $state")
                binding.progressBar.isVisible = state is SearchState.Loading
                binding.scrollViewOne.isVisible = state is SearchState.Content || state is SearchState.History
                binding.noSongs.isVisible = state is SearchState.Empty
                binding.networkError.isVisible = state is SearchState.NetworkError

                binding.searchHint.isVisible = false
                binding.clearHistory.isVisible = false

                when (state) {
                    is SearchState.History -> {
                        val hasHistory = state.tracks.isNotEmpty()
                        binding.searchHint.isVisible = hasHistory
                        binding.clearHistory.isVisible = hasHistory
                        adapter.submitList(state.tracks)
                    }
                    is SearchState.Content -> adapter.submitList(state.tracks)
                    is SearchState.Empty,
                    is SearchState.NetworkError -> adapter.submitList(emptyList())
                    else -> Unit
                }
            }
        }
    }

    private fun setupListeners() {
        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.clearIcon.setOnClickListener {
            binding.findEditText.setText("")
        }

        binding.buttonUpdate.setOnClickListener {
            viewModel.retryLastSearch()
        }
    }

    private fun onTrackClick(track: Track) {
        viewModel.addToHistory(track)

        findNavController().navigate(
            R.id.action_findFragment_to_playerFragment,
            bundleOf("track_extra" to track)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}