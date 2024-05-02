package com.rick.movie.data_movie.network.di

import com.rick.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.movie.data_movie.network.demo.DemoTmdbNetworkDataSource
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