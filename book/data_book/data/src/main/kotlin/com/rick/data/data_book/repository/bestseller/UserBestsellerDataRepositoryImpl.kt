package com.rick.data.data_book.repository.bestseller

import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.data_book.logBestsellerFavoriteToggled
import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_book.bestseller.BestsellerUserData
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