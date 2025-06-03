package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repositary.impl.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.repositary.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.interfaces.HistoryUseCase
import com.practicum.playlistmaker.domain.usecase.SearchTracksUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.HistoryUseCaseImpl
import com.practicum.playlistmaker.domain.repositories.TrackRepositoryInter

object Creator {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    //Общие зависимости
    fun provideNetworkClient(): NetworkClient = RetrofitNetworkClient()

    // Для TrackRepository
    fun provideTrackRepository(networkClient: NetworkClient = provideNetworkClient()): TrackRepositoryInter {
        return TrackRepositoryImpl(networkClient)
    }

    //Для HistoryRepository
    fun provideHistoryRepository(): HistoryRepositoryImpl {
        return HistoryRepositoryImpl(appContext)
    }

    //UseCase
    fun provideSearchTracksUseCase(trackRepository: TrackRepositoryInter = provideTrackRepository()): SearchTracksUseCaseImpl {
        return SearchTracksUseCaseImpl(trackRepository)
    }
    fun provideHistoryUseCase(historyRepository: HistoryRepositoryImpl = provideHistoryRepository()): HistoryUseCase {
        return HistoryUseCaseImpl(historyRepository)
    }



}