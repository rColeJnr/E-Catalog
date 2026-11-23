package com.rick.ecatalogs.book.data_book.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.book.data_book.database.model.GutenbergRecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GutenbergRecentSearchQueryDao {

    @Query("SELECT * FROM gutenberg_recent_search_queries_table ORDER BY queriedDate DESC LIMIT :limit")
    fun getGutenbergRecentSearchQueryEntities(limit: Int): Flow<List<GutenbergRecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceGutenbergRecentSearchQuery(recentSearchQuery: GutenbergRecentSearchQueryEntity)

    @Query(value = "DELETE FROM gutenberg_recent_search_queries_table")
    suspend fun clearGutenbergRecentSearchQueries()

}