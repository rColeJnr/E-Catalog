package com.rick.ecatalogs.game.data_game.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.game.data_game.data.model.asGame
import com.rick.ecatalogs.game.data_game.database.GameTransactionRunner
import com.rick.ecatalogs.game.data_game.database.dao.GameDao
import com.rick.ecatalogs.game.data_game.database.dao.GameRemoteKeysDao
import com.rick.ecatalogs.game.data_game.database.model.GameEntity
import com.rick.ecatalogs.game.data_game.database.model.GameRemoteKeys
import com.rick.ecatalogs.game.data_game.database.model.asEntity
import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.game.data_game.network.IgdbNetworkDataSource
import retrofit2.HttpException
import java.io.IOException

/**
 * The "popular now" catalog: pages of IGDB's popularity list stored in Room, in order
 * ([catalogPosition]). The stored list is reused while it's fresh ([ListCache]); a refresh
 * replaces it but keeps favorite games, like the other catalogs.
 */
@OptIn(ExperimentalPagingApi::class)
internal class GameRemoteMediator(
    private val network: IgdbNetworkDataSource,
    private val gameDao: GameDao,
    private val keysDao: GameRemoteKeysDao,
    private val transactionRunner: GameTransactionRunner,
    private val listCache: ListCache,
    private val favoriteIds: suspend () -> Set<Int>,
) : RemoteMediator<Int, GameEntity>() {

    override suspend fun initialize(): InitializeAction =
        if (listCache.isFresh(CachedList.GAMES)) InitializeAction.SKIP_INITIAL_REFRESH
        else InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, GameEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> FIRST_PAGE
            // The list is always loaded from its first page
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val last = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                val keys = last?.let { keysDao.remoteKeysId(it.id) }
                keys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = keys != null)
            }
        }
        return try {
            val games = network.fetchPopularGames(page).map { it.asGame() }
            val endOfPaginationReached = writeGamePage(
                page = page,
                games = games,
                isRefresh = loadType == LoadType.REFRESH,
                gameDao = gameDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                keepIds = if (loadType == LoadType.REFRESH) favoriteIds() else emptySet(),
            )
            if (loadType == LoadType.REFRESH) listCache.markRefreshed(CachedList.GAMES)
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

/**
 * Writes one page of the catalog; a refresh first replaces the stored list, keeping [keepIds]
 * (the favorites). Shared by the mediator and the background sync. Returns whether it was the last page.
 */
internal suspend fun writeGamePage(
    page: Int,
    games: List<Game>,
    isRefresh: Boolean,
    gameDao: GameDao,
    keysDao: GameRemoteKeysDao,
    transactionRunner: GameTransactionRunner,
    keepIds: Set<Int>,
): Boolean {
    val endOfPaginationReached = games.isEmpty() || page >= MAX_PAGES
    transactionRunner {
        if (isRefresh) {
            keysDao.clearRemoteKeys()
            gameDao.clearCatalog()
            gameDao.deleteOutsideCatalog(keepIds)
        }
        val prevKey = if (page == FIRST_PAGE) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        keysDao.insertAll(games.map { GameRemoteKeys(game = it.id, prevKey = prevKey, nextKey = nextKey) })
        gameDao.upsertGames(games.mapIndexed { index, game -> game.asEntity(catalogPosition(page, index)) })
    }
    return endOfPaginationReached
}

internal fun catalogPosition(page: Int, index: Int): Int = page * CATALOG_PAGE_STRIDE + index

internal const val FIRST_PAGE = 1
private const val CATALOG_PAGE_STRIDE = 1_000
/** 10 pages of 50: the 500 most popular games */
private const val MAX_PAGES = 10
