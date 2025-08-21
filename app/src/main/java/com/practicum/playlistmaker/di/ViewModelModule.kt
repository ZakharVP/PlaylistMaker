package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.playlist.main.ui.MainViewModel
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.FavoritesViewModel
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.PlaylistsViewModel
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.FavoriteViewModel
import com.practicum.playlistmaker.playlist.player.ui.viewmodels.PlayerViewModel
import com.practicum.playlistmaker.playlist.search.ui.viewmodels.SearchViewModel
import com.practicum.playlistmaker.playlist.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SearchViewModel(
        searchUseCase = get(),
        historyUseCase = get(),
        networkChecker = get()
    ) }
    viewModel { PlayerViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { MainViewModel(get()) }

    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { FavoriteViewModel(get()) }
}