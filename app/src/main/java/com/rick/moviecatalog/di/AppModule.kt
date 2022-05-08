package com.rick.moviecatalog.di

import android.content.Context
import androidx.room.Room
import com.rick.moviecatalog.data.local.MovieCatalogDatabase
import com.rick.moviecatalog.data.local.MovieCatalogDatabase.Companion.DATABASE_NAME
import com.rick.moviecatalog.data.remote.MovieCatalogApi
import com.rick.moviecatalog.data.remote.MovieCatalogApi.Companion.BASE_URL
import com.rick.moviecatalog.repository.MovieCatalogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun providesMovieCatalogApi(): MovieCatalogApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }).build()
            )
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun providesMovieDatabase(@ApplicationContext context: Context): MovieCatalogDatabase =
        Room.databaseBuilder(
            context,
            MovieCatalogDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun bindMovieCatalogRepository(api: MovieCatalogApi, db: MovieCatalogDatabase, @ApplicationContext context: Context):
            MovieCatalogRepository = MovieCatalogRepository(api, db, context)
}