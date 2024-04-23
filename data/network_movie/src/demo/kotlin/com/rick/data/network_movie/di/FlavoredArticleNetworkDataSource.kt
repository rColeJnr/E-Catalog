package com.rick.data.network_movie.di

import com.rick.data.network_movie.ArticleNetworkDataSource
import com.rick.data.network_movie.demo.DemoNyTimesNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FlavoredArticleNetworkDataSource {

    @Binds
    fun binds(impl: DemoNyTimesNetworkDataSource): ArticleNetworkDataSource

}