package com.rick.book.data_book.network.di

import com.rick.book.data_book.network.BestsellerNetworkDataSource
import com.rick.book.data_book.network.retrofit.BestsellerRetrofitNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredBestsellerNetworkModule {

    @Binds
    fun bindsBestsellerNetworkDataSource(impl: BestsellerRetrofitNetwork): BestsellerNetworkDataSource

}