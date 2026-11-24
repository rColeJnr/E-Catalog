package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import androidx.paging.PagingData
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
     * Checks every [BestsellerGenre] list with one overview request and fetches (and replaces)
     * only the lists whose books changed.
     *
     * @return per genre, the books that are new since the previous sync (genres seen for the first
     * time are recorded without being returned).
     * @throws java.io.IOException or retrofit2.HttpException on network failure.
     */
    suspend fun syncAllGenres(): Map<BestsellerGenre, List<Bestseller>>

    /** The genre's list as Paging data, downloaded page by page when it's missing or stale. */
    fun getBestsellerPaging(bookGenre: String): Flow<PagingData<Bestseller>>

    /** A stored book by ISBN, or null if it isn't stored (any more). */
    fun getBestsellerById(id: String): Flow<Bestseller?>
}
