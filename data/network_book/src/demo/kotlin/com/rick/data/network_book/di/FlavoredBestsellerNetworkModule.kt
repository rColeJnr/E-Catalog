package com.rick.data.network_book.di

import com.rick.data.network_book.BestsellerNetworkDataSource
import com.rick.data.network_book.demo.DemoBestsellerNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface FlavoredBestsellerNetworkModule {

    @Binds
    fun binds(impl: DemoBestsellerNetworkDataSource): BestsellerNetworkDataSource

}