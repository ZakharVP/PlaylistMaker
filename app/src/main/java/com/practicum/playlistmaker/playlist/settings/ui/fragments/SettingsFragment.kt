package com.practicum.playlistmaker.playlist.settings.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.playlist.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()
    private var isInitialLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupThemeSwitch()
        setupButtons()
        observeThemeState()

    }

    private fun observeThemeState() {
        viewModel.themeState.observe(viewLifecycleOwner) { settingsTheme ->
            if (isInitialLoad) {
                isInitialLoad = false
                // Только устанавливаем начальное состояние без вызова setDefaultNightMode
                binding.switchTheme.isChecked = settingsTheme.darkThemeEnabled
                return@observe
            }

            Log.d("THEME_DEBUG", "Applying theme: ${settingsTheme.darkThemeEnabled}")
            AppCompatDelegate.setDefaultNightMode(
                if (settingsTheme.darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            updateToolbarIconColor(settingsTheme.darkThemeEnabled)
        }
    }

    private fun setupToolbar() {
        binding.toolBarSettings.setNavigationOnClickListener {
            parentFragmentManager.popBackStack() // Закрываем фрагмент
        }
    }

    private fun setupThemeSwitch() {
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            // Проверяем, что изменение действительно от пользователя
            if (binding.switchTheme.isPressed) {
                Log.d("THEME_DEBUG", "User toggled switch to: $isChecked")
                viewModel.toggleTheme()
            }
        }
    }

    private fun updateToolbarIconColor(darkThemeEnabled: Boolean) {
        val color = if (darkThemeEnabled) {
            ContextCompat.getColor(requireContext(), R.color.yp_white)
        } else {
            ContextCompat.getColor(requireContext(), R.color.yp_black)
        }
        binding.toolBarSettings.navigationIcon?.setTint(color)
    }

    private fun setupButtons() {
        binding.share.setOnClickListener { shareApp() }
        binding.sendToSupport.setOnClickListener { sendToSupport() }
        binding.agreement.setOnClickListener { openAgreement() }
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.title_share))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.title_share)))
    }

    private fun sendToSupport() {
        val email = getString(R.string.mail_address)
        val subject = getString(R.string.mail_theme)
        val body = getString(R.string.mail_body)

        Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }.let { intent ->
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                }.also { fallbackIntent ->
                    startActivity(Intent.createChooser(fallbackIntent, getString(R.string.send_email)))
                }
            }
        }
    }

    private fun openAgreement() {
        val url = getString(R.string.offer_address)
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }.also { startActivity(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}