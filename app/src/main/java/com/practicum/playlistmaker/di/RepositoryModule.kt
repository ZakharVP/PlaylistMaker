package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.playlist.main.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.playlist.main.domain.ThemeRepository
import com.practicum.playlistmaker.playlist.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.playlist.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.playlist.search.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.playlist.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.playlist.search.domain.repository.HistoryRepository
import com.practicum.playlistmaker.playlist.search.domain.repository.TrackRepository
import com.practicum.playlistmaker.playlist.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.playlist.settings.domain.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<HistoryRepository> {
        HistoryRepositoryImpl(
            context = androidContext()
        )
    }
    single<TrackRepository> {
        TrackRepositoryImpl(
            networkClient = get()
        )
    }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<PlayerRepository> { PlayerRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}