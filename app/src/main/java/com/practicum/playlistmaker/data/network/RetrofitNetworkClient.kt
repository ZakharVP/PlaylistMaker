package com.practicum.playlistmaker.data.network

import android.util.Log
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.SongRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {

    private val imdbBaseUrl = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(ItunesApplicationApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is SongRequest) {
            val resp = imdbService.search(dto.expression).execute()
            val body = resp.body() ?: Response()
            Log.d("RetrofitNetworkClient", "Response code: ${resp.code()}")
            return body.apply { resultCode = resp.code() }
        } else {
            Log.d("RetrofitNetworkClient", "Bad request, code: 400")
            return Response().apply { resultCode = 400 }
        }
    }

}