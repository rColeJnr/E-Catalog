package com.rick.data.anime_favorite.repository.manga

import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_anime.MangaUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserMangaDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : UserMangaDataRepository {
    override val mangaUserData: Flow<MangaUserData>
        get() = preferencesDataSource.mangaUserData

    override suspend fun setMangaFavoriteId(mangaId: Int, isFavorite: Boolean) {
        preferencesDataSource.setMangaFavoriteIds(mangaId, isFavorite)
    }
}