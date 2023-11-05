package com.rick.data_movie.imdb_am_not_paying

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.imdb_am_not_paying.series_model.TvSeries

@Dao
interface TvSeriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvSeries(series: List<TvSeries>)

    @Query("SELECT * FROM tv_series")
    suspend fun getTvSeries() : List<TvSeries>

}