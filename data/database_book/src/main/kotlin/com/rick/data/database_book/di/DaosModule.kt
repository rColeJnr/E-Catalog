package com.rick.data.database_book.di

import com.rick.data.database_book.BookDatabase
import com.rick.data.database_book.dao.BestsellerDao
import com.rick.data.database_book.dao.BestsellerRemoteKeysDao
import com.rick.data.database_book.dao.GutenbergDao
import com.rick.data.database_book.dao.GutenbergRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesGutenbergDao(bookDatabase: BookDatabase): GutenbergDao =
        bookDatabase.gutenbergDao

    @Provides
    fun providesBestsellerDao(bookDatabase: BookDatabase): BestsellerDao =
        bookDatabase.bestsellerDao

    @Provides
    fun providesGutenbergRemoteKeysDao(bookDatabase: BookDatabase): GutenbergRemoteKeysDao =
        bookDatabase.gutenbergRemoteKeysDao

    @Provides
    fun providesBestsellerRemoteKeysDao(bookDatabase: BookDatabase): BestsellerRemoteKeysDao =
        bookDatabase.bestsellerRemoteKeysDao

}