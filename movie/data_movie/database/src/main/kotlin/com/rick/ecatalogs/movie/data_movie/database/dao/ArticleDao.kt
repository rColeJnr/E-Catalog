package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    /** Rows that aren't part of the catalog list (e.g. search results); never replaces a stored row. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOutsideCatalog(entities: List<ArticleEntity>)

    /** Takes every row out of the catalog list, before a refresh writes the list again. */
    @Query("UPDATE article_table SET catalog_position = NULL")
    suspend fun clearCatalog()

    /** Deletes the rows that are neither in the catalog list nor in [keepIds] (the favorites). */
    @Query("DELETE FROM article_table WHERE catalog_position IS NULL AND id NOT IN (:keepIds)")
    suspend fun deleteOutsideCatalog(keepIds: Set<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticles(article: List<ArticleEntity>)

    // Only the catalog list: search results and kept favorites have no position
    @Query("SELECT * FROM article_table WHERE catalog_position IS NOT NULL ORDER BY publication_date DESC")
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
                    THEN title LIKE (:query) OR lead_paragraph LIKE (:query)
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