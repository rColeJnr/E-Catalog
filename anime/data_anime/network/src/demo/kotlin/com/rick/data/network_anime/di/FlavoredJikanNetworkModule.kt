package com.rick.data.network_anime.di

import com.rick.data.network_anime.JikanNetworkDataSource
import com.rick.data.network_anime.demo.DemoJikanNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FlavoredJikanNetworkModule {

    @Binds
    fun binds(impl: DemoJikanNetworkDataSource): JikanNetworkDataSource

}