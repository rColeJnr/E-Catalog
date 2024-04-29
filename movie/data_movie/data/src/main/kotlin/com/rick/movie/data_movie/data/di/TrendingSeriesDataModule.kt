package com.rick.movie.data_movie.data.di

import com.rick.movie.data_movie.data.repository.trending_series.TrendingSeriesByIdRepository
import com.rick.movie.data_movie.data.repository.trending_series.TrendingSeriesByIdRepositoryImpl
import com.rick.movie.data_movie.data.repository.trending_series.TrendingSeriesRecentSearchRepository
import com.rick.movie.data_movie.data.repository.trending_series.TrendingSeriesRecentSearchRepositoryImpl
import com.rick.movie.data_movie.data.repository.trending_series.TrendingSeriesRepository
import com.rick.movie.data_movie.data.repository.trending_series.TrendingSeriesRepositoryImpl
import com.rick.movie.data_movie.data.repository.trending_series.UserTrendingSeriesDataRepository
import com.rick.movie.data_movie.data.repository.trending_series.UserTrendingSeriesDataRepositoryImpl
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

    @Binds
    internal abstract fun bindsTrendingSeriesRecentSearchRepository(
        trendingSeriesRecentSearchRepository: TrendingSeriesRecentSearchRepositoryImpl
    ): TrendingSeriesRecentSearchRepository

    @Binds
    internal abstract fun bindsTrendingSeriesByIdRepository(
        trendingSeriesByIdRepository: TrendingSeriesByIdRepositoryImpl
    ): TrendingSeriesByIdRepository
}