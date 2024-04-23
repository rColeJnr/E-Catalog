package com.rick.data.database_book

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data.database_book.dao.BestsellerDao
import com.rick.data.database_book.dao.BestsellerRemoteKeysDao
import com.rick.data.database_book.dao.GutenbergDao
import com.rick.data.database_book.dao.GutenbergRemoteKeysDao
import com.rick.data.database_book.model.BestsellerEntity
import com.rick.data.database_book.model.BestsellerRemoteKeys
import com.rick.data.database_book.model.GutenbergEntity
import com.rick.data.database_book.model.GutenbergRemoteKeys
import com.rick.data.database_book.util.BookTypeConverters

@Database(
    entities = [GutenbergEntity::class, BestsellerEntity::class, GutenbergRemoteKeys::class, BestsellerRemoteKeys::class],
    version = 1,
    exportSchema = true
)

@TypeConverters(BookTypeConverters::class)
abstract class BookDatabase : RoomDatabase() {

    abstract val bestsellerRemoteKeysDao: BestsellerRemoteKeysDao
    abstract val gutenbergDao: GutenbergDao
    abstract val bestsellerDao: BestsellerDao
    abstract val gutenbergRemoteKeysDao: GutenbergRemoteKeysDao
}