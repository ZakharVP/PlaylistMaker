package com.practicum.playlistmaker.playlist.settings.data.datasource

import android.content.SharedPreferences

class ThemePreferencesDataSource(private val sharedPreferences: SharedPreferences) {
    fun getTheme(): Boolean = sharedPreferences.getBoolean(THEME_KEY, false)
    fun saveTheme(darkThemeEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, darkThemeEnabled)
            .apply()
    }

    companion object {
        private const val THEME_KEY = "dark_theme_enabled"
    }
}