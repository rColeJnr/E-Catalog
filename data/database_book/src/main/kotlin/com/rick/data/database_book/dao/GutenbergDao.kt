package com.rick.data.database_book.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_book.model.GutenbergEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GutenbergDao {

    @Upsert
    suspend fun upsertGutenberg(books: List<GutenbergEntity>)

    @Query("DELETE FROM gutenberg_table")
    suspend fun clearGutenbergs()

    @Query("SELECT * FROM gutenberg_table ORDER BY downloads DESC")
    fun getGutenbergs(): PagingSource<Int, GutenbergEntity>

    @Query(
        """
            SELECT * FROM gutenberg_table
            WHERE id IN (:filterGutenbergIds)
            ORDER BY id
        """
    )
    fun getGutenbergWithFilters(
        filterGutenbergIds: Set<Int>
    ): Flow<List<GutenbergEntity>>

    @Query(
        "SELECT * FROM gutenberg_table WHERE " +
                "title LIKE :query " +
                "ORDER BY title ASC"
    )
    fun searchGutenberg(query: String): Flow<List<GutenbergEntity>>

}
