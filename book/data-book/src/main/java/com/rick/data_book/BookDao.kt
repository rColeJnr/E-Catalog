package com.rick.data_book

import androidx.paging.PagingSource
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

    @Query("SELECT * FROM book_db ORDER BY downloads DESC")
    fun getBooks(): PagingSource<Int, Book>

    @Query(
        "SELECT * FROM book_db WHERE " +
                "title LIKE :title " +
                "ORDER BY title ASC"
    )
    suspend fun getBook(title: String) : List<Book>

}
