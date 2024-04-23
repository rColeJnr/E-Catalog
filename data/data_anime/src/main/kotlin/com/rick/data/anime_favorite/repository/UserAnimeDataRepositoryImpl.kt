package com.rick.data.anime_favorite.repository

import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_anime.AnimeUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserAnimeDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : UserAnimeDataRepository {
    override val animeUserData: Flow<AnimeUserData>
        get() = preferencesDataSource.animeUserData

    override suspend fun setAnimeFavoriteId(animeId: Int, isFavorite: Boolean) {
        preferencesDataSource.setAnimeFavoriteIds(animeId, isFavorite)
    }
}