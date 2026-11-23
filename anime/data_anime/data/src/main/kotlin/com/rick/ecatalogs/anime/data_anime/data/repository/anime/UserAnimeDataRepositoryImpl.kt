package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import com.rick.ecatalogs.anime.data_anime.data.logAnimeFavoriteToggled
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.anime.data_anime.model.AnimeUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserAnimeDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper
) : UserAnimeDataRepository {
    override val animeUserData: Flow<AnimeUserData>
        get() = preferencesDataSource.animeUserData

    override suspend fun setAnimeFavoriteId(animeId: Int, isFavorite: Boolean) {
        preferencesDataSource.setAnimeFavoriteIds(animeId, isFavorite)
        analyticsHelper.logAnimeFavoriteToggled(animeId.toString(), isFavorite)
    }
}