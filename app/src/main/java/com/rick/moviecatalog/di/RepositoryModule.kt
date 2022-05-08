package com.rick.moviecatalog.di

import com.rick.moviecatalog.repository.MovieCatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieCatalogRepository():
            MovieCatalogRepository
}