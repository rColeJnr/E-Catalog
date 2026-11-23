package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerUserData
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