package com.rick.data.movie_favorite.di

import com.rick.data.movie_favorite.repository.trending_series.CompositeTrendingSeriesRepository
import com.rick.data.movie_favorite.repository.trending_series.UserTrendingSeriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UserTrendingSeriesCompositeRepository {

    @Binds
    fun bindsUserTrendingSeriesRepository(
        compositeRepository: CompositeTrendingSeriesRepository
    ): UserTrendingSeriesRepository

}