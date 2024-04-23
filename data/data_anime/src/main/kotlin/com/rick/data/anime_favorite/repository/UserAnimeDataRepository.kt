package com.rick.data.anime_favorite.repository

import com.rick.data.model_anime.AnimeUserData
import kotlinx.coroutines.flow.Flow

interface UserAnimeDataRepository {

    /**
     * Stream of [AnimeUserData]
     */
    val animeUserData: Flow<AnimeUserData>

    /**
     * updates the favorite status of the resource
     */
    suspend fun setAnimeFavoriteId(animeId: Int, isFavorite: Boolean)
}