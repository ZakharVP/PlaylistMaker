package com.practicum.playlistmaker.playlist.mediateka.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.PlaylistDetailViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.playlist.mediateka.ui.adapters.PlaylistTracksAdapter

class PlaylistDetailFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistDetailViewModel by viewModel()
    private lateinit var tracksAdapter: PlaylistTracksAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<com.google.android.material.card.MaterialCardView>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<com.google.android.material.card.MaterialCardView>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getLong("playlistId") ?: -1L
        if (playlistId != -1L) {
            viewModel.loadPlaylistData(playlistId)
        }

        setupBackButton()
        setupBottomSheet()
        setupMenuBottomSheet()
        setupRecyclerView()
        setupButtonListeners()
        observeViewModel()

        binding.dimBackground.bringToFront()
        binding.menuSheet.bringToFront()
        binding.backButton.bringToFront()
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuSheet)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        menuBottomSheetBehavior.isHideable = true

        // Устанавливаем высоту после того как view будет измерено
        binding.menuSheet.post {
            if (_binding == null) return@post
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            // Устанавливаем максимальную высоту на 40% экрана
            menuBottomSheetBehavior.maxHeight = (screenHeight * 0.4).toInt()
            // Убираем peekHeight, чтобы меню открывалось полностью
            menuBottomSheetBehavior.peekHeight = 0
        }

        // Настройка затемнения фона
        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (_binding == null) return
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.dimBackground.visibility = View.VISIBLE
                    binding.menuSheet.visibility = View.VISIBLE
                    // Скрываем основной Bottom Sheet с треками
                    binding.bottomSheet.visibility = View.GONE
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.dimBackground.visibility = View.GONE
                    binding.menuSheet.visibility = View.GONE
                    // Показываем основной Bottom Sheet с треками, если они есть
                    if (viewModel.tracks.value.isNotEmpty()) {
                        binding.bottomSheet.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (_binding == null) return
                binding.dimBackground.alpha = slideOffset
            }
        })

        // Обработчики кликов для меню
        binding.shareOption.setOnClickListener {
            sharePlaylist()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.editOption.setOnClickListener {
            viewModel.playlist.value?.let { playlist ->
                val bundle = Bundle().apply {
                    putLong("playlistId", playlist.id)
                }
                findNavController().navigate(
                    R.id.action_playlistDetailFragment_to_newPlaylistFragment,
                    bundle
                )
            }
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.deleteOption.setOnClickListener {
            showDeletePlaylistDialog()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // Закрытие меню при клике на затемненную область
        binding.dimBackground.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupButtonListeners() {
        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.menuButton.setOnClickListener {
            showMenu()
        }
    }

    private fun showMenu() {
        // Обновляем информацию в меню
        viewModel.playlist.value?.let { playlist ->
            binding.playlistTitleMenu.text = playlist.name
            binding.playlistInfoMenu.text = viewModel.getPlaylistInfoText()

            // Загружаем изображение плейлиста
            if (playlist.coverUri != null && playlist.coverUri.isNotBlank()) {
                Glide.with(requireContext())
                    .load(playlist.coverUri)
                    .into(binding.playlistImageMenu)
            } else {
                binding.playlistImageMenu.setImageResource(R.drawable.no_image_placeholder)
            }
        }

        // Показываем меню перед открытием
        binding.menuSheet.visibility = View.VISIBLE
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun sharePlaylist() {
        val sharingText = viewModel.getPlaylistForSharing()

        if (sharingText.isEmpty()) {
            // Показываем сообщение, если нет треков
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.share_error_title))
                .setMessage(getString(R.string.share_error_message))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            // Отправляем через Intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, sharingText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_playlist)))
        }
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_title))
            .setMessage(getString(R.string.delete_playlist_message))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                deletePlaylist()
                dialog.dismiss()
            }
            .show()
    }

    private fun deletePlaylist() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deletePlaylist()
            findNavController().navigateUp()
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isHideable = false

        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        bottomSheetBehavior.peekHeight = (screenHeight * 0.4).toInt()
    }

    private fun setupRecyclerView() {
        tracksAdapter = PlaylistTracksAdapter(
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_playlistDetailFragment_to_playerFragment,
                    bundleOf("track_extra" to track)
                )
            },
            onTrackLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )

        binding.tracksRecyclerView.apply {
            adapter = tracksAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showDeleteTrackDialog(track: com.practicum.playlistmaker.playlist.sharing.data.models.Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_title))
            .setMessage(getString(R.string.delete_track_message, track.trackName))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.removeTrack(track.trackId)
                dialog.dismiss()
            }
            .show()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlist.collect { playlist ->
                    playlist?.let { updateUI(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalDuration.collect { duration ->
                    binding.totalDurationText.text = duration
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracks.collect { tracks ->
                    tracksAdapter.submitList(tracks)
                    if (tracks.isNotEmpty()) {
                        binding.bottomSheet.visibility = View.VISIBLE
                    } else {
                        binding.bottomSheet.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun updateUI(playlist: com.practicum.playlistmaker.playlist.sharing.data.models.Playlist) {
        binding.playlistTitle.text = playlist.name ?: ""

        if (playlist.coverUri != null && playlist.coverUri.isNotBlank()) {
            Glide.with(requireContext())
                .load(playlist.coverUri)
                .into(binding.playlistCover)
        } else {
            binding.playlistCover.setImageResource(R.drawable.no_image_placeholder)
        }

        playlist.description?.let { description ->
            if (description.isNotBlank()) {
                binding.playlistDescription.text = description
                binding.playlistDescription.visibility = View.VISIBLE
            } else {
                binding.playlistDescription.visibility = View.GONE
            }
        } ?: run {
            binding.playlistDescription.visibility = View.GONE
        }

        val trackCount = playlist.tracksCount
        val trackCountText = when {
            trackCount % 10 == 1 && trackCount % 100 != 11 -> "$trackCount трек"
            trackCount % 10 in 2..4 && trackCount % 100 !in 12..14 -> "$trackCount трека"
            else -> "$trackCount треков"
        }
        binding.trackCountText.text = trackCountText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}