package com.rick.anime.data_anime.data.repository.manga

import com.rick.anime.data_anime.data.model.asExternalModel
import com.rick.data.database_anime.dao.MangaRecentSearchQueryDao
import com.rick.data.database_anime.model.MangaRecentSearchQueryEntity
import com.rick.data.model_anime.MangaRecentSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject

class MangaRecentSearchRepositoryImpl @Inject constructor(
    private val recentSearchQueryDao: MangaRecentSearchQueryDao
) : MangaRecentSearchRepository {
    override fun getMangaRecentSearchQueries(limit: Int): Flow<List<MangaRecentSearchQuery>> =
        recentSearchQueryDao.getMangaRecentSearchQueryEntities(limit).map { searchQueries ->
            searchQueries.map { it.asExternalModel() }
        }

    override suspend fun insertOrReplaceMangaRecentSearch(searchQuery: String) {
        recentSearchQueryDao.insertOrReplaceMangaRecentSearchQuery(
            MangaRecentSearchQueryEntity(
                query = searchQuery,
                queriedDate = Clock.System.now(),
            ),
        )
    }

    override suspend fun clearMangaRecentSearches() =
        recentSearchQueryDao.clearMangaRecentSearchQueries()
}