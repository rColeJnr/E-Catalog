package com.rick.data.anime_favorite.repository.anime

import com.rick.core.Dispatcher
import com.rick.core.EcsDispatchers
import com.rick.data.database_anime.dao.AnimeDao
import com.rick.data.database_anime.dao.AnimeFtsDao
import com.rick.data.model_anime.AnimeSearchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AnimeSearchRepositoryImpl @Inject constructor(
    private val animeDao: AnimeDao,
    private val animeFtsDao: AnimeFtsDao,
    @Dispatcher(EcsDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : AnimeSearchRepository {

    override suspend fun populateFtsAnimeData() {
        withContext(ioDispatcher) {
            animeFtsDao.upsertAnimes(
                animeDao.
            )
        }
    }

    override fun animeSearchContents(searchQuery: String): Flow<AnimeSearchResult> {
        TODO("Not yet implemented")
    }

    override fun getAnimeSearchContentsCount(): Flow<Int> {
        TODO("Not yet implemented")
    }
}