package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.playlist.search.domain.useCases.HistoryUseCase
import com.practicum.playlistmaker.playlist.search.domain.useCases.SearchTracksUseCase
import com.practicum.playlistmaker.playlist.settings.domain.SettingsInteractor
import org.koin.dsl.module

val interactorModule = module {

    factory<SearchTracksUseCase> {
        SearchTracksUseCase(
            repository = get(),
            networkChecker = get()
        )
    }

    factory<HistoryUseCase> {
        HistoryUseCase(
            repository = get()
        )
    }

    factory { SettingsInteractor(get()) }

}