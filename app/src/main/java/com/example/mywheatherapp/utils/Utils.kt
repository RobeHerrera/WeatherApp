package com.example.mywheatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
fun checkForInternet(context: Context): Boolean {
    // registrar la actividad con el servicio connectivity Manager
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // si la version de Android es M o mayor se usa NetworkCapabilities para verificar
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        // Devuelve un objeto de tipo Network corespodiente a la conectividad del dispositivo
        val network = connectivityManager.activeNetwork?: return false

        // Representacion of the capabilities of an active netwok.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when{
            // Indica si la red usa Ttranspote WiFi o tiene conectividad WiFi
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            // Indica si la red tiene conectividad por datos moviles
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }else{
        // Si la version de Ansroid es menor a M
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }

}