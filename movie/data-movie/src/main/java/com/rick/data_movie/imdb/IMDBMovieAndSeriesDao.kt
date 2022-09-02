package com.rick.data_movie.imdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.imdb.movie_model.IMDBMovie

@Dao
interface IMDBMovieAndSeriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: IMDBMovie)

    @Query(
        "SELECT * FROM imdb_movie WHERE " +
                "id LIKE :queryString"
    )
    fun movieById(queryString: String): IMDBMovie

}
