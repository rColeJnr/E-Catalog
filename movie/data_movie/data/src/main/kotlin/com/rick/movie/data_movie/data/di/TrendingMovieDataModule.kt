package com.rick.movie.data_movie.data.di

import com.rick.movie.data_movie.data.repository.trending_movie.TrendingMovieByIdRepository
import com.rick.movie.data_movie.data.repository.trending_movie.TrendingMovieByIdRepositoryImpl
import com.rick.movie.data_movie.data.repository.trending_movie.TrendingMovieRecentSearchRepository
import com.rick.movie.data_movie.data.repository.trending_movie.TrendingMovieRecentSearchRepositoryImpl
import com.rick.movie.data_movie.data.repository.trending_movie.TrendingMovieRepository
import com.rick.movie.data_movie.data.repository.trending_movie.TrendingMovieRepositoryImpl
import com.rick.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class TrendingMovieDataModule {

    @Binds
    internal abstract fun bindsUserTrendingMovieDataRepository(
        userDataRepository: UserTrendingMovieDataRepositoryImpl
    ): UserTrendingMovieDataRepository

    @Binds
    internal abstract fun bindsTrendingMovieRepository(
        trendingMovieRepository: TrendingMovieRepositoryImpl
    ): TrendingMovieRepository

    @Binds
    internal abstract fun bindsTrendingMovieRecentSearchRepository(
        trendingMovieRecentSearchRepository: TrendingMovieRecentSearchRepositoryImpl
    ): TrendingMovieRecentSearchRepository

    @Binds
    internal abstract fun bindsTrendingMovieByIdRepository(
        trendingMovieByIdRepository: TrendingMovieByIdRepositoryImpl
    ): TrendingMovieByIdRepository
}