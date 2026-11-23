package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.model.bestseller.Bestseller
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre
import kotlinx.coroutines.flow.Flow

//data class BestsellerQuery(
//
//)

/**
 * Data layer implementation for [Bestseller]
 * */

interface BestsellerRepository {

    /**
     * Returns available bestseller books that match the specified [bookGenre]
     */
    fun getBestseller(
        bookGenre: String,
        date: String = "current"
    ): Flow<List<Bestseller>>

    /**
     * Returns user favorite bestseller books
     */
    fun getBestsellerFavorites(favorites: Set<String>): Flow<List<Bestseller>>

    /**
     * Fetches every [BestsellerGenre] list and replaces a stored list when its books changed.
     *
     * @return per genre, the books that are new since the previous sync (genres seen for the first
     * time are recorded without being returned).
     * @throws java.io.IOException or retrofit2.HttpException on network failure.
     */
    suspend fun syncAllGenres(): Map<BestsellerGenre, List<Bestseller>>
}
