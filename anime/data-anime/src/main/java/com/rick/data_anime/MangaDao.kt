package com.rick.data_anime

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_anime.model_manga.Manga

@Dao
interface MangaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangas(mangas: List<Manga>)

    @Query("SELECT * FROM manga_db ORDER BY rank ASC")
    fun getMangas(): PagingSource<Int, Manga>

    @Query("DELETE FROM manga_db")
    suspend fun clearMangas()

    @Query("SELECT * FROM manga_db WHERE title LIKE :query" +
            " ORDER BY title ASC ")
    suspend fun searchManga(query: String): List<Manga>

}