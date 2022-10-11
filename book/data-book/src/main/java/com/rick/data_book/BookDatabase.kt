package com.rick.data_book

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data_book.model.Book

@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(BookTypeConverters::class)
abstract class BookDatabase: RoomDatabase() {

    abstract val bookDao: BookDao
    abstract val remoteKeys: BookRemoteKeys

    companion object {
        const val DATABASE_NAME = "BOOK_DB"
    }

}