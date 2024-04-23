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

    @Query("DELETE FROM trending_movie_table")
    suspend fun deleteTrendingMovie()

    @Query("SELECT * FROM trending_movie_table ORDER  BY release_date DESC")
    fun getTrendingMovie(): PagingSource<Int, TrendingMovieEntity>

    @Query(
        """
            SELECT * FROM trending_movie_table
            WHERE id IN (:filterTrendingMovieIds)
            ORDER BY id
        """
    )
    fun getTrendingMovieWithFilters(
        filterTrendingMovieIds: Set<Int>
    ): Flow<List<TrendingMovieEntity>>

}