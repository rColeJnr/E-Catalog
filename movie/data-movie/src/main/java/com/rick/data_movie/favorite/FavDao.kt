package com.rick.data_movie.favorite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("SELECT * FROM imdb_favorite WHERE type LIKE 'tv' ORDER BY title ASC")
    suspend fun getFavoriteSeries(): List<Favorite>

    @Query("SELECT * FROM favorite WHERE type LIKE 'movie' ORDER BY title ASC")
    suspend fun getFavoriteMovies(): List<Favorite>

}