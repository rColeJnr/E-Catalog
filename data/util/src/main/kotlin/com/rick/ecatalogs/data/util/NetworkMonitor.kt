package com.rick.ecatalogs.data.util

import kotlinx.coroutines.flow.Flow

/** Whether the device can reach the internet, updated as networks come and go. */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
