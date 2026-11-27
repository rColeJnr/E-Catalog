package com.rick.ecatalogs.anime.data_anime.data.repository.manga

import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.data.repository.catalogPosition
import com.rick.ecatalogs.anime.data_anime.database.JikanTransactionRunner
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.model.MangaRemoteKeys
import com.rick.ecatalogs.anime.data_anime.network.model.MangaNetwork

internal const val MANGA_FIRST_PAGE = 1

/**
 * Stores one page of the top manga list. A refresh rewrites the list: rows that leave it are
 * deleted unless they're in [keepIds] (the favorites). Used by the catalog's [MangaRemoteMediator]
 * and by the background sync, so both write the list the same way.
 *
 * Returns whether this was the last page (Jikan answers past the end with an empty page).
 */
internal suspend fun writeMangaPage(
    page: Int,
    items: List<MangaNetwork>,
    isRefresh: Boolean,
    mangaDao: MangaDao,
    keysDao: MangaRemoteKeysDao,
    transactionRunner: JikanTransactionRunner,
    keepIds: Set<Int>,
): Boolean {
    val endOfPaginationReached = items.isEmpty()
    transactionRunner {
        if (isRefresh) {
            keysDao.clearRemoteKeys()
            // Written anew: rows leave the list, and those that aren't favorites are deleted
            mangaDao.clearCatalog()
            mangaDao.deleteOutsideCatalog(keepIds)
        }
        val prevKey = if (page == MANGA_FIRST_PAGE) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        keysDao.insertAll(items.map { MangaRemoteKeys(manga = it.id, prevKey = prevKey, nextKey = nextKey) })
        mangaDao.upsertManga(
            items.mapIndexed { index, item -> item.asEntity().copy(catalogPosition = catalogPosition(page, index)) },
        )
    }
    return endOfPaginationReached
}
