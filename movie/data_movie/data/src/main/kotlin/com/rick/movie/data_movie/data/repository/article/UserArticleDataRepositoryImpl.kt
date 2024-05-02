package com.rick.movie.data_movie.data.repository.article

import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_movie.ArticleUserData
import com.rick.movie.data_movie.data.repository.logArticleFavoriteToggled
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserArticleDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper
) : UserArticleDataRepository {

    override val userData: Flow<ArticleUserData>
        get() = preferencesDataSource.articleUserData

    override suspend fun setNyTimesArticleFavoriteId(articleId: String, isFavorite: Boolean) {
        preferencesDataSource.setNyTimesArticlesFavoriteIds(articleId, isFavorite)
        analyticsHelper.logArticleFavoriteToggled(articleId.toString(), isFavorite)
    }
}