package com.rick.data_anime


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_anime.model_anime.Anime

@Dao
interface AnimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimes(animes: List<Anime>)

    @Query("SELECT * FROM anime_db ORDER BY rank ASC")
    fun getAnimes(): PagingSource<Int, Anime>

    @Query("DELETE FROM anime_db")
    suspend fun clearAnimes()

    @Query("SELECT * FROM anime_db WHERE title LIKE :query" +
            " ORDER BY title ASC ")
    suspend fun searchAnime(query: String): List<Anime>

}