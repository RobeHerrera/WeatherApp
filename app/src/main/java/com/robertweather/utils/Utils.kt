package com.robertweather.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.robertweather.R


fun checkForInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false

    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}

fun showSnack(view: View, message: String, actionMessage:String? = null, onAction: (()-> Unit)? = null) {
    val snack = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)

    if(actionMessage != null && onAction != null){
        snack.setAction(actionMessage){
            onAction()
            snack.dismiss()
        }
    } else {
        snack.setAction(view.context.getString(R.string.ok)){
            snack.dismiss()
        }
    }
    snack.show()
}

