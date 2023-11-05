package com.rick.data_movie.tmdb

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.tmdb.movie.MovieResponse
import com.rick.data_movie.tmdb.trending_movie.TrendingMovie
import com.rick.data_movie.tmdb.trending_tv.TrendingTv
import com.rick.data_movie.tmdb.tv.TvResponse

@Dao
interface TMDBDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrendingTv(trendingTv: List<TrendingTv>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrendingMovie(trendingMovie: List<TrendingMovie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTv(tv: TvResponse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieResponse)

    @Query("DELETE FROM trending_tv")
    suspend fun deleteTrendingTv()

    @Query("DELETE FROM trending_movie")
    suspend fun deleteTrendingMovie()

//    @Query("DELETE FROM tmdb_tv")
//    suspend fun deleteTv(tv: TvResponse)
//
//    @Query("DELETE FROM tmdb_movie")
//    suspend fun deleteMovie(movie: MovieResponse)

    @Query("SELECT * FROM trending_tv")
    fun getTrendingTv(): PagingSource<Int, TrendingTv>

    @Query("SELECT * FROM trending_movie")
    fun getTrendingMovie(): PagingSource<Int, TrendingMovie>

    @Query("SELECT * FROM tmdb_tv WHERE id = :id")
    suspend fun getTv(id: Int): TvResponse?

    @Query("SELECT * FROM tmdb_movie WHERE id = :id")
    suspend fun getMovie(id: Int): MovieResponse?

}