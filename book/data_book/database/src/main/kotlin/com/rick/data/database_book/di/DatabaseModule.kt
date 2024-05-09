package com.rick.data.database_book.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data.database_book.BookDatabase
import com.rick.data.database_book.util.BookTypeConverters
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
    fun providesBookDatabase(
        @ApplicationContext context: Context
    ): BookDatabase = Room.databaseBuilder(
        context,
        BookDatabase::class.java,
        "book-database"
    ).addTypeConverter(BookTypeConverters(GsonParser(Gson()))).build()
}