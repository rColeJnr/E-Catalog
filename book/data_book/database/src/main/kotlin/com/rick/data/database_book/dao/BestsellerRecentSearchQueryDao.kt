package com.rick.data.database_book.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_book.model.BestsellerRecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BestsellerRecentSearchQueryDao {

    @Query("SELECT * FROM bestseller_recent_search_queries_table ORDER BY queriedDate DESC LIMIT :limit")
    fun getBestsellerRecentSearchQueryEntities(limit: Int): Flow<List<BestsellerRecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceBestsellerRecentSearchQuery(recentSearchQuery: BestsellerRecentSearchQueryEntity)

    @Query(value = "DELETE FROM bestseller_recent_search_queries_table")
    suspend fun clearBestsellerRecentSearchQueries()

}