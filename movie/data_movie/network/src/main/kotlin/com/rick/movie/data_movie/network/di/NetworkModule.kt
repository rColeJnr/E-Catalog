package com.rick.movie.data_movie.network.di

import android.content.Context
import com.rick.movie.data_movie.network.demo.DemoMovieAssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
//    @Provides
//    @Singleton
//    fun providesNetworkMovieJson(): Json = Json {
//        ignoreUnknownKeys = true
//    }

    @Provides
    @Singleton
    fun providesDemoAssetManager(
        @ApplicationContext context: Context
    ): DemoMovieAssetManager = DemoMovieAssetManager(context.assets::open)

}