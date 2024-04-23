package com.rick.data.database_anime.dao


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_anime.model.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Upsert
    suspend fun upsertAnime(data: List<AnimeEntity>)

    @Query(
        "SELECT * FROM anime_table" +
                " ORDER BY popularity ASC"
    )
    fun getAnime(): PagingSource<Int, AnimeEntity>

    @Query(
        """
            SELECT * FROM anime_table
            WHERE id IN (:filterAnimeIds)
            ORDER BY id
        """
    )
    fun getAnimeWithFilters(
        filterAnimeIds: Set<Int>
    ): Flow<List<AnimeEntity>>

    @Query("DELETE FROM anime_table")
    suspend fun clearAnime()

    // This is supposed to get u either anime or manga, from the same search screen
    // To be used if I ever do a general search screen
    @Query(
        "SELECT * FROM anime_table WHERE title LIKE :query " +
                "ORDER BY title ASC "
    )
    suspend fun searchManga(query: String): List<AnimeEntity>

//    @Query(
//        "SELECT * FROM anime_table WHERE title LIKE :query " +
//                " ORDER BY rank ASC "
//    )
//    suspend fun searchAnimeAndManga(query: String): List<AnimeEntity>

}