package com.rick.book.data_book.network

import com.rick.book.data_book.network.demo.DemoGutenbergNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface FlavoredGutenbergNetworkDataSource {

    @Binds
    fun bindsGutenbergNetworkDataSource(impl: DemoGutenbergNetworkDataSource): GutenbergNetworkDataSource

}