package com.practicum.playlistmaker.playlist.settings.data

import com.practicum.playlistmaker.playlist.settings.data.datasource.ThemePreferencesDataSource
import com.practicum.playlistmaker.playlist.settings.domain.SettingsRepository
import com.practicum.playlistmaker.playlist.settings.domain.model.SettingsTheme

class SettingsRepositoryImpl(private val dataSource: ThemePreferencesDataSource): SettingsRepository {
    override fun getSettingsTheme(): SettingsTheme {
        return SettingsTheme(dataSource.getTheme())
    }

    override fun saveSettingsTheme(settings: SettingsTheme) {
        dataSource.saveTheme(settings.darkThemeEnabled)
    }
}

