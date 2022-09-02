package com.rick.data_movie.imdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.imdb.movie_model.IMDBMovie

@Dao
interface IMDBMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: IMDBMovie)

    @Query(
        "SELECT * FROM imdb_movie WHERE " +
                "id LIKE :queryString"
    )
    fun movieByTitle(queryString: String): IMDBMovie

    @Query(
        "SELECT * FROM imdb_movie WHERE " +
                "title LIKE :queryString"
    )
    fun seriesByTitle(queryString: String): IMDBMovie

}
