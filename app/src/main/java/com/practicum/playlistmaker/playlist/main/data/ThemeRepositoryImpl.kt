package com.practicum.playlistmaker.playlist.main.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.playlist.main.domain.ThemeRepository

class ThemeRepositoryImpl( private val sharedPreferences: SharedPreferences ) : ThemeRepository {
    override fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("dark_theme_enabled", false)
    }
}