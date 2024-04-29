package com.rick.data.network_movie.di

import com.rick.data.network_movie.TmdbNetworkDataSource
import com.rick.data.network_movie.demo.DemoTmdbNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface FlavoredTmdbNetworkDataSource {


    @Binds
    fun binds(impl: DemoTmdbNetworkDataSource): TmdbNetworkDataSource

}