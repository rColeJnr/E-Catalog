package com.rick.ecatalogs.data.util.di

import com.rick.ecatalogs.data.util.ConnectivityManagerNetworkMonitor
import com.rick.ecatalogs.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkMonitorModule {

    @Binds
    abstract fun bindsNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor
}
