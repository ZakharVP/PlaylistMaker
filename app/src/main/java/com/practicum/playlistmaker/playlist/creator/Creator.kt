package com.practicum.playlistmaker.playlist.creator

import android.content.Context
import com.practicum.playlistmaker.playlist.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.playlist.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.playlist.search.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.playlist.search.domain.repository.TrackRepository
import com.practicum.playlistmaker.playlist.search.domain.useCases.SearchTracksUseCase
import com.practicum.playlistmaker.playlist.search.domain.useCases.HistoryUseCase
import com.practicum.playlistmaker.util.AndroidNetworkChecker
import com.practicum.playlistmaker.util.NetworkChecker

object Creator {

    fun provideNetworkChecker(context: Context): NetworkChecker {
        return AndroidNetworkChecker(context)
    }

    fun provideTrackRepository(applicationContext: Context): TrackRepositoryImpl {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }
    private fun provideHistoryRepository(context: Context) : HistoryRepositoryImpl {
        return HistoryRepositoryImpl(context)
    }

    fun provideSearchTracksUseCase(
        repository: TrackRepository,
        networkChecker: NetworkChecker
    ): SearchTracksUseCase {
        return SearchTracksUseCase(repository, networkChecker)
    }

    fun provideHistoryUseCase(context: Context): HistoryUseCase {
        return HistoryUseCase(provideHistoryRepository(context))
    }
}