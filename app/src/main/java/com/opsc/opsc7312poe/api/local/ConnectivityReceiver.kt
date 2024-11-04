package com.opsc.opsc7312poe.api.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityReceiver  (private val onConnect: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        // Check if there is an active network and it is connected
        if (networkInfo != null && networkInfo.isConnected) {
            onConnect() // Trigger sync when internet is available
        }
    }
}