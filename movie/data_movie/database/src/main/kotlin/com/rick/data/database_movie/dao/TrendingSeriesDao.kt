package com.rick.data.database_movie.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_movie.model.TrendingSeriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingSeriesDao {

    @Upsert
    suspend fun upsertTrendingSeries(trendingSeriesEntities: List<TrendingSeriesEntity>)

    @Query(
        value =
        """
            DELETE FROM trending_movie_table
            WHERE id IN (:ids)
        """
    )
    suspend fun deleteTrendingSeries(ids: List<Int>)

    @Query("SELECT * FROM trending_series_table ORDER BY popularity")
    fun getTrendingSeries(): PagingSource<Int, TrendingSeriesEntity>

    @Query(
        """
            SELECT * FROM trending_series_table
            WHERE 
                CASE WHEN :filterById
                    THEN id IN (:filterIds)
                    ELSE 1
                END
            AND
                CASE WHEN :filterByQuery
                    THEN name LIKE (:query)
                    ELSE 1
                END
            ORDER BY popularity DESC
        """
    )
    fun getTrendingSeriesWithFilters(
        filterById: Boolean = false,
        filterIds: Set<Int> = emptySet(),
        filterByQuery: Boolean = false,
        query: String = ""
    ): Flow<List<TrendingSeriesEntity>>

}