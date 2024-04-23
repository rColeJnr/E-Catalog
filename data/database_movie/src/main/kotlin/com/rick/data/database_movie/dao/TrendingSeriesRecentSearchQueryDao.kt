package com.rick.data.database_movie.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_movie.model.TrendingSeriesRecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingSeriesRecentSearchQueryDao {

    @Query("SELECT * FROM trending_series_recent_search_queries_table ORDER BY queriedDate DESC LIMIT :limit")
    fun getTrendingSeriesRecentSearchQueryEntities(limit: Int): Flow<List<TrendingSeriesRecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceTrendingSeriesRecentSearchQuery(recentSearchQuery: TrendingSeriesRecentSearchQueryEntity)

    @Query(value = "DELETE FROM trending_series_recent_search_queries_table")
    suspend fun clearTrendingSeriesRecentSearchQueries()

}