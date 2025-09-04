package com.practicum.playlistmaker.playlist.mediateka.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.NewPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()

    private var hasUnsavedChanges: Boolean = false
    private var isCreating: Boolean = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setCoverUri(uri)
                loadImage(uri)
                hasUnsavedChanges = true // ДОБАВЛЕНО: отмечаем изменения
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        } else {
            Toast.makeText(requireContext(), "Разрешение необходимо для выбора обложки", Toast.LENGTH_SHORT).show()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (hasUnsavedChanges && !isCreating) {
                showExitConfirmationDialog()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("PlaylistsFragment", "onViewCreated called")
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        setupListeners()
        setupTextWatcher()
        observeViewModel()
        updateButtonState()

        val initialName = binding.nameEditText.text.toString().trim()
        val initialDescription = binding.descriptionEditText.text.toString().trim()
        android.util.Log.d("NewPlaylistFragment", "Initial: name='$initialName', description='$initialDescription'")
    }

    private fun setupListeners() {
        // Кнопка назад
        binding.backButton.setOnClickListener {
            if (hasUnsavedChanges && !isCreating) {
                showExitConfirmationDialog()
            } else {
                findNavController().navigateUp()
            }
        }

        // Выбор обложки при нажатии на изображение
        binding.coverImageView.setOnClickListener {
            pickImageFromGallery()
        }

        // Создание плейлиста
        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val description = binding.descriptionEditText.text.toString().trim()

            if (name.isNotEmpty()) {
                isCreating = true
                viewModel.createPlaylist(name, description)
            } else {
                binding.nameEditText.error = "Введите название плейлиста"
            }
        }
    }

    private fun setupTextWatcher() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hasUnsavedChanges = true
            }
            override fun afterTextChanged(s: Editable?) {
                updateButtonState()
                if (s?.isNotEmpty() == true) {
                    binding.nameInputLayout.error = null
                }
            }
        }

        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.descriptionEditText.addTextChangedListener(textWatcher)
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Завершить") { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun updateButtonState() {
        val text = binding.nameEditText.text.toString().trim()
        val isEmpty = text.isEmpty()

        binding.saveButton.isEnabled = !isEmpty

        val colorRes = if (isEmpty) R.color.yp_gray else R.color.yp_blue
        binding.saveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.createPlaylistState.collect { state ->
                when (state) {
                    is NewPlaylistViewModel.CreatePlaylistState.Loading -> {
                        binding.saveButton.isEnabled = false
                        binding.saveButton.text = "Создание..."
                    }
                    is NewPlaylistViewModel.CreatePlaylistState.Success -> {
                        val name = binding.nameEditText.text.toString().trim()

                        Toast.makeText(
                            requireContext(),
                            "Плейлист \"$name\" создан!",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                    }
                    is NewPlaylistViewModel.CreatePlaylistState.Error -> {
                        isCreating = false
                        binding.saveButton.isEnabled = true
                        binding.saveButton.text = "Создать"
                        Toast.makeText(requireContext(), "Ошибка: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        // Проверяем разрешение для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                launchImagePicker()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Для старых версий
            launchImagePicker()
        }
    }

    private fun launchImagePicker() {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            val chooserIntent = Intent.createChooser(intent, "Выберите обложку для плейлиста")
            pickImageLauncher.launch(chooserIntent)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Не удалось открыть галерею", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun loadImage(uri: Uri) {
        Glide.with(requireContext())
            .load(uri)
            .placeholder(R.drawable.no_image_placeholder)
            .into(binding.coverImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}