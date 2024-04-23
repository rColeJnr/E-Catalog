package com.rick.data.data_book.repository.gutenberg

import com.rick.data.model_book.GutenbergUserData
import kotlinx.coroutines.flow.Flow

interface UserGutenbergDataRepository {

    /**
     * Stream of [GutenbergUserData]
     */
    val userData: Flow<GutenbergUserData>

    /**
     * updates the favorite status of the resource
     */
    suspend fun setGutenbergFavoriteId(gutenbergId: Int, isFavorite: Boolean)
}