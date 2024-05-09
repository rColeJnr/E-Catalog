package com.rick.data.database_movie.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data.database_movie.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticles(article: List<ArticleEntity>)

    @Query(
        """
        DELETE FROM article_table
        WHERE id IN (:ids)
        """
    )
    suspend fun clearArticles(ids: List<String>)

    @Query("SELECT * FROM article_table ORDER BY publication_date DESC")
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
        filterArticleIds: Set<String> = emptySet(),
        filterByQuery: Boolean = false,
        query: String = ""
    ): Flow<List<ArticleEntity>>

}