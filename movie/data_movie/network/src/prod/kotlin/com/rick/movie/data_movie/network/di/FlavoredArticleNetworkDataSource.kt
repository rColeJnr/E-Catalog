package com.rick.movie.data_movie.network.di

import com.rick.movie.data_movie.network.ArticleNetworkDataSource
import com.rick.movie.data_movie.network.retrofit.NyTimesRetrofitNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredArticleNetworkDataSource {

    @Binds
    fun providesArticleNetworkDataSource(impl: NyTimesRetrofitNetwork): ArticleNetworkDataSource

}