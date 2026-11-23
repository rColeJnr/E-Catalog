package com.rick.ecatalogs.movie.data_movie.data.repository.article

import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.movie.data_movie.model.ArticleUserData
import com.rick.ecatalogs.movie.data_movie.data.repository.logArticleFavoriteToggled
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