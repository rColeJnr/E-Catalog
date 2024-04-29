package com.rick.data.network_book.di

import com.rick.data.network_book.GutenbergNetworkDataSource
import com.rick.data.network_book.demo.DemoGutenbergNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface FlavoredGutenbergNetworkDataSource {

    @Binds
    fun binds(impl: DemoGutenbergNetworkDataSource): GutenbergNetworkDataSource

}