package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.book.data_book.data.logGutenbergFavoriteToggled
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergUserData
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