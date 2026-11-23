package com.rick.ecatalogs.book.data_book.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.ecatalogs.book.data_book.database.dao.BestsellerDao
import com.rick.ecatalogs.book.data_book.database.dao.BestsellerRemoteKeysDao
import com.rick.ecatalogs.book.data_book.database.dao.GutenbergDao
import com.rick.ecatalogs.book.data_book.database.dao.GutenbergRecentSearchQueryDao
import com.rick.ecatalogs.book.data_book.database.dao.GutenbergRemoteKeysDao
import com.rick.ecatalogs.book.data_book.database.model.BestsellerEntity
import com.rick.ecatalogs.book.data_book.database.model.BestsellerRemoteKeys
import com.rick.ecatalogs.book.data_book.database.model.GutenbergEntity
import com.rick.ecatalogs.book.data_book.database.model.GutenbergRecentSearchQueryEntity
import com.rick.ecatalogs.book.data_book.database.model.GutenbergRemoteKeys
import com.rick.ecatalogs.book.data_book.database.util.BookTypeConverters

@Database(
    entities = [
        GutenbergEntity::class,
        BestsellerEntity::class,
        GutenbergRemoteKeys::class,
        BestsellerRemoteKeys::class,
        GutenbergRecentSearchQueryEntity::class
    ],
    version = 2,
    exportSchema = true
)

@TypeConverters(BookTypeConverters::class)
abstract class BookDatabase : RoomDatabase() {

    abstract val bestsellerRemoteKeysDao: BestsellerRemoteKeysDao
    abstract val gutenbergDao: GutenbergDao
    abstract val bestsellerDao: BestsellerDao
    abstract val gutenbergRemoteKeysDao: GutenbergRemoteKeysDao
    abstract val gutenbergRecentSearchQueryDao: GutenbergRecentSearchQueryDao
}