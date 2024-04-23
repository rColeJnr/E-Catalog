package com.rick.data.movie_favorite.di

import com.rick.data.movie_favorite.repository.trending_movie.TrendingMovieRepository
import com.rick.data.movie_favorite.repository.trending_movie.TrendingMovieRepositoryImpl
import com.rick.data.movie_favorite.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.data.movie_favorite.repository.trending_movie.UserTrendingMovieDataRepositoryImpl
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

}