package com.fveloz.redditreader.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import com.fveloz.redditreader.ReaderApplication

class NetworkUtils {
    companion object{
        fun isConnected(context: Context? = null): Boolean {
            val connectivityManager = ReaderApplication.getAppContext().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
}