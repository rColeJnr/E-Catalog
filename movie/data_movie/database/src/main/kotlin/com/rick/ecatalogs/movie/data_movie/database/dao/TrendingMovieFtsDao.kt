package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingMovieFtsDao {
    @Upsert
    suspend fun upsertTrendingMovies(movies: List<TrendingMovieFtsEntity>)

    @Query("SELECT trendingMovieId FROM trending_movie_fts_table WHERE trending_movie_fts_table MATCH :query")
    fun searchAllTrendingMovies(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM trending_movie_fts_table")
    fun getCount(): Flow<Int>
}
