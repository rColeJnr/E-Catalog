package com.rick.ecatalogs.anime.data_anime.database.dao

import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    /** Rows that aren't part of the catalog list (e.g. search results); never replaces a stored row. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOutsideCatalog(entities: List<AnimeEntity>)

    /** Takes every row out of the catalog list, before a refresh writes the list again. */
    @Query("UPDATE anime_table SET catalog_position = NULL")
    suspend fun clearCatalog()

    /** Deletes the rows that are neither in the catalog list nor in [keepIds] (the favorites). */
    @Query("DELETE FROM anime_table WHERE catalog_position IS NULL AND id NOT IN (:keepIds)")
    suspend fun deleteOutsideCatalog(keepIds: Set<Int>)

    @Upsert
    suspend fun upsertAnime(data: List<AnimeEntity>)

    // Only the catalog list: search results and kept favorites have no position
    @Query("SELECT * FROM anime_table WHERE catalog_position IS NOT NULL ORDER BY catalog_position")
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