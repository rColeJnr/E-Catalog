package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import kotlinx.coroutines.flow.first
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.data.model.isAdult
import com.rick.ecatalogs.anime.data_anime.network.model.isStale
import com.rick.ecatalogs.anime.data_anime.database.JikanTransactionRunner
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeDao
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import com.rick.ecatalogs.anime.data_anime.database.model.asAnime
import com.rick.ecatalogs.anime.data_anime.model.Anime
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class AnimeRepositoryImpl @Inject constructor(
    private val keysDao: AnimeRemoteKeysDao,
    private val network: JikanNetworkDataSource,
    private val animeDao: AnimeDao,
    private val transactionRunner: JikanTransactionRunner,
    private val listCache: ListCache,
    private val preferences: PreferencesDataSource,
) : AnimeRepository {
    override fun getAnimes(): Flow<PagingData<AnimeEntity>> {
        val pagingSourceFactory = { animeDao.getAnime() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 20,
                initialLoadSize = 60
            ),
            remoteMediator = AnimeRemoteMediator(
                network = network,
                animeDao = animeDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                listCache = listCache,
                favoriteIds = { preferences.animeUserData.first().animeFavoriteIds },
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    override suspend fun syncFirstPage(): List<Anime> {
        val response = network.fetchTopAnime(ANIME_FIRST_PAGE)
        val items = response.data
        val fetchedIds = items.map(AnimeNetwork::id).toSet()
        if (fetchedIds.isEmpty()) return emptyList()

        if (fetchedIds != keysDao.firstPageIds().toSet()) {
            writeAnimePage(
                page = ANIME_FIRST_PAGE,
                items = items,
                isRefresh = true,
                animeDao = animeDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                keepIds = preferences.animeUserData.first().animeFavoriteIds,
            )
        }
        // The first page was just checked against the server: the stored list is current
        if (!response.meta.isStale) listCache.markRefreshed(CachedList.ANIME)

        val previouslySeen = preferences.notifiedTrendingAnimeIds()
        preferences.setNotifiedTrendingAnimeIds(fetchedIds)
        if (previouslySeen.isEmpty()) return emptyList()
        return items.filter { it.id !in previouslySeen }.map { it.asEntity().asAnime() }
    }

    override fun getAnimeFavorites(favorites: Set<Int>): Flow<List<Anime>> =
        animeDao.getAnimeWithFilters(
            filterById = true,
            filterIds = favorites
        )
            .map { animeEntities -> animeEntities.map { it.asAnime() } }

    override fun searchAnimes(query: String): Flow<List<Anime>> = channelFlow {
        try {
            val searchResponse = network.searchAnime(query).data
                .filterNot { it.isAdult() }
                .take(SEARCH_RESULT_LIMIT)

            // Outside the catalog list: a stored row (and its place in the list) is kept
            animeDao.insertOutsideCatalog(searchResponse.map(AnimeNetwork::asEntity))

            // appending '%' so we can allow other characters to be before and after the query string
            val dbQuery = "%${query.replace(' ', '%')}%"
            animeDao.getAnimeWithFilters(
                filterByQuery = true,
                query = dbQuery
            )
                .collectLatest { articleEntities -> send(articleEntities.map(AnimeEntity::asAnime)) }

        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage ?: "IOException")
        } catch (e: HttpException) {
            Log.e(TAG, e.localizedMessage ?: "HTTPException")
        }
    }
}

private const val TAG = "AnimeRepositoryImpl"
/** Jikan returns at most 25 results per page. */
private const val SEARCH_RESULT_LIMIT = 25
