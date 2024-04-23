package com.rick.data.database_movie.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_movie.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Upsert
    suspend fun upsertArticles(article: List<ArticleEntity>)

    @Query("DELETE FROM article_table")
    suspend fun clearArticles()

    @Query("SELECT * FROM article_table")
    fun getArticles(): PagingSource<Int, ArticleEntity>

    @Query(
        """
            SELECT * FROM article_table
            WHERE id IN (:filterArticleIds)
            ORDER BY id
        """
    )
    fun getArticlesWithFilters(
        filterArticleIds: Set<Long>
    ): Flow<List<ArticleEntity>>

}