package com.rick.data.anime_favorite.repository.anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.database_anime.dao.AnimeDao
import com.rick.data.database_anime.dao.AnimeRemoteKeysDao
import com.rick.data.database_anime.model.AnimeEntity
import com.rick.data.database_anime.model.asAnime
import com.rick.data.model_anime.Anime
import com.rick.data.network_anime.JikanNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AnimeRepositoryImpl @Inject constructor(
    private val keysDao: AnimeRemoteKeysDao,
    private val api: JikanNetworkDataSource,
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
                initialLoadSize = 40
            ),
            remoteMediator = AnimeRemoteMediator(
                network = api,
                animeDao = animeDao,
                keysDao = keysDao
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    override fun getAnimeFavorites(favorites: Set<Int>): Flow<List<Anime>> =
        animeDao.getAnimeWithFilters(favorites)
            .map { animeEntities -> animeEntities.map { it.asAnime() } }
}