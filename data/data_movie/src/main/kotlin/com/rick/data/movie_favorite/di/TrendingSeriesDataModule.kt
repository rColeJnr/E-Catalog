package com.rick.data.movie_favorite.di

import com.rick.data.movie_favorite.repository.trending_series.TrendingSeriesRepository
import com.rick.data.movie_favorite.repository.trending_series.TrendingSeriesRepositoryImpl
import com.rick.data.movie_favorite.repository.trending_series.UserTrendingSeriesDataRepository
import com.rick.data.movie_favorite.repository.trending_series.UserTrendingSeriesDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TrendingSeriesDataModule {

    @Binds
    internal abstract fun bindsUserTrendingSeriesDataRepository(
        userDataRepository: UserTrendingSeriesDataRepositoryImpl
    ): UserTrendingSeriesDataRepository

    @Binds
    internal abstract fun bindsTrendingSeriesRepository(
        trendingSeriesRepository: TrendingSeriesRepositoryImpl
    ): TrendingSeriesRepository

}