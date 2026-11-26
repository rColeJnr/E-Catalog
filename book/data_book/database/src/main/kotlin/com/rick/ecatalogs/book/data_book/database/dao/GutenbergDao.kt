package com.rick.ecatalogs.book.data_book.database.dao

import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.book.data_book.database.model.GutenbergEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GutenbergDao {

    /** Rows that aren't part of the catalog list (e.g. search results); never replaces a stored row. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOutsideCatalog(entities: List<GutenbergEntity>)

    /** Takes every row out of the catalog list, before a refresh writes the list again. */
    @Query("UPDATE gutenberg_table SET catalog_position = NULL")
    suspend fun clearCatalog()

    /** Deletes the rows that are neither in the catalog list nor in [keepIds] (the favorites). */
    @Query("DELETE FROM gutenberg_table WHERE catalog_position IS NULL AND id NOT IN (:keepIds)")
    suspend fun deleteOutsideCatalog(keepIds: Set<Int>)

    @Upsert
    suspend fun upsertGutenberg(books: List<GutenbergEntity>)

    // Only the catalog list: search results and kept favorites have no position
    @Query("SELECT * FROM gutenberg_table WHERE catalog_position IS NOT NULL ORDER BY catalog_position")
    fun getGutenbergs(): PagingSource<Int, GutenbergEntity>

    @Query(
        """
            SELECT * FROM gutenberg_table
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
            ORDER BY downloads DESC
        """
    )
    fun getGutenbergWithFilters(
        filterById: Boolean = false,
        filterIds: Set<Int> = emptySet(),
        filterByQuery: Boolean = false,
        query: String = ""
    ): Flow<List<GutenbergEntity>>

    @Query(
        "SELECT * FROM gutenberg_table WHERE " +
                "title LIKE :query " +
                "ORDER BY title ASC"
    )
    fun searchGutenberg(query: String): Flow<List<GutenbergEntity>>

}
