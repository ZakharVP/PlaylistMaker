package com.practicum.playlistmaker.playlist.search.data.network

import com.practicum.playlistmaker.playlist.search.data.dto.SongResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApplicationApi {
    @GET("search")
    fun search(@Query("term") text: String) : Call<SongResponse>
}