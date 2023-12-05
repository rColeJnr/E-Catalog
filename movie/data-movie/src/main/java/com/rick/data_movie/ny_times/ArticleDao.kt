package com.rick.data_movie.ny_times

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.ny_times.article_models.NYMovie

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(article: List<NYMovie>)

    @Query("DELETE FROM ny_times_article")
    suspend fun clearArticles()

    @Query("SELECT * FROM ny_times_article")
    fun getMovieArticles(): PagingSource<Int, NYMovie>

    @Query("SELECT * FROM ny_times_article WHERE favorite LIKE :bool ")
    suspend fun getFavoriteMovieArticle(bool: Boolean): List<NYMovie>

}