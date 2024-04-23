package com.rick.data.data_book.repository.bestseller

import com.rick.data.model_book.bestseller.UserBestseller
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