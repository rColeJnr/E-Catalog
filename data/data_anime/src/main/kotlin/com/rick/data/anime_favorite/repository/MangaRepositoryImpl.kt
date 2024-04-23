package com.rick.data.anime_favorite.repository

import android.nfc.tech.MifareUltralight
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.database_anime.dao.MangaDao
import com.rick.data.database_anime.dao.MangaRemoteKeysDao
import com.rick.data.database_anime.model.MangaEntity
import com.rick.data.database_anime.model.asManga
import com.rick.data.model_anime.Manga
import com.rick.data.network_anime.JikanNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
                pageSize = MifareUltralight.PAGE_SIZE,
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
        mangaDao.getMangaWithFilters(favorites)
            .map { mangaEntities -> mangaEntities.map { it.asManga() } }
}