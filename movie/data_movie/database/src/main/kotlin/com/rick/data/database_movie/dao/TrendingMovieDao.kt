package com.rick.data.database_movie.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_movie.model.TrendingMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingMovieDao {

    @Upsert
    suspend fun upsertTrendingMovie(trendingMovie: List<TrendingMovieEntity>)

    @Query(
        value = """
        DELETE FROM trending_movie_table
        WHERE id IN (:ids)
    """
    )
    suspend fun deleteTrendingMovie(ids: List<Int>)

    @Query("SELECT * FROM trending_movie_table ORDER  BY release_date DESC")
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