package com.rick.data_anime.favorite

import androidx.room.*

@Dao
interface FavDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jikan: JikanFavorite)

    @Delete
    suspend fun delete(jikan: JikanFavorite)

    @Query("SELECT * FROM favorite WHERE type LIKE \"Manga\" ORDER BY title ASC")
    fun getFavoriteManga(): List<JikanFavorite>

    @Query("SELECT * FROM favorite WHERE type LIKE \"Anime\" ORDER BY title ASC")
    fun getFavoriteAnime(): List<JikanFavorite>

}