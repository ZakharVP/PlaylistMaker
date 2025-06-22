package com.practicum.playlistmaker.playlist.search.data.network

import com.practicum.playlistmaker.playlist.search.data.dto.Response
import com.practicum.playlistmaker.playlist.search.data.dto.SongRequest

interface NetworkClient {
    fun doRequest(dto: Any): Response
}