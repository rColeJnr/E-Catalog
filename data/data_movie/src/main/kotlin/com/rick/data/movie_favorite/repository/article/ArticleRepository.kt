package com.rick.data.movie_favorite.repository.article

import androidx.paging.PagingData
import com.rick.data.database_movie.model.ArticleEntity
import com.rick.data.model_movie.article_models.Article
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [Article] & [ArticleEntity]
 * */
interface ArticleRepository {
    fun getArticles(apiKey: String): Flow<PagingData<ArticleEntity>>
    fun getArticleFavorites(favorites: Set<Long>): Flow<List<Article>>
}