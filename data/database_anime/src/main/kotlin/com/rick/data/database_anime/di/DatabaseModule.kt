package com.rick.data.database_anime.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data.database_anime.JikanDatabase
import com.rick.data.database_anime.util.JikanConverters
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
        "jikan-database-test-1",
    ).addTypeConverter(JikanConverters(GsonParser(Gson()))).build()
}
