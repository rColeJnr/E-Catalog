package com.rick.data_book.favorite

import androidx.room.*

@Dao
interface FavDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Favorite)

    @Delete
    suspend fun delete(book: Favorite)

    @Query("SELECT * FROM favorite ORDER BY title ASC")
    suspend fun getFavorites(): List<Favorite>

}