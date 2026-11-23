package com.rick.ecatalogs.anime.data_anime.data.repository.manga

import com.rick.ecatalogs.anime.data_anime.model.MangaUserData
import kotlinx.coroutines.flow.Flow

interface UserMangaDataRepository {

    /**
     * Stream of [MangaUserData]
     */
    val mangaUserData: Flow<MangaUserData>

    /**
     * updates the favorite status of the resource
     */
    suspend fun setMangaFavoriteId(mangaId: Int, isFavorite: Boolean)
}