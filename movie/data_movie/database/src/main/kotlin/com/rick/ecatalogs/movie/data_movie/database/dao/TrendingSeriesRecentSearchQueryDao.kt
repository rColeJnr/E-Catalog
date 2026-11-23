package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesRecentSearchQueryEntity
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