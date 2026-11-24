package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import androidx.paging.PagingData
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserBestsellerRepository {

    /**
     * Returns available bestseller books as a stream
     */
    fun observeBestseller(
        genre: String,
        date: String,
        viewModelScope: CoroutineScope
    ): Flow<List<UserBestseller>>

    /**
     * Returns the user's favorite bestseller books as a stream
     * */
    fun observeBestsellerFavorite(): Flow<List<UserBestseller>>

    /** The genre's list, paged, with the user's favorites applied. */
    fun observeBestsellerPaging(
        genre: String,
        scope: CoroutineScope,
    ): Flow<PagingData<UserBestseller>>

    /** One book with the user's favorite state, for the details screen. */
    fun observeBestsellerById(id: String): Flow<UserBestseller?>
}