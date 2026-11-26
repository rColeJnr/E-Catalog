package com.rick.ecatalogs.anime.data_anime.database.di

import com.rick.ecatalogs.anime.data_anime.database.MIGRATION_1_2
import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.ecatalogs.data.common.GsonParser
import com.rick.ecatalogs.anime.data_anime.database.JikanDatabase
import com.rick.ecatalogs.anime.data_anime.database.util.JikanConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesJikanDatabase(
        @ApplicationContext context: Context,
    ): JikanDatabase = Room.databaseBuilder(
        context,
        JikanDatabase::class.java,
        "jikan-database",
    ).addTypeConverter(JikanConverters(GsonParser(Gson())))
        .addMigrations(MIGRATION_1_2)
        .build()
}
