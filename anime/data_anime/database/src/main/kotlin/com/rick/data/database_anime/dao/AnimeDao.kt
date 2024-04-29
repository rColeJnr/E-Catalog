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
        "SELECT * FROM anime_table" + " ORDER BY popularity ASC"
    )
    fun getAnime(): PagingSource<Int, AnimeEntity>

    @Query(
        """
            SELECT * FROM anime_table
            WHERE 
                CASE WHEN :filterById
                    THEN id IN (:filterIds)
                    ELSE 1
                END
            AND
                CASE WHEN :filterByQuery
                    THEN title LIKE (:query)
                    ELSE 1
                END
            ORDER BY popularity DESC
        """
    )
    fun getAnimeWithFilters(
        filterById: Boolean = false,
        filterIds: Set<Int> = emptySet(),
        filterByQuery: Boolean = false,
        query: String = ""
    ): Flow<List<AnimeEntity>>

    @Query(
        value = """
            DELETE FROM anime_table
            WHERE id IN (:ids)
        """
    )
    suspend fun clearAnime(ids: List<Int>)

    // This is supposed to get u either anime or manga, from the same search screen
    // To be used if I ever do a general search screen
    @Query(
        "SELECT * FROM anime_table WHERE title LIKE :query " + "ORDER BY title ASC "
    )
    suspend fun searchManga(query: String): List<AnimeEntity>

//    @Query(
//        "SELECT * FROM anime_table WHERE title LIKE :query " +
//                " ORDER BY rank ASC "
//    )
//    suspend fun searchAnimeAndManga(query: String): List<AnimeEntity>

}