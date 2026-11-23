package com.rick.ecatalogs.anime.data_anime.network.di

import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.demo.DemoJikanNetworkDataSource
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