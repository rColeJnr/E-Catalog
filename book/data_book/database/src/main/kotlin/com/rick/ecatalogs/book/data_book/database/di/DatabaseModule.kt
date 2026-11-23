package com.rick.ecatalogs.book.data_book.database.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.ecatalogs.data.common.GsonParser
import com.rick.ecatalogs.book.data_book.database.BookDatabase
import com.rick.ecatalogs.book.data_book.database.MIGRATION_1_2
import com.rick.ecatalogs.book.data_book.database.util.BookTypeConverters
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
    ).addTypeConverter(BookTypeConverters(GsonParser(Gson())))
        .addMigrations(MIGRATION_1_2)
        .build()
}