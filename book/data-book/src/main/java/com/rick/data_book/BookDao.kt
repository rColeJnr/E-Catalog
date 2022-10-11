package com.rick.data_book

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_book.model.Book

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(book: List<Book>)

    @Query("DELETE FROM book_db")
    suspend fun clearBooks()

    @Query("SELECT * FROM book_db")
    suspend fun getBooks(): List<Book>

    @Query("SELECT * FROM book_db WHERE id = :id")
    suspend fun getBook(id: Int) : Book

}
