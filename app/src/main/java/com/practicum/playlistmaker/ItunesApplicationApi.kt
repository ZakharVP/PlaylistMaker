package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApplicationApi {
    @GET("search")
    fun search(@Query("term") text: String) : Call<SongResponse>
}