package com.rick.ecatalogs.movie.data_movie.data.di

import com.rick.ecatalogs.movie.data_movie.data.repository.trending_series.CompositeTrendingSeriesRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_series.UserTrendingSeriesRepository
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