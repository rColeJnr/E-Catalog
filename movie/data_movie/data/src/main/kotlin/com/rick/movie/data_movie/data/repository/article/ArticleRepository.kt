package com.rick.movie.data_movie.data.repository.article

import androidx.paging.PagingData
import com.rick.data.database_movie.model.ArticleEntity
import com.rick.data.model_movie.article_models.Article
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [Article] & [ArticleEntity]
 * */
interface ArticleRepository {
    fun getArticles(apiKey: String): Flow<PagingData<ArticleEntity>>
    fun getArticleFavorites(favorites: Set<String>): Flow<List<Article>>
    fun searchArticle(query: String, apiKey: String): Flow<List<Article>>
}