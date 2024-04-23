package com.rick.data.movie_favorite.repository.article

import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_movie.ArticleUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserArticleDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : UserArticleDataRepository {

    override val userData: Flow<ArticleUserData>
        get() = preferencesDataSource.articleUserData

    override suspend fun setNyTimesArticleFavoriteId(articleId: Long, isFavorite: Boolean) {
        preferencesDataSource.setNyTimesArticlesFavoriteIds(articleId, isFavorite)
    }
}