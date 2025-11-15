package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private const val THEME_KEY = "dark_theme_enabled"

    private val _themeState = MutableStateFlow(false)
    val themeState: StateFlow<Boolean> = _themeState.asStateFlow()

    fun initTheme(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val darkThemeEnabled = sharedPreferences.getBoolean(THEME_KEY, false)
        _themeState.value = darkThemeEnabled
        applyTheme(darkThemeEnabled)
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        _themeState.value = enabled
        applyTheme(enabled)
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}