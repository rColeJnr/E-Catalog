package com.rick.movie.data_movie.data.repository.article

import com.rick.data.model_movie.ArticleRecentSearchQuery
import kotlinx.coroutines.flow.Flow


/**
 * Data layer interface for anime recent searches
 * */
interface ArticleRecentSearchRepository {

    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     */
    fun getArticleRecentSearchQueries(limit: Int): Flow<List<ArticleRecentSearchQuery>>

    /**
     * Insert or replace the [searchQuery] as part of the recent searches.
     */
    suspend fun insertOrReplaceArticleRecentSearch(searchQuery: String)

    /**
     * Clear the recent searches.
     */
    suspend fun clearArticleRecentSearches()
}