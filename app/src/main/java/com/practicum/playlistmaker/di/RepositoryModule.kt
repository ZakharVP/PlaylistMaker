package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.playlist.main.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.playlist.main.domain.ThemeRepository
import com.practicum.playlistmaker.playlist.mediateka.data.repository.FavoritesRepositoryImpl
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.playlist.mediateka.data.repository.PlaylistRepositoryImpl
import com.practicum.playlistmaker.playlist.mediateka.domain.repository.FavoritesRepository
import com.practicum.playlistmaker.playlist.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.playlist.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.playlist.player.domain.useCases.FavoritesUseCase
import com.practicum.playlistmaker.playlist.search.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.playlist.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.playlist.search.data.sharedprefs.SearchHistoryStorage
import com.practicum.playlistmaker.playlist.search.domain.repository.HistoryRepository
import com.practicum.playlistmaker.playlist.search.domain.repository.TrackRepository
import com.practicum.playlistmaker.playlist.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.playlist.settings.domain.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    factory {
        MediaPlayer()
    }
    factory {
        SearchHistoryStorage(
            context = androidContext(),
            gson = get()
        )
    }
    factory<HistoryRepository> {
        HistoryRepositoryImpl(
            searchHistoryStorage = get(),
            gson = get()
        )
    }
    factory<TrackRepository> {
        TrackRepositoryImpl(
            networkClient = get()
        )
    }
    single<ThemeRepository> {
        ThemeRepositoryImpl( get() )
    }
    factory<PlayerRepository> {
        PlayerRepositoryImpl(
            mediaPlayer = get()
        )
    }
    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(
            trackDao = get()
        )
    }
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            playlistDao = get(),
            playlistTrackDao = get(),
            gson = get()
        )
    }
    single {
        FavoritesUseCase(get())
    }
}