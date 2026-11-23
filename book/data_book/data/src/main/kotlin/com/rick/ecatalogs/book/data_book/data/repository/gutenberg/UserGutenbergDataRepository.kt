package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergUserData
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