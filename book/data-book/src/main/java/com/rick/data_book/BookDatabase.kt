package com.rick.data_book

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(BookTypeConverters::class)
abstract class BookDatabase: RoomDatabase() {

    abstract val bookDao: BookDao

    companion object {
        const val DATABASE_NAME = "BOOK_DB"
    }

}