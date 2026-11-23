package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.model.bestseller.Bestseller
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
        apiKey: String,
        date: String = "current"
    ): Flow<List<Bestseller>>

    /**
     * Returns user favorite bestseller books
     */
    fun getBestsellerFavorites(favorites: Set<String>): Flow<List<Bestseller>>
}