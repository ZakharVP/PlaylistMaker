package com.practicum.playlistmaker.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

@SuppressLint("MissingPermission")
class AndroidNetworkChecker(
    private val context: Context
) : NetworkChecker {

    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return try {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            // Проверяем не только наличие соединения, но и доступность интернета
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            Log.e("NetworkChecker", "Error checking network", e)
            false
        }
    }
}