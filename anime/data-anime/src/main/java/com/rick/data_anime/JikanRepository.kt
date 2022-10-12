package com.rick.data_anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data_anime.model_anime.Anime
import com.rick.data_anime.model_manga.Manga
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val PAGE_SIZE = 2

class JikanRepository @Inject constructor(
    private val api: JikanApi,
    private val db: JikanDatabase
) {

    fun fetchAnimes(): Flow<PagingData<Anime>> {
        val pagingSourceFactory = { db.animeDao.getAnimes() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = AnimeRemoteMediator(api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun fetchManga(): Flow<PagingData<Manga>> {
        val pagingSourceFactory = { db.mangaDao.getMangas() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = MangaRemoteMediator(api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

}