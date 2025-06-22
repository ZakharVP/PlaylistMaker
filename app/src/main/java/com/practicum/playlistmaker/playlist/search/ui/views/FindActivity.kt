package com.practicum.playlistmaker.playlist.search.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivityFindBinding
import com.practicum.playlistmaker.playlist.creator.Creator
import com.practicum.playlistmaker.playlist.player.ui.views.PlayerActivity
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import com.practicum.playlistmaker.playlist.search.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchState
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchViewModel
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchViewModelFactory

class FindActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindBinding
    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModelFactory(
                searchUseCase = Creator.provideSearchTracksUseCase(
                    repository = Creator.provideTrackRepository(applicationContext),
                    networkChecker = Creator.provideNetworkChecker(applicationContext)
                ),
                historyUseCase = Creator.provideHistoryUseCase(this),
                networkChecker = Creator.provideNetworkChecker(applicationContext)
            )
        )[SearchViewModel::class.java]
    }
    private val adapter = TrackAdapter { onTrackClick(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupObservers()
        setupListeners()

        viewModel.showHistory()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarFind)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Обработчик нажатия на кнопку "Назад"
        binding.toolBarFind.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSearchView() {
        binding.findEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
        viewModel.tracks.observe(this) { tracks ->
            adapter.submitList(tracks)
        }

        viewModel.state.observe(this) { state ->
            Log.d("SearchState", "New state: $state")
            binding.progressBar.isVisible = state is SearchState.Loading
            binding.scrollViewOne.isVisible = state is SearchState.Content || state is SearchState.History
            binding.noSongs.isVisible = state is SearchState.Empty
            binding.networkError.isVisible = state is SearchState.NetworkError

            // Сначала скрываем все дополнительные элементы
            binding.searchHint.isVisible = false
            binding.clearHistory.isVisible = false

            when (state) {
                is SearchState.History -> {
                    val tracks = state.tracks
                    val hasHistory = tracks.isNotEmpty()
                    // Показываем только при наличии истории
                    binding.searchHint.isVisible = hasHistory
                    binding.clearHistory.isVisible = hasHistory
                    adapter.submitList(tracks)
                }
                is SearchState.Content -> {
                    adapter.submitList(state.tracks)
                    binding.searchHint.isVisible = false
                }
                is SearchState.Empty -> {
                    adapter.submitList(emptyList())
                }
                is SearchState.NetworkError -> {
                    adapter.submitList(emptyList())
                }
                is SearchState.Loading -> {
                    // Обработка загрузки
                }
                SearchState.EmptyHistory -> {
                    // Ничего не делаем, элементы уже скрыты
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

        // Используем константу TRACK_EXTRA из PlayerActivity
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.TRACK_EXTRA, track)
        }
        startActivity(intent)
    }
}