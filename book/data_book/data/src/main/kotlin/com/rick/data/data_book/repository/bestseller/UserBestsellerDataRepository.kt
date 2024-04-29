package com.rick.data.data_book.repository.bestseller

import com.rick.data.model_book.bestseller.BestsellerUserData
import kotlinx.coroutines.flow.Flow

interface UserBestsellerDataRepository {

    /**
     * Stream of [BestsellerUserData]
     */
    val userData: Flow<BestsellerUserData>

    /**
     * updates the favorite status of the resource
     */
    suspend fun setBestsellerFavoriteId(bestsellerId: String, isFavorite: Boolean)
}