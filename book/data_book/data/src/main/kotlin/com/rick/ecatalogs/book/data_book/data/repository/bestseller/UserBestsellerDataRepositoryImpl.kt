package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.book.data_book.data.logBestsellerFavoriteToggled
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserBestsellerDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper
) : UserBestsellerDataRepository {
    override val userData: Flow<BestsellerUserData>
        get() = preferencesDataSource.bestsellerUserData

    override suspend fun setBestsellerFavoriteId(bestsellerId: String, isFavorite: Boolean) {
        preferencesDataSource.setBestsellerFavoriteIds(bestsellerId, isFavorite)
        analyticsHelper.logBestsellerFavoriteToggled(bestsellerId, isFavorite)
    }
}