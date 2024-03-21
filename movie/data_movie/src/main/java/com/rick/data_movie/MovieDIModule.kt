package com.rick.data_movie

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data_movie.ny_times.NYMovieApi
import com.rick.data_movie.ny_times.article_models.Converters
import com.rick.data_movie.tmdb.TMDBApi
import com.rick.data_movie.tmdb.TMDBConverters
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
object MovieDIModule {
    @Provides
    @Singleton
    fun providesMovieCatalogApi(): NYMovieApi {
        return Retrofit.Builder()
            .baseUrl(NYMovieApi.NY_TIMES_BASE_URL)
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
    fun providesTMDBApi(): TMDBApi {
        return Retrofit.Builder()
            .baseUrl(TMDBApi.TMDB_BASE_URL)
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
        ).addTypeConverter(Converters(GsonParser(Gson())))
            .addTypeConverter(TMDBConverters(GsonParser(Gson())))
            .build()

    @Provides
    @Singleton
    fun bindMovieCatalogRepository(api: NYMovieApi, tmdbApi: TMDBApi, db: MovieCatalogDatabase):
            MovieCatalogRepository = MovieCatalogRepository(nyApi = api, tmdbApi = tmdbApi, db = db)
}