package com.rick.data.movie_favorite.di

import com.rick.data.movie_favorite.repository.trending_movie.CompositeTrendingMovieRepository
import com.rick.data.movie_favorite.repository.trending_movie.UserTrendingMovieRepository
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