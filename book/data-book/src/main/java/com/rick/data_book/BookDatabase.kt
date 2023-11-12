package com.rick.data_book

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data_book.favorite.FavDao
import com.rick.data_book.favorite.Favorite
import com.rick.data_book.gutenberg.BookRemoteKeys
import com.rick.data_book.gutenberg.BookRemoteKeysDao
import com.rick.data_book.gutenberg.model.Book
import com.rick.data_book.nytimes.NYBookRemoteKeys
import com.rick.data_book.nytimes.NYBookRemoteKeysDao
import com.rick.data_book.nytimes.model.NYBook

@Database(
    entities = [Book::class, Favorite::class, BookRemoteKeys::class, NYBookRemoteKeys::class, NYBook::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(BookTypeConverters::class)
abstract class BookDatabase : RoomDatabase() {

    abstract val nyRemoteKeysDao: NYBookRemoteKeysDao
    abstract val bookDao: BookDao
    abstract val favDao: FavDao
    abstract val remoteKeysDao: BookRemoteKeysDao

    companion object {
        const val DATABASE_NAME = "TEST_BOOK_DB"
    }
}