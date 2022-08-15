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
    fun getMovies(): PagingSource<Int, Movie>

    @Query("SELECT * FROM movies_db WHERE id = :id")
    fun getMovie(id: Int): Movie?

}