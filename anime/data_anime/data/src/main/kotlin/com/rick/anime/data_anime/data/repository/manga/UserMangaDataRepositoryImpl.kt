package com.rick.anime.data_anime.data.repository.manga

import com.rick.anime.data_anime.data.logMangaFavoriteToggled
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_anime.MangaUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserMangaDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper
) : UserMangaDataRepository {
    override val mangaUserData: Flow<MangaUserData>
        get() = preferencesDataSource.mangaUserData

    override suspend fun setMangaFavoriteId(mangaId: Int, isFavorite: Boolean) {
        preferencesDataSource.setMangaFavoriteIds(mangaId, isFavorite)
        analyticsHelper.logMangaFavoriteToggled(mangaId.toString(), isFavorite)
    }
}