package com.rick.data.movie_favorite.repository.article

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
    suspend fun setNyTimesArticleFavoriteId(articleId: Long, isFavorite: Boolean)
}