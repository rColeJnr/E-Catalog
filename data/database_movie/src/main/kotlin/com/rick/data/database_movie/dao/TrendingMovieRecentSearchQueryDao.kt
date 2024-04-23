package com.rick.data.database_movie.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_movie.model.TrendingMovieRecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingMovieRecentSearchQueryDao {

    @Query("SELECT * FROM trending_movie_recent_search_queries_table ORDER BY queriedDate DESC LIMIT :limit")
    fun getTrendingMovieRecentSearchQueryEntities(limit: Int): Flow<List<TrendingMovieRecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceTrendingMovieRecentSearchQuery(recentSearchQuery: TrendingMovieRecentSearchQueryEntity)

    @Query(value = "DELETE FROM trending_movie_recent_search_queries_table")
    suspend fun clearTrendingMovieRecentSearchQueries()

}