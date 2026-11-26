package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingMovieDao {

    /** Rows that aren't part of the catalog list (e.g. search results); never replaces a stored row. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOutsideCatalog(entities: List<TrendingMovieEntity>)

    /** Takes every row out of the catalog list, before a refresh writes the list again. */
    @Query("UPDATE trending_movie_table SET catalog_position = NULL")
    suspend fun clearCatalog()

    /** Deletes the rows that are neither in the catalog list nor in [keepIds] (the favorites). */
    @Query("DELETE FROM trending_movie_table WHERE catalog_position IS NULL AND id NOT IN (:keepIds)")
    suspend fun deleteOutsideCatalog(keepIds: Set<Int>)

    @Upsert
    suspend fun upsertTrendingMovie(trendingMovie: List<TrendingMovieEntity>)

    // Only the catalog list: search results and kept favorites have no position
    @Query("SELECT * FROM trending_movie_table WHERE catalog_position IS NOT NULL ORDER BY catalog_position")
    fun getTrendingMovie(): PagingSource<Int, TrendingMovieEntity>

    @Query(
        """
            SELECT * FROM trending_movie_table
            WHERE 
                CASE WHEN :filterById
                    THEN id IN (:filterIds)
                    ELSE 1
                END
            AND
                CASE WHEN :filterByQuery
                    THEN title LIKE (:query)
                    ELSE 1
                END
            ORDER BY popularity DESC
        """
    )
    fun getTrendingMovieWithFilters(
        filterById: Boolean = false,
        filterIds: Set<Int> = emptySet(),
        filterByQuery: Boolean = false,
        query: String = ""
    ): Flow<List<TrendingMovieEntity>>

}