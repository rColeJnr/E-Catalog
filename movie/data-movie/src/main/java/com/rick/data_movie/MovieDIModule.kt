package com.rick.data_movie

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data_movie.MovieCatalogDatabase.Companion.MIGRATION_1_2
import com.rick.data_movie.imdb.IMDBApi
import com.rick.data_movie.imdb.IMDBConverters
import com.rick.data_movie.ny_times.Converters
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
    fun providesMovieCatalogApi(): MovieCatalogApi {
        return Retrofit.Builder()
            .baseUrl(MovieCatalogApi.NY_TIMES_BASE_URL)
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
    fun providesIMDBApi(): IMDBApi {
        return Retrofit.Builder()
            .baseUrl(IMDBApi.IMDB_BASE_URL)
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
            .addTypeConverter(IMDBConverters(GsonParser(Gson())))
            .fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun bindMovieCatalogRepository(api: MovieCatalogApi, imdbApi: IMDBApi, db: MovieCatalogDatabase):
            MovieCatalogRepository = MovieCatalogRepository(nyApi = api, imdbApi = imdbApi, db = db)
}