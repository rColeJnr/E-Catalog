package com.rick.movie.data_movie.data.repository.article

import com.rick.data.model_movie.ArticleUserData
import kotlinx.coroutines.flow.Flow

interface UserArticleDataRepository {

    /**
     * Stream of [ArticleUserData]
     */
    val userData: Flow<ArticleUserData>

    /**
     * updates the favorite status of the resource
     */
    suspend fun setNyTimesArticleFavoriteId(articleId: String, isFavorite: Boolean)
}