package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingSeriesDao {

    /** Rows that aren't part of the catalog list (e.g. search results); never replaces a stored row. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOutsideCatalog(entities: List<TrendingSeriesEntity>)

    /** Takes every row out of the catalog list, before a refresh writes the list again. */
    @Query("UPDATE trending_series_table SET catalog_position = NULL")
    suspend fun clearCatalog()

    /** Deletes the rows that are neither in the catalog list nor in [keepIds] (the favorites). */
    @Query("DELETE FROM trending_series_table WHERE catalog_position IS NULL AND id NOT IN (:keepIds)")
    suspend fun deleteOutsideCatalog(keepIds: Set<Int>)

    @Upsert
    suspend fun upsertTrendingSeries(trendingSeriesEntities: List<TrendingSeriesEntity>)

    // Only the catalog list: search results and kept favorites have no position
    @Query("SELECT * FROM trending_series_table WHERE catalog_position IS NOT NULL ORDER BY catalog_position")
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