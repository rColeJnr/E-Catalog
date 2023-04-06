package com.rick.data_book

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rick.data_book.model.Book

@Database(
    entities = [Book::class, BookRemoteKeys::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(BookTypeConverters::class)
abstract class BookDatabase: RoomDatabase() {

    abstract val bookDao: BookDao
    abstract val remoteKeysDao: BookRemoteKeysDao

    companion object {
        const val DATABASE_NAME = "BOOK_DB"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE book_db ADD COLUMN favorite BOOLEAN")
            }
        }
    }

}