package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import android.util.Log
import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.data.common.Dispatcher
import com.rick.ecatalogs.data.common.EcsDispatchers
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeDao
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeFtsDao
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import com.rick.ecatalogs.anime.data_anime.database.model.asFtsEntity
import com.rick.ecatalogs.anime.data_anime.model.AnimeSearchResult
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeNetwork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class AnimeSearchRepositoryImpl @Inject constructor(
    private val animeDao: AnimeDao,
    private val animeFtsDao: AnimeFtsDao,
    private val network: JikanNetworkDataSource,
    @Dispatcher(EcsDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : AnimeSearchRepository {

    override suspend fun populateFtsAnimeData(query: String) {
        try {
            val searchResponse = network.searchAnime(
                query = query,
                max_score = 10
            ).data

            withContext(ioDispatcher) {
                animeDao.clearAnime(searchResponse.map(AnimeNetwork::id))

                animeDao.upsertAnime(searchResponse.map(AnimeNetwork::asEntity))

                animeFtsDao.upsertAnimes(
                    animeDao.getAnimeWithFilters()
                        .first()
                        .map(AnimeEntity::asFtsEntity),
                )
            }

        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage ?: "IOException")
        } catch (e: HttpException) {
            Log.e(TAG, e.localizedMessage ?: "HTTPException")
        }
    }

    override fun animeSearchContents(searchQuery: String): Flow<AnimeSearchResult> {
        TODO("Not yet implemented")
    }

    override fun getAnimeSearchContentsCount(): Flow<Int> {
        TODO("Not yet implemented")
    }
}

private const val TAG = "AnimeRepositoryImpl"