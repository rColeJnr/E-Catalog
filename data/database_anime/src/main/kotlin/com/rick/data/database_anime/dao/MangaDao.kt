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
            WHERE id IN (:filterMangaIds)
            ORDER BY id
        """
    )
    fun getMangaWithFilters(
        filterMangaIds: Set<Int>
    ): Flow<List<MangaEntity>>

    @Query("DELETE FROM manga_table")
    suspend fun clearManga()

    // This is supposed to get u either anime or manga, from the same search screen
    // To be used if I ever do a general search screen
    @Query(
        "SELECT * FROM manga_table WHERE title LIKE :query " +
                "ORDER BY rank ASC "
    )
    suspend fun searchManga(query: String): List<MangaEntity>

//    @Query(
//        "SELECT * FROM manga_table WHERE title LIKE :query " +
//                " ORDER BY rank ASC "
//    )
//    suspend fun searchAnimeAndManga(query: String): List<MangaEntity>

}