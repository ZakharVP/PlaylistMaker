package com.practicum.playlistmaker.playlist.settings.data.datasource

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.ThemeManager

class ThemePreferencesDataSource(private val sharedPreferences: SharedPreferences) {
    fun getTheme(): Boolean = sharedPreferences.getBoolean(THEME_KEY, false)

    fun saveTheme(darkThemeEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, darkThemeEnabled)
            .apply()
        ThemeManager.setDarkThemeEnabled(darkThemeEnabled)
    }

    companion object {
        private const val THEME_KEY = "dark_theme_enabled"
    }
}