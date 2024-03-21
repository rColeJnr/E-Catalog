package com.rick.data_book

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_book.gutenberg.model.Book
import com.rick.data_book.nytimes.model.NYBook

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(book: List<Book>)

    @Query("DELETE FROM book_db")
    suspend fun clearBooks()

    @Query("SELECT * FROM book_db ORDER BY downloads DESC")
    fun getBooks(): PagingSource<Int, Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBestsellers (books: List<NYBook>)

    @Query("DELETE FROM ny_book")
    suspend fun clearBestsellers()

    @Query("SELECT * FROM ny_book ORDER BY rank ASC")
    fun getBestsellers(): PagingSource<Int, NYBook>

    @Query("SELECT * FROM book_db WHERE favorite LIKE :bool ORDER BY downloads DESC")
    fun getFavoriteBooks(bool: Boolean = false): List<Book>

    @Query(
        "SELECT * FROM book_db WHERE " +
                "title LIKE :title " +
                "ORDER BY title ASC"
    )
    suspend fun getBook(title: String) : List<Book>

}
