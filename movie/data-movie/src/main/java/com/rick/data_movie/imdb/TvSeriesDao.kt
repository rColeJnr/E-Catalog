package com.rick.data_movie.imdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.imdb.series_model.TvSeries

@Dao
interface TvSeriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvSeries(series: TvSeries)

    @Query("SELECT * FROM tv_series")
    suspend fun getTvSeries() : List<TvSeries>

}