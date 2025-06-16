package com.practicum.playlistmaker.playlist.settings.domain

import com.practicum.playlistmaker.playlist.settings.domain.model.SettingsTheme

class SettingsInteractor(private val repository: SettingsRepository) {
    fun getSettingsTheme(): SettingsTheme = repository.getSettingsTheme()
    fun saveSettingsTheme(settings: SettingsTheme)  = repository.saveSettingsTheme(settings)
}