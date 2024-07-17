package com.rick.anime.data_anime.data.repository.anime

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.anime.data_anime.data.model.asEntity
import com.rick.data.database_anime.dao.AnimeDao
import com.rick.data.database_anime.dao.AnimeRemoteKeysDao
import com.rick.data.database_anime.model.AnimeEntity
import com.rick.data.database_anime.model.asAnime
import com.rick.data.model_anime.Anime
import com.rick.data.network_anime.JikanNetworkDataSource
import com.rick.data.network_anime.model.AnimeNetwork
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
) : AnimeRepository {
    override fun getAnimes(): Flow<PagingData<AnimeEntity>> {
        val pagingSourceFactory = { animeDao.getAnime() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = AnimeRemoteMediator(
                network = network,
                animeDao = animeDao,
                keysDao = keysDao
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
                max_score = 10
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