package com.rick.data_book

import androidx.room.*
import com.rick.data.model_book.Favorite

@Dao
interface FavDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: com.rick.data.model_book.Favorite)

    @Delete
    suspend fun delete(book: com.rick.data.model_book.Favorite)

    @Query("SELECT * FROM favorite ORDER BY title ASC")
    suspend fun getFavorites(): List<com.rick.data.model_book.Favorite>

}