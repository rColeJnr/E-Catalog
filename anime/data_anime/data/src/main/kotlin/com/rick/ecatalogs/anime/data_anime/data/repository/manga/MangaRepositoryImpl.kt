package com.rick.ecatalogs.anime.data_anime.data.repository.manga

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
import com.rick.ecatalogs.anime.data_anime.database.JikanTransactionRunner
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.model.MangaEntity
import com.rick.ecatalogs.anime.data_anime.database.model.asManga
import com.rick.ecatalogs.anime.data_anime.model.Manga
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.model.MangaNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


internal class MangaRepositoryImpl @Inject constructor(
    private val keysDao: MangaRemoteKeysDao,
    private val mangaDao: MangaDao,
    private val network: JikanNetworkDataSource,
    private val transactionRunner: JikanTransactionRunner,
    private val listCache: ListCache,
    private val preferences: PreferencesDataSource,
) : MangaRepository {
    override fun getMangas(): Flow<PagingData<MangaEntity>> {
        val pagingSourceFactory = { mangaDao.getManga() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 20,
                initialLoadSize = 60
            ),
            remoteMediator = MangaRemoteMediator(
                network = network,
                mangaDao = mangaDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                listCache = listCache,
                favoriteIds = { preferences.mangaUserData.first().mangaFavoriteIds },
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun syncFirstPage(): List<Manga> {
        val items = network.fetchTopManga(MANGA_FIRST_PAGE).data
        val fetchedIds = items.map(MangaNetwork::id).toSet()
        if (fetchedIds.isEmpty()) return emptyList()

        if (fetchedIds != keysDao.firstPageIds().toSet()) {
            writeMangaPage(
                page = MANGA_FIRST_PAGE,
                items = items,
                isRefresh = true,
                mangaDao = mangaDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                keepIds = preferences.mangaUserData.first().mangaFavoriteIds,
            )
        }
        // The first page was just checked against the server: the stored list is current
        listCache.markRefreshed(CachedList.MANGA)

        val previouslySeen = preferences.notifiedTrendingMangaIds()
        preferences.setNotifiedTrendingMangaIds(fetchedIds)
        if (previouslySeen.isEmpty()) return emptyList()
        return items.filter { it.id !in previouslySeen }.map { it.asEntity().asManga() }
    }

    override fun getMangaFavorites(favorites: Set<Int>): Flow<List<Manga>> =
        mangaDao.getMangaWithFilters(
            filterById = true, filterIds = favorites
        )
            .map { mangaEntities -> mangaEntities.map { it.asManga() } }

    override fun searchManga(query: String): Flow<List<Manga>> = channelFlow {
        try {
            val searchResponse = network.searchManga(
                query = query,
                limit = SEARCH_RESULT_LIMIT
            ).data

            // Outside the catalog list: a stored row (and its place in the list) is kept
            mangaDao.insertOutsideCatalog(searchResponse.map(MangaNetwork::asEntity))

            // appending '%' so we can allow other characters to be before and after the query string
            val dbQuery = "%${query.replace(' ', '%')}%"
            mangaDao.getMangaWithFilters(
                filterByQuery = true,
                query = dbQuery
            )
                .collectLatest { articleEntities -> send(articleEntities.map(MangaEntity::asManga)) }

        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage ?: "IOException")
        } catch (e: HttpException) {
            Log.e(TAG, e.localizedMessage ?: "HTTPException")
        }
    }
}

private const val TAG = "MangaRepositoryImpl"
/** Jikan returns at most 25 results per page. */
private const val SEARCH_RESULT_LIMIT = 25
