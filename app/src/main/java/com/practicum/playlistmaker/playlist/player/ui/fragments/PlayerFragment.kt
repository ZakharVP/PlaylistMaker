package com.practicum.playlistmaker.playlist.player.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.ConstantsApp.BundleConstants.TRACK_EXTRA
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioplayerBinding
import com.practicum.playlistmaker.playlist.mediateka.domain.interactor.PlaylistInteractor
import com.practicum.playlistmaker.playlist.player.domain.model.PlayerState
import com.practicum.playlistmaker.playlist.player.ui.adapters.PlaylistBottomSheetAdapter
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.FavoriteViewModel
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.PlayerViewModel
import com.practicum.playlistmaker.playlist.sharing.data.models.Playlist
import com.practicum.playlistmaker.playlist.sharing.data.models.Track
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.navigation.fragment.findNavController

class PlayerFragment : Fragment() {

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!
    private var isNightMode: Boolean = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var playlistsAdapter: PlaylistBottomSheetAdapter
    private lateinit var playlistsRecyclerView: RecyclerView

    private val playerViewModel: PlayerViewModel by viewModel()
    private val favoriteViewModel: FavoriteViewModel by viewModel()
    private val playlistInteractor: PlaylistInteractor by inject() // Добавлено

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

        setupBottomSheet()
        setupViews(track)
        setupPlayer(track.previewUrl)
        setupObservers()
        setupFavoriteButton(track)
    }

    private fun setupBottomSheet() {
        // Инициализация Bottom Sheet Behavior
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)

        // Настраиваем начальное состояние - скрыто
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.skipCollapsed = true

        playlistsRecyclerView = binding.playlistsRecyclerView
        playlistsAdapter = PlaylistBottomSheetAdapter { playlist ->
            // Обработка выбора плейлиста
            addTrackToPlaylist(playlist)
        }

        playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        playlistsRecyclerView.adapter = playlistsAdapter

        // Добавлено наблюдение за списком плейлистов
        playerViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistsAdapter.updateList(playlists.toList())
            if (playlists.isNotEmpty() && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                playlistsRecyclerView.scrollToPosition(0)
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        binding.overlay.alpha = 0f
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.overlay.alpha = 1f
                        playerViewModel.loadPlaylists()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Плавное изменение прозрачности overlay
                binding.overlay.alpha = slideOffset
                binding.overlay.visibility = if (slideOffset > 0) View.VISIBLE else View.GONE
            }
        })

        // Обработчик клика на overlay (закрывает Bottom Sheet)
        binding.overlay.setOnClickListener {
            hideBottomSheet()
        }

        // Обработчик клика на кнопку создания нового плейлиста
        binding.createNewPlaylistButton.setOnClickListener {
            // Логика создания нового плейлиста
            hideBottomSheet()
        }
    }

    private fun addTrackToPlaylist(playlist: Playlist) {
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(TRACK_EXTRA) as? Track
        } ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1) Получаем актуальный плейлист из БД и проверяем наличие трека
                val existingPlaylist = playlistInteractor.getPlaylistByIdSync(playlist.id)
                val alreadyExists = existingPlaylist?.tracks?.any { it.trackId == track.trackId } == true

                if (alreadyExists) {
                    Toast.makeText(requireContext(), "Трек уже добавлен в этот плейлист", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 2)
                playlistInteractor.addTrackToPlaylist(playlist.id, track)
                Toast.makeText(requireContext(), "Трек добавлен в ${playlist.name}", Toast.LENGTH_SHORT).show()
                hideBottomSheet()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка добавления трека", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViews(track: Track) {
        binding.toolBarAudioPlayer.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Обработчик клика на кнопку добавления в плейлист
        binding.buttonAddSingle.setOnClickListener {
            showBottomSheet()
        }

        binding.createNewPlaylistButton.setOnClickListener {
            navigateToNewPlaylist()
            hideBottomSheet()
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

    private fun navigateToNewPlaylist() {
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(TRACK_EXTRA) as? Track
        }

        track?.let { currentTrack ->
            val bundle = Bundle().apply {
                putParcelable("track_extra", currentTrack)
            }
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment, bundle)
        }
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

    private fun showBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        playerViewModel.loadPlaylists()
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pausePlayer()
        // Закрываем Bottom Sheet при паузе фрагмента
        if (::bottomSheetBehavior.isInitialized && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            hideBottomSheet()
        }
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