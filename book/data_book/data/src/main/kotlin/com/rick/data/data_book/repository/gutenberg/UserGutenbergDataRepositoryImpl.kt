package com.rick.data.data_book.repository.gutenberg

import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.data_book.logGutenbergFavoriteToggled
import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_book.gutenberg.GutenbergUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserGutenbergDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper
) : UserGutenbergDataRepository {
    override val userData: Flow<GutenbergUserData>
        get() = preferencesDataSource.gutenbergUserData

    override suspend fun setGutenbergFavoriteId(gutenbergId: Int, isFavorite: Boolean) {
        preferencesDataSource.setGutenbergFavoriteIds(gutenbergId, isFavorite)
        analyticsHelper.logGutenbergFavoriteToggled(gutenbergId.toString(), isFavorite)
    }


}