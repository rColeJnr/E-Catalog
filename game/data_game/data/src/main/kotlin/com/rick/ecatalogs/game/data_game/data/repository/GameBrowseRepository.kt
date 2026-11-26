package com.rick.ecatalogs.game.data_game.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.game.data_game.data.model.asGame
import com.rick.ecatalogs.game.data_game.data.model.asGenre
import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.game.data_game.model.GameGenre
import com.rick.ecatalogs.game.data_game.network.IgdbNetworkDataSource
import com.rick.ecatalogs.game.data_game.network.IgdbQueries
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

/** The browse rows above the catalog and the genre filter. */
interface GameBrowseRepository {
    fun comingSoon(): Flow<List<Game>>
    fun recentlyReleased(): Flow<List<Game>>
    suspend fun genres(): List<GameGenre>
    /** Most rated games of a genre, straight from IGDB (not stored). */
    fun gamesByGenre(genreId: Int): Flow<PagingData<Game>>

    /**
     * Background sync: downloads "coming soon" again and returns the games that weren't in it
     * at the previous sync (empty the first time, so installing doesn't notify the whole list).
     * Throws when offline, so the sync can retry.
     */
    suspend fun syncComingSoon(): List<Game>
}

/**
 * Rows and genres are kept as JSON files in `cacheDir/game_browse` and reused while
 * [ListCache] says they are fresh (the sync interval), like TmdbBrowseRepositoryImpl. Offline,
 * the stored copy is used however old it is.
 */
@Singleton
internal class GameBrowseRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val network: IgdbNetworkDataSource,
    private val listCache: ListCache,
    private val preferences: PreferencesDataSource,
) : GameBrowseRepository {

    private val cacheDir = File(context.cacheDir, "game_browse")
    private val gson = Gson()

    override fun comingSoon(): Flow<List<Game>> = flow {
        val now = nowSeconds()
        val games = cached(CachedList.GAMES_COMING_SOON, gameListType) {
            network.fetchComingSoon(now).map { it.asGame() }
        }
        // A stored row may hold games that have come out since
        emit(games.filter { (it.releaseDate ?: 0L) > now })
    }

    override fun recentlyReleased(): Flow<List<Game>> = flow {
        emit(cached(CachedList.GAMES_RECENTLY_RELEASED, gameListType) {
            network.fetchRecentlyReleased(nowSeconds()).map { it.asGame() }
        })
    }

    override suspend fun genres(): List<GameGenre> =
        cached(CachedList.GAME_GENRES, genreListType) { network.fetchGenres().mapNotNull { it.asGenre() } }

    override fun gamesByGenre(genreId: Int): Flow<PagingData<Game>> =
        Pager(PagingConfig(pageSize = IgdbQueries.ROW_SIZE, enablePlaceholders = false)) {
            GenrePagingSource { page -> network.fetchGamesByGenre(genreId, page).map { it.asGame() } }
        }.flow

    override suspend fun syncComingSoon(): List<Game> {
        if (!network.isConfigured) return emptyList()
        val games = network.fetchComingSoon(nowSeconds()).map { it.asGame() }
        withContext(Dispatchers.IO) {
            cacheDir.mkdirs()
            File(cacheDir, "${CachedList.GAMES_COMING_SOON}.json").writeText(gson.toJson(games, gameListType))
        }
        listCache.markRefreshed(CachedList.GAMES_COMING_SOON)
        val ids = games.map(Game::id).toSet()
        if (ids.isEmpty()) return emptyList()
        val previouslySeen = preferences.notifiedComingSoonGameIds()
        preferences.setNotifiedComingSoonGameIds(ids)
        if (previouslySeen.isEmpty()) return emptyList()
        return games.filter { it.id !in previouslySeen }
    }

    private suspend fun <T> cached(key: String, type: Type, fetch: suspend () -> List<T>): List<T> =
        withContext(Dispatchers.IO) {
            val file = File(cacheDir, "$key.json")
            val stored: List<T>? = if (file.exists()) {
                runCatching { gson.fromJson<List<T>>(file.readText(), type) }.getOrNull()
            } else {
                null
            }
            if (stored != null && listCache.isFresh(key)) return@withContext stored
            try {
                fetch().also { items ->
                    cacheDir.mkdirs()
                    file.writeText(gson.toJson(items, type))
                    listCache.markRefreshed(key)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                stored ?: emptyList()
            }
        }

    private fun nowSeconds() = System.currentTimeMillis() / 1_000

    private companion object {
        val gameListType: Type = object : TypeToken<List<Game>>() {}.type
        val genreListType: Type = object : TypeToken<List<GameGenre>>() {}.type
    }
}

private class GenrePagingSource(
    private val fetch: suspend (page: Int) -> List<Game>,
) : PagingSource<Int, Game>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val page = params.key ?: 1
        return try {
            val games = fetch(page)
            LoadResult.Page(
                data = games,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (games.isEmpty() || page >= MAX_GENRE_PAGES) null else page + 1,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? =
        state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey?.plus(1) }
}

private const val MAX_GENRE_PAGES = 25
