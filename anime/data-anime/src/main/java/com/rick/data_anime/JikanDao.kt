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
            " ORDER BY rank ASC")
    fun getAnime(type: String = "Anime"): PagingSource<Int, Jikan>

    @Query("SELECT * FROM jikan_db WHERE type LIKE :type" +
            " ORDER BY rank ASC")
    fun getManga(type: String = "Manga"): PagingSource<Int, Jikan>

    @Query("DELETE FROM jikan_db WHERE type LIKE :type")
    suspend fun clearAnime(type: String = "Anime")

    @Query("DELETE FROM jikan_db WHERE type LIKE :type")
    suspend fun clearManga(type: String = "Manga")

    @Query("SELECT * FROM jikan_db WHERE title LIKE :query " +
            "AND type LIKE :type ORDER BY popularity ASC ")
    suspend fun searchAnimeOrManga(query: String, type: String): List<Jikan>

    //TODO a feature
    @Query("SELECT * FROM jikan_db WHERE title LIKE :query " +
            " ORDER BY rank ASC ")
    suspend fun searchAnimeAndManga(query: String): List<Jikan>


}