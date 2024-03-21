package com.rick.data_anime


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_anime.model_jikan.Jikan

@Dao
interface JikanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJikan(data: List<Jikan>)

    @Query("SELECT * FROM jikan_db WHERE type LIKE :type " +
            " ORDER BY popularity ASC")
    fun getAnime(type: String = "TV"): PagingSource<Int, Jikan>

    @Query("SELECT * FROM jikan_db WHERE type LIKE \"Manga\"" +
            " ORDER BY popularity ASC")
    fun getManga(): PagingSource<Int, Jikan>

    @Query("DELETE FROM jikan_db WHERE type LIKE :type")
    suspend fun clearAnime(type: String = "TV")

    @Query("DELETE FROM jikan_db WHERE type LIKE :type")
    suspend fun clearManga(type: String = "Manga")

    // This is supposed to get u either anime or manga, from the same search screen
    // To be used if I ever do a general search screen
    @Query("SELECT * FROM jikan_db WHERE title LIKE :query " +
            "AND type LIKE :type ORDER BY rank ASC ")
    suspend fun searchAnimeOrManga(query: String, type: String): List<Jikan>

    @Query("SELECT * FROM jikan_db WHERE title LIKE :query " +
            " ORDER BY rank ASC ")
    suspend fun searchAnimeAndManga(query: String): List<Jikan>

}