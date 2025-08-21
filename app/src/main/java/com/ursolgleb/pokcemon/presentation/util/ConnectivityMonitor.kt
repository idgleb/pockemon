package com.ursolgleb.pokcemon.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class ConnectivityMonitor(
	private val context: Context,
	private val onStatusChanged: (Boolean) -> Unit
) {

	private var connectivityManager: ConnectivityManager? = null
	private var networkCallback: ConnectivityManager.NetworkCallback? = null

	fun register() {
		connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
		val cm = connectivityManager ?: return
		val request = NetworkRequest.Builder()
			.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
			.build()
		networkCallback = object : ConnectivityManager.NetworkCallback() {
			override fun onAvailable(network: Network) {
				onStatusChanged(true)
			}
			override fun onLost(network: Network) {
				onStatusChanged(false)
			}
		}
		cm.registerNetworkCallback(request, networkCallback!!)
	}

	fun unregister() {
		try {
			connectivityManager?.unregisterNetworkCallback(networkCallback ?: return)
		} catch (_: Exception) { }
	}

	fun isNetworkAvailable(): Boolean {
		val cm = connectivityManager ?: return false
		val network = cm.activeNetwork ?: return false
		val caps = cm.getNetworkCapabilities(network) ?: return false
		return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
	}
}


