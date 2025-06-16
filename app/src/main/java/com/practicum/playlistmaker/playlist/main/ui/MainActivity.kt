package com.practicum.playlistmaker.playlist.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.distinctUntilChanged
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.playlist.main.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.playlist.main.domain.MainInteractor
import com.practicum.playlistmaker.playlist.mediateka.ui.views.MediatekaActivity
import com.practicum.playlistmaker.playlist.search.ui.views.FindActivity
import com.practicum.playlistmaker.playlist.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        val sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val themeRepository = ThemeRepositoryImpl(sharedPreferences)
        val interactor = MainInteractor(themeRepository)
        MainViewModel(interactor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyThemeFromPreferences()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupWindowInsets()
        setupObserverTheme()
    }

    override fun onResume() {
        super.onResume()
        applyThemeFromPreferences() // Обновляем тему при возврате на экран
    }

    private fun applyThemeFromPreferences() {
        val darkThemeEnabled = viewModel.currentTheme.value ?: false
        val newNightMode = if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(newNightMode)
    }

    private fun setupObserverTheme() {
        viewModel.currentTheme
            .distinctUntilChanged()
            .observe(this) { darkThemeEnabled ->
                applyThemeFromPreferences()
            }
    }

    private fun setupNavigation() {
        binding.bSearch.setOnClickListener {
            startActivity(Intent(this, FindActivity::class.java))
        }
        binding.bMedia.setOnClickListener {
            startActivity(Intent(this, MediatekaActivity::class.java))
        }
        binding.bSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}