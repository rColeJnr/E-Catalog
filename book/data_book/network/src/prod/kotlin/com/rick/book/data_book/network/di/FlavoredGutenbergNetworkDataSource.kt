package com.rick.book.data_book.network.di

import com.rick.book.data_book.network.GutenbergNetworkDataSource
import com.rick.book.data_book.network.retrofit.GutenbergRetrofitNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredGutenbergNetworkDataSource {

    @Binds
    fun bindsGutenbergNetworkDataSource(impl: GutenbergRetrofitNetwork): GutenbergNetworkDataSource

}