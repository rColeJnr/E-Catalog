package com.rick.data_anime.favorite

import androidx.room.*
import com.rick.data_anime.model_jikan.Jikan

@Dao
interface FavDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jikan: Jikan)

    @Delete
    suspend fun delete(jikan: Jikan)

    @Query("SELECT * FROM favorite WHERE type LIKE \"Manga\" ORDER BY title ASC")
    fun getFavoriteManga(): List<Jikan>

    @Query("SELECT * FROM favorite WHERE type LIKE \"Anime\" ORDER BY title ASC")
    fun getFavoriteAnime(): List<Jikan>

}