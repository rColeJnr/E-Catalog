package com.rick.screen_movie

import com.rick.data_movie.MovieCatalogApi
import com.rick.data_movie.MovieCatalogDatabase
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb.IMDBApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DIModule {

    @Provides
    @Singleton
    fun bindMovieCatalogRepository(api: MovieCatalogApi, imdbApi: IMDBApi, db: MovieCatalogDatabase):
            MovieCatalogRepository = MovieCatalogRepository(nyApi = api, imdbApi = imdbApi, db = db)
}