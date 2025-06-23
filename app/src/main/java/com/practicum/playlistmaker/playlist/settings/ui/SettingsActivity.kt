package com.practicum.playlistmaker.playlist.settings.ui

import android.net.Uri
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.playlist.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.playlist.settings.data.datasource.ThemePreferencesDataSource
import com.practicum.playlistmaker.playlist.settings.domain.SettingsInteractor
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()
    private var isSwitchProgrammaticUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataSource = ThemePreferencesDataSource(getSharedPreferences("theme_prefs", MODE_PRIVATE))
        val repository = SettingsRepositoryImpl(dataSource)
        val interactor = SettingsInteractor(repository)

        setupToolbar()
        setupThemeSwitch()
        setupButtons()
        observeThemeState()
    }

    private fun observeThemeState() {
        viewModel.themeState.observe(this) { settingsTheme ->
            AppCompatDelegate.setDefaultNightMode(
                if (settingsTheme.darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            updateToolbarIconColor(settingsTheme.darkThemeEnabled)

            if (!isSwitchProgrammaticUpdate) {
                isSwitchProgrammaticUpdate = true
                binding.switchTheme.isChecked = settingsTheme.darkThemeEnabled
                isSwitchProgrammaticUpdate = false
            }
        }
    }

    private fun setupToolbar() {
        binding.toolBarSettings.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupThemeSwitch() {
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (!isSwitchProgrammaticUpdate) {
                viewModel.toggleTheme()
            }
        }
    }

    private fun updateToolbarIconColor(darkThemeEnabled: Boolean) {
        val color = if (darkThemeEnabled) {
            ContextCompat.getColor(this, R.color.yp_white) // Цвет для темной темы
        } else {
            ContextCompat.getColor(this, R.color.yp_black) // Цвет для светлой темы
        }
        binding.toolBarSettings.navigationIcon?.setTint(color)
    }

    private fun setupButtons() {
        binding.share.setOnClickListener {
            shareApp()
        }
        binding.sendToSupport.setOnClickListener {
            sendToSupport()
        }
        binding.agreement.setOnClickListener {
            openAgreement()
        }
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

        val sendLetter = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        if (sendLetter.resolveActivity(packageManager) != null) {
            startActivity(sendLetter)
        } else {
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(Intent.createChooser(fallbackIntent, getString(R.string.send_email)))
        }
    }

    private fun openAgreement() {
        val url = getString(R.string.offer_address)
        val openUrlAgreement = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(openUrlAgreement)
    }

}
