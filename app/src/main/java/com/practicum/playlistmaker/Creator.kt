package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.domain.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.interactors.HistoryUseCase
import com.practicum.playlistmaker.domain.interactors.SearchTracksUseCase

object Creator {
    private fun provideTrackRepository(): TrackRepositoryImpl {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }
    private fun provideHistoryRepository(context: Context) : HistoryRepositoryImpl {
        return HistoryRepositoryImpl(context)
    }

    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCase(provideTrackRepository())
    }

    fun provideHistoryUseCase(context: Context): HistoryUseCase {
        return HistoryUseCase(provideHistoryRepository(context))
    }


}