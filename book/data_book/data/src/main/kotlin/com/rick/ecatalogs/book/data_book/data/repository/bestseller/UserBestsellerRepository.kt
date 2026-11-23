package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserBestsellerRepository {

    /**
     * Returns available bestseller books as a stream
     */
    fun observeBestseller(
        apiKey: String,
        genre: String,
        date: String,
        viewModelScope: CoroutineScope
    ): Flow<List<UserBestseller>>

    /**
     * Returns the user's favorite bestseller books as a stream
     * */
    fun observeBestsellerFavorite(): Flow<List<UserBestseller>>
}