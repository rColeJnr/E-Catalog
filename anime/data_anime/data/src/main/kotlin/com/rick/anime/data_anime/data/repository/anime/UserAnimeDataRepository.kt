package com.rick.anime.data_anime.data.repository.anime

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