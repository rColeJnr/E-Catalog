package com.rick.ecatalogs.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.rick.ecatalogs.data.common.Dispatcher
import com.rick.ecatalogs.data.common.EcsDispatchers.IO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/** [NetworkMonitor] backed by [ConnectivityManager] callbacks (as in Now in Android). */
internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : NetworkMonitor {

    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        if (connectivityManager == null) {
            channel.trySend(false)
            channel.close()
            return@callbackFlow
        }

        // Called for changes to *any* network with internet, not just the active one, so
        // track the set of such networks: online while it isn't empty.
        val callback = object : NetworkCallback() {
            private val networks = mutableSetOf<Network>()

            override fun onAvailable(network: Network) {
                networks += network
                channel.trySend(true)
            }

            override fun onLost(network: Network) {
                networks -= network
                channel.trySend(networks.isNotEmpty())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        // The callback only reports changes: start from the current state
        channel.trySend(connectivityManager.isCurrentlyConnected())

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)
        .conflate()

    private fun ConnectivityManager.isCurrentlyConnected(): Boolean =
        getNetworkCapabilities(activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}
