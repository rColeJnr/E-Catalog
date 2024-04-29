package com.rick.data.database_movie.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data.database_movie.MovieDatabase
import com.rick.data.database_movie.util.NyTimesConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesMovieDatabase(
        @ApplicationContext context: Context
    ): MovieDatabase =
        Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movie-database-test-1"
        ).addTypeConverter(NyTimesConverters(GsonParser(Gson())))
            .build()

}