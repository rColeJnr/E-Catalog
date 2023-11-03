package com.rick.data_movie.ny_times_deprecated

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.ny_times.article_models.Doc

@Dao
interface MoviesDao {

    @Deprecated("Use insertArticles instead")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movie: List<Doc>)

    @Deprecated("Use clearArticles instead")
    @Query("DELETE FROM movies_db")
    suspend fun clearMovies()

    @Deprecated("Use getMovieArticles instead")
    @Query("SELECT * FROM movies_db WHERE favorite LIKE :bool ORDER BY title ASC")
    fun getFavoriteMovies(bool: Boolean = false): List<Movie>

    @Deprecated("Use getMovieArticles instead")
    @Query("SELECT * FROM movies_db ORDER BY id ASC")
    fun getMovies(): PagingSource<Int, Movie>

//    @Query("SELECT * FROM movies_db WHERE id = :id")
//    fun getMovie(id: Int): Movie?

}