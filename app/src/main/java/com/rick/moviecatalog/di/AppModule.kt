package com.rick.moviecatalog.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data_movie.Converters
import com.rick.data_movie.MovieCatalogApi
import com.rick.data_movie.MovieCatalogDatabase
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
            .baseUrl(MovieCatalogApi.BASE_URL)
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
            MovieCatalogDatabase.DATABASE_NAME
        ).addTypeConverter(Converters(GsonParser(Gson()))).build()

}