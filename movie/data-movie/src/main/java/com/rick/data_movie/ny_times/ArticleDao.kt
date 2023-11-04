package com.rick.data_movie.ny_times

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.ny_times.article_models.Doc

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(article: List<Doc>)

    @Query("DELETE FROM ny_times_article")
    suspend fun clearArticles()

    @Query("SELECT * FROM ny_times_article")
    fun getMovieArticles(): PagingSource<Int, Doc>

    @Query("SELECT * FROM ny_times_article WHERE favorite LIKE :bool ")
    suspend fun getFavoriteMovieArticle(bool: Boolean): List<Doc>

}