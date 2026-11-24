package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import com.rick.ecatalogs.data.datastore.ListCache
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
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
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    override fun getAnimeFavorites(favorites: Set<Int>): Flow<List<Anime>> =
        animeDao.getAnimeWithFilters(
            filterById = true,
            filterIds = favorites
        )
            .map { animeEntities -> animeEntities.map { it.asAnime() } }

    override fun searchAnimes(query: String): Flow<List<Anime>> = channelFlow {
        try {
            val searchResponse = network.searchAnime(
                query = query,
                limit = SEARCH_RESULT_LIMIT
            ).data

            animeDao.clearAnime(searchResponse.map(AnimeNetwork::id))

            animeDao.upsertAnime(searchResponse.map(AnimeNetwork::asEntity))

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
