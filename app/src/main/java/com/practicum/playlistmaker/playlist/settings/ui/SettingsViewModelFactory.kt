package com.practicum.playlistmaker.playlist.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.playlist.settings.domain.SettingsInteractor

class SettingsViewModelFactory( private val interactor: SettingsInteractor ) : ViewModelProvider.Factory {
    override fun <T :ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(interactor) as T
        }
        throw IllegalStateException("Unknown ViewModel class")
    }
}