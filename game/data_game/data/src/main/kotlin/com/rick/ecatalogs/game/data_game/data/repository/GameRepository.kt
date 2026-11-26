package com.rick.ecatalogs.game.data_game.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.game.data_game.data.model.asGame
import com.rick.ecatalogs.game.data_game.data.model.asGameDetails
import com.rick.ecatalogs.game.data_game.database.GameTransactionRunner
import com.rick.ecatalogs.game.data_game.database.dao.GameDao
import com.rick.ecatalogs.game.data_game.database.dao.GameRemoteKeysDao
import com.rick.ecatalogs.game.data_game.database.model.GameEntity
import com.rick.ecatalogs.game.data_game.database.model.asEntity
import com.rick.ecatalogs.game.data_game.database.model.asGame
import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.game.data_game.model.GameDetails
import com.rick.ecatalogs.game.data_game.network.IgdbNetworkDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface GameRepository {
    /** The "popular now" catalog, stored in Room. */
    fun getPopularGames(): Flow<PagingData<Game>>

    /** Stored games with these ids (favorites), by name. */
    fun getGames(ids: Set<Int>): Flow<List<Game>>

    /** Searches IGDB; the results are stored (outside the catalog) and matched by name. */
    fun searchGames(query: String): Flow<List<Game>>

    /** Fetched every time the details screen opens; also stores the game so it can be a favorite. */
    suspend fun gameDetails(id: Int): GameDetails?

    /**
     * Background sync: fetches the first "popular now" page and rewrites the stored one only when
     * it changed (favorites are kept). Throws when offline, so the sync can retry.
     */
    suspend fun syncFirstPage()
}

internal class GameRepositoryImpl @Inject constructor(
    private val network: IgdbNetworkDataSource,
    private val gameDao: GameDao,
    private val keysDao: GameRemoteKeysDao,
    private val transactionRunner: GameTransactionRunner,
    private val listCache: ListCache,
    private val preferences: PreferencesDataSource,
) : GameRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPopularGames(): Flow<PagingData<Game>> = Pager(
        config = PagingConfig(pageSize = 50, prefetchDistance = 20, initialLoadSize = 50, enablePlaceholders = false),
        remoteMediator = GameRemoteMediator(
            network = network,
            gameDao = gameDao,
            keysDao = keysDao,
            transactionRunner = transactionRunner,
            listCache = listCache,
            favoriteIds = { preferences.gameUserData.first().gameFavoriteIds },
        ),
        pagingSourceFactory = { gameDao.getGames() },
    ).flow.map { page -> page.map(GameEntity::asGame) }

    override fun getGames(ids: Set<Int>): Flow<List<Game>> =
        gameDao.getGamesByIds(ids).map { entities -> entities.map(GameEntity::asGame) }

    override fun searchGames(query: String): Flow<List<Game>> = channelFlow {
        try {
            val results = network.searchGames(query).map { it.asGame() }
            // Outside the catalog list: a stored row (and its place in the list) is kept
            gameDao.insertOutsideCatalog(results.map { it.asEntity() })
        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage ?: "IOException") // offline: search what's stored
        } catch (e: HttpException) {
            Log.e(TAG, e.localizedMessage ?: "HttpException")
        }
        val dbQuery = "%${query.trim().replace(' ', '%')}%"
        gameDao.searchGames(dbQuery).collectLatest { entities -> send(entities.map(GameEntity::asGame)) }
    }

    override suspend fun syncFirstPage() {
        // Without credentials every request fails: don't make the whole sync retry for that
        if (!network.isConfigured) return
        val games = network.fetchPopularGames(FIRST_PAGE).map { it.asGame() }
        if (games.isEmpty()) return
        if (games.map(Game::id).toSet() != keysDao.firstPageIds().toSet()) {
            writeGamePage(
                page = FIRST_PAGE,
                games = games,
                isRefresh = true,
                gameDao = gameDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                keepIds = preferences.gameUserData.first().gameFavoriteIds,
            )
        }
        listCache.markRefreshed(CachedList.GAMES)
    }

    override suspend fun gameDetails(id: Int): GameDetails? = try {
        network.fetchGameDetails(id)?.asGameDetails()?.also { details ->
            gameDao.insertOutsideCatalog(listOf(details.game.asEntity()))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(TAG, e.localizedMessage ?: "Details failed")
        null
    }
}

private const val TAG = "GameRepository"
