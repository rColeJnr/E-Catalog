package com.rick.data.database_movie.di

import com.rick.data.database_movie.MovieDatabase
import com.rick.data.database_movie.dao.ArticleDao
import com.rick.data.database_movie.dao.ArticleRemoteKeysDao
import com.rick.data.database_movie.dao.TrendingMovieDao
import com.rick.data.database_movie.dao.TrendingMovieRemoteKeysDao
import com.rick.data.database_movie.dao.TrendingSeriesDao
import com.rick.data.database_movie.dao.TrendingSeriesRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesArticleDao(db: MovieDatabase): ArticleDao =
        db.articleDao

    @Provides
    fun provideArticleRemoteKeysDao(db: MovieDatabase): ArticleRemoteKeysDao =
        db.articleRemoteKeysDao

    @Provides
    fun provideTrendingMovieDao(db: MovieDatabase): TrendingMovieDao =
        db.trendingMovieDao

    @Provides
    fun provideTrendingMovieRemoteKeys(db: MovieDatabase): TrendingMovieRemoteKeysDao =
        db.trendingMovieRemoteKeysDao

    @Provides
    fun provideTrendingSeriesDao(db: MovieDatabase): TrendingSeriesDao =
        db.trendingSeriesDao

    @Provides
    fun provideTrendingSeriesRemoteKeysDao(db: MovieDatabase): TrendingSeriesRemoteKeysDao =
        db.trendingSeriesRemoteKeysDao

}