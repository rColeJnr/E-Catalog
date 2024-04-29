package com.rick.data.database_anime.dao


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_anime.model.MangaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {

    @Upsert
    suspend fun upsertManga(data: List<MangaEntity>)

    @Query(
        "SELECT * FROM manga_table ORDER BY popularity"
    )
    fun getManga(): PagingSource<Int, MangaEntity>

    @Query(
        """
            SELECT * FROM manga_table
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
    fun getMangaWithFilters(
        filterById: Boolean = false,
        filterIds: Set<Int> = emptySet(),
        filterByQuery: Boolean = false,
        query: String = ""
    ): Flow<List<MangaEntity>>

    @Query(
        """
            DELETE FROM manga_table
            WHERE id IN (:ids)
        """
    )
    suspend fun clearManga(ids: List<Int>)

    // This is supposed to get u either anime or manga, from the same search screen
    // To be used if I ever do a general search screen
    @Query(
        "SELECT * FROM manga_table WHERE title LIKE :query " + "ORDER BY rank ASC "
    )
    suspend fun searchManga(query: String): List<MangaEntity>

//    @Query(
//        "SELECT * FROM manga_table WHERE title LIKE :query " +
//                " ORDER BY rank ASC "
//    )
//    suspend fun searchAnimeAndManga(query: String): List<MangaEntity>

}