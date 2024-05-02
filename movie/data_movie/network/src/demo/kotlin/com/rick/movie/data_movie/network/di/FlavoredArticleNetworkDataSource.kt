package com.rick.movie.data_movie.network.di

import com.rick.movie.data_movie.network.ArticleNetworkDataSource
import com.rick.movie.data_movie.network.demo.DemoNyTimesNetworkDataSource
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