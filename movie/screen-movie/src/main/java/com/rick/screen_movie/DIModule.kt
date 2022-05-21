package com.rick.screen_movie

import android.content.Context
import com.rick.data_movie.MovieCatalogApi
import com.rick.data_movie.MovieCatalogDatabase
import com.rick.data_movie.MovieCatalogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DIModule {

    @Provides
    @Singleton
    fun bindMovieCatalogRepository(api: MovieCatalogApi, db: MovieCatalogDatabase, @ApplicationContext context: Context):
            MovieCatalogRepository = MovieCatalogRepository(api, db)
}