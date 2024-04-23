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

    @Query("DELETE FROM trending_series_table")
    suspend fun deleteTrendingSeries()

    @Query("SELECT * FROM trending_series_table ORDER BY popularity")
    fun getTrendingSeries(): PagingSource<Int, TrendingSeriesEntity>

    @Query(
        """
            SELECT * FROM trending_series_table
            WHERE id IN (:filterTrendingSeriesIds)
            ORDER BY id
        """
    )
    fun getTrendingSeriesWithFilters(
        filterTrendingSeriesIds: Set<Int>
    ): Flow<List<TrendingSeriesEntity>>

}