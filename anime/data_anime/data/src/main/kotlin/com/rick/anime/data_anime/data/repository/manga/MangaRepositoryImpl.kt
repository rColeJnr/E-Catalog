package com.rick.anime.data_anime.data.repository.manga

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.anime.data_anime.data.model.asEntity
import com.rick.data.database_anime.dao.MangaDao
import com.rick.data.database_anime.dao.MangaRemoteKeysDao
import com.rick.data.database_anime.model.MangaEntity
import com.rick.data.database_anime.model.asManga
import com.rick.data.model_anime.Manga
import com.rick.data.network_anime.JikanNetworkDataSource
import com.rick.data.network_anime.model.MangaNetwork
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
) : MangaRepository {
    override fun getMangas(): Flow<PagingData<MangaEntity>> {
        val pagingSourceFactory = { mangaDao.getManga() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = MangaRemoteMediator(
                network = network,
                mangaDao = mangaDao,
                keysDao = keysDao
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
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
                max_score = 10
            ).data

            mangaDao.clearManga(searchResponse.map(MangaNetwork::id))

            mangaDao.upsertManga(searchResponse.map(MangaNetwork::asEntity))

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