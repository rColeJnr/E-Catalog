package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import com.rick.ecatalogs.anime.data_anime.model.AnimeUserData
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