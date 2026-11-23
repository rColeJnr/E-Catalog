package com.rick.ecatalogs.anime.data_anime.network.di

import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.retrofit.JikanRetrofitNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredJikanNetworkModule {

    @Binds
    fun binds(impl: JikanRetrofitNetwork): JikanNetworkDataSource

}