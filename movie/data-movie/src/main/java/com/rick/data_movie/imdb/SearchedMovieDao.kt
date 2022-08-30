package com.rick.data_movie.imdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.imdb.search_model.SearchedMovie

@Dao
interface SearchedMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<SearchedMovie>)

    @Query(
        "SELECT * FROM searched_movie WHERE " +
                "title LIKE :queryString or description LIKE :queryString " +
                "ORDER BY title ASC"
    )
    fun moviesByTitle(queryString: String): List<SearchedMovie>

}