package com.rick.data_movie.imdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.imdb.series_model.TvSeries
import com.rick.data_movie.ny_times.Movie

@Dao
interface TvSeriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvSeries(series: List<TvSeries>)

    @Query("SELECT * FROM tv_series")
    suspend fun getTvSeries() : List<TvSeries>

    @Query("SELECT * FROM tv_series WHERE favorite LIKE :bool ORDER BY title ASC")
    fun getFavoriteSeries(bool: Boolean = false): List<TvSeries>

}