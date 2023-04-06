package com.rick.data_book.favorite

import androidx.room.*
import com.rick.data_book.model.Book

@Dao
interface FavDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM favorite ORDER BY title ASC")
    fun getFavorites(): List<Book>

}