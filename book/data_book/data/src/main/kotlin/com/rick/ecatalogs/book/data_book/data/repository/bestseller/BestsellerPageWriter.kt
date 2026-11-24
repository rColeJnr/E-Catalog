package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.data.model.asBestsellerEntity
import com.rick.ecatalogs.book.data_book.database.BookTransactionRunner
import com.rick.ecatalogs.book.data_book.database.dao.BestsellerDao
import com.rick.ecatalogs.book.data_book.database.dao.BestsellerRemoteKeysDao
import com.rick.ecatalogs.book.data_book.database.model.BestsellerRemoteKeys
import com.rick.ecatalogs.book.data_book.network.model.BestsellerResponse

/** NYT pages bestseller lists in steps of 20 (`offset` = 0, 20, 40, …). */
internal const val BESTSELLER_PAGE_SIZE = 20

/** Remote keys are per list: the same book (ISBN) can be on several lists. */
internal fun bestsellerRemoteKeyId(genre: String, isbn: String) = "$genre|$isbn"

/** Whether the list has no books after this page. */
internal fun bestsellerEndReached(offset: Int, pageSize: Int, totalResults: Int): Boolean =
    pageSize == 0 || offset + pageSize >= totalResults

/**
 * Stores one page of a list with its remote keys (the next page's offset). A refresh (offset 0)
 * first clears the list and its keys. Used by the Paging mediator and by background sync, so
 * both keep the list and its keys consistent.
 *
 * @return true when this was the list's last page.
 */
internal suspend fun writeBestsellerPage(
    genre: String,
    offset: Int,
    response: BestsellerResponse,
    isRefresh: Boolean,
    bestsellerDao: BestsellerDao,
    keysDao: BestsellerRemoteKeysDao,
    transactionRunner: BookTransactionRunner,
): Boolean {
    val books = response.results.books
    val endReached = bestsellerEndReached(offset, books.size, response.num_results)
    transactionRunner {
        if (isRefresh) {
            bestsellerDao.clearBestsellers(genre)
            keysDao.clearRemoteKeys(genre)
        }
        val prevKey = if (offset == 0) null else offset - BESTSELLER_PAGE_SIZE
        val nextKey = if (endReached) null else offset + BESTSELLER_PAGE_SIZE
        keysDao.insertAll(
            books.map {
                BestsellerRemoteKeys(
                    id = bestsellerRemoteKeyId(genre, it.id),
                    prevKey = prevKey,
                    nextKey = nextKey,
                )
            }
        )
        bestsellerDao.upsertBestsellers(books.map { it.asBestsellerEntity(genre) })
    }
    return endReached
}
