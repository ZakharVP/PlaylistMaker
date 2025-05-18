package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.domain.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.interactors.HistoryUseCase
import com.practicum.playlistmaker.domain.interactors.SearchTracksUseCase
import com.practicum.playlistmaker.domain.repositary.HistoryRepository
import com.practicum.playlistmaker.domain.repositary.TrackRepository

object Creator {

    // Для TrackRepository
    fun provideTrackRepository(networkClient: NetworkClient = provideNetworkClient()): TrackRepository {
        return TrackRepositoryImpl( networkClient )
    }

    //Для HistoryRepository
    fun provideHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(context)
    }

    //UseCases они же итеракторы
    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCase(provideTrackRepository())
    }
    fun provideHistoryUseCase(context: Context): HistoryUseCase {
        return HistoryUseCase(provideHistoryRepository(context))
    }

    //Общие зависимости
    fun provideNetworkClient(): NetworkClient = RetrofitNetworkClient()

}