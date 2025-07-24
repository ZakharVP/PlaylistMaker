package com.practicum.playlistmaker.playlist.search.data.network

import android.util.Log
import com.practicum.playlistmaker.playlist.search.data.dto.Response
import com.practicum.playlistmaker.playlist.search.data.dto.SongRequest
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient{

    private val imdbBaseUrl = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(ItunesApplicationApi::class.java)

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is SongRequest) {
            try {
                val resp = imdbService.search(dto.expression).awaitResponse()
                val body = resp.body() ?: Response()
                Log.d("RetrofitNetworkClient", "Response code: ${resp.code()}")
                return body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                Response().apply { resultCode = 500 }
            }
        } else {
            Log.d("RetrofitNetworkClient", "Bad request, code: 400")
            return Response().apply { resultCode = 400 }
        }
    }
}