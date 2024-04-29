package com.rick.movie.data_movie.data.di

import com.rick.movie.data_movie.data.repository.trending_movie.CompositeTrendingMovieRepository
import com.rick.movie.data_movie.data.repository.trending_movie.UserTrendingMovieRepository
import dagger.Binds


import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UserTrendingMovieCompositeRepository {

    @Binds
    fun bindsUserTrendingMovieRepository(
        compositeRepository: CompositeTrendingMovieRepository
    ): UserTrendingMovieRepository

}