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

    @Query(
        """
        DELETE FROM article_table
        WHERE id IN (:ids)
        """
    )
    suspend fun clearArticles(ids: List<Long>)

    @Query("SELECT * FROM article_table")
    fun getArticles(): PagingSource<Int, ArticleEntity>

    @Query(
        """
            SELECT * FROM article_table
            WHERE 
                CASE WHEN :filterById
                    THEN id IN (:filterArticleIds)
                    ELSE 1
                END
            AND
                CASE WHEN :filterByQuery
                    THEN title LIKE (:query)
                    ELSE 1
                END
            ORDER BY publication_date DESC
        """
    )
    fun getArticlesWithFilters(
        filterById: Boolean = false,
        filterArticleIds: Set<Long> = emptySet(),
        filterByQuery: Boolean = false,
        query: String = ""
    ): Flow<List<ArticleEntity>>

}