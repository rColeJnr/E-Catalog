package com.rick.ecatalogs.data.testing.util

import com.rick.ecatalogs.data.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** A [NetworkMonitor] a test switches on and off with [setConnected]; online to start. */
class TestNetworkMonitor : NetworkMonitor {

    private val connectivityFlow = MutableStateFlow(true)

    override val isOnline: Flow<Boolean> = connectivityFlow

    fun setConnected(isConnected: Boolean) {
        connectivityFlow.value = isConnected
    }
}
