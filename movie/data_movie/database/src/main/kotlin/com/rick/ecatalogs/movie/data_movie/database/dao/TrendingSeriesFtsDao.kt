package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingSeriesFtsDao {
    @Upsert
    suspend fun upsertTrendingSeries(series: List<TrendingSeriesFtsEntity>)

    @Query("SELECT trendingSeriesId FROM trending_series_fts_table WHERE trending_series_fts_table MATCH :query")
    fun searchAllTrendingSeries(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM trending_series_fts_table")
    fun getCount(): Flow<Int>
}
