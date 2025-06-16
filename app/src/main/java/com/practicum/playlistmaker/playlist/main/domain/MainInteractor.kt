package com.practicum.playlistmaker.playlist.main.domain

class MainInteractor(private val themeRepository: ThemeRepository) {
    fun getCurrentTheme(): Boolean {
        return themeRepository.getDarkTheme()
    }
}