package com.rick.ecatalogs.movie.data_movie.database.di

import com.rick.ecatalogs.movie.data_movie.database.MIGRATION_1_2
import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.ecatalogs.data.common.GsonParser
import com.rick.ecatalogs.movie.data_movie.database.MovieDatabase
import com.rick.ecatalogs.movie.data_movie.database.util.NyTimesConverters
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
            "movie-database"
        ).addTypeConverter(NyTimesConverters(GsonParser(Gson())))
            .addMigrations(MIGRATION_1_2)
            .build()

}