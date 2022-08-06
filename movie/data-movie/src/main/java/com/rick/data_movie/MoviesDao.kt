package com.rick.data_movie

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movie: List<Movie>)

    @Query("DELETE FROM movies_db")
    suspend fun clearMovies()

    @Query("SELECT * FROM movies_db")
    fun getMovies(): PagingSource<String, Movie>

//    @Query("SELECT * FROM repos WHERE " +
//            "name LIKE :queryString OR description LIKE :queryString " +
//            "ORDER BY stars DESC, name ASC")
//    fun reposByName(queryString: String): PagingSource<Int, Repo>

}