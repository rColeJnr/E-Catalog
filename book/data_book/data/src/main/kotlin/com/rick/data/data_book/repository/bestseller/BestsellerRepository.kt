package com.rick.data.data_book.repository.bestseller

import com.rick.data.model_book.bestseller.Bestseller
import kotlinx.coroutines.flow.Flow

//data class BestsellerQuery(
//
//)

/**
 * Data layer implementation for [Bestseller]
 * */

interface BestsellerRepository {

    /**
     * Returns available bestseller books that match the specified [query]
     */
    fun getBestseller(
        bookGenre: String,
        apiKey: String,
        date: String = "current"
    ): Flow<List<Bestseller>>

    fun getBestsellerFavorites(favorites: Set<String>): Flow<List<Bestseller>>
}