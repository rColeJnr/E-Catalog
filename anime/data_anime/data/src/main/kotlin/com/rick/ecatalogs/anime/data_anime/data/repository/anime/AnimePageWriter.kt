package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.data.repository.catalogPosition
import com.rick.ecatalogs.anime.data_anime.database.JikanTransactionRunner
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeDao
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeRemoteKeys
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeNetwork

internal const val ANIME_FIRST_PAGE = 1

/**
 * Stores one page of the top anime list. A refresh rewrites the list: rows that leave it are
 * deleted unless they're in [keepIds] (the favorites). Used by the catalog's [AnimeRemoteMediator]
 * and by the background sync, so both write the list the same way.
 *
 * Returns whether this was the last page (Jikan answers past the end with an empty page).
 */
internal suspend fun writeAnimePage(
    page: Int,
    items: List<AnimeNetwork>,
    isRefresh: Boolean,
    animeDao: AnimeDao,
    keysDao: AnimeRemoteKeysDao,
    transactionRunner: JikanTransactionRunner,
    keepIds: Set<Int>,
): Boolean {
    val endOfPaginationReached = items.isEmpty()
    transactionRunner {
        if (isRefresh) {
            keysDao.clearRemoteKeys()
            // Written anew: rows leave the list, and those that aren't favorites are deleted
            animeDao.clearCatalog()
            animeDao.deleteOutsideCatalog(keepIds)
        }
        val prevKey = if (page == ANIME_FIRST_PAGE) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        keysDao.insertAll(items.map { AnimeRemoteKeys(anime = it.id, prevKey = prevKey, nextKey = nextKey) })
        animeDao.upsertAnime(
            items.mapIndexed { index, item -> item.asEntity().copy(catalogPosition = catalogPosition(page, index)) },
        )
    }
    return endOfPaginationReached
}
