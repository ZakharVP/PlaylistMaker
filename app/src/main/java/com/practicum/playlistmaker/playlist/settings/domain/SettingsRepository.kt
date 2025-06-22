package com.practicum.playlistmaker.playlist.settings.domain

import com.practicum.playlistmaker.playlist.settings.domain.model.SettingsTheme

interface SettingsRepository {
    fun getSettingsTheme(): SettingsTheme
    fun saveSettingsTheme(settings: SettingsTheme)
}