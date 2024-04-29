package com.rick.movie.data_movie.data.repository.article

import com.rick.data.model_movie.article_models.ArticleSearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 */
interface ArticleSearchRepository {

    /**
     * Populate the fast text search (fts) tables for the search contents.
     */
    suspend fun populateFtsArticleData()

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [ArticleSearchResult]
     */
    fun articleSearchContents(searchQuery: String): Flow<ArticleSearchResult>

    fun getArticleSearchContentsCount(): Flow<Int>

}