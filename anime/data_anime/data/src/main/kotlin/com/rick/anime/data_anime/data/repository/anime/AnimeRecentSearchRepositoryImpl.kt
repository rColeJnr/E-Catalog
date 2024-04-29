package com.rick.anime.data_anime.data.repository.anime

import com.rick.anime.data_anime.data.model.asExternalModel
import com.rick.data.database_anime.dao.AnimeRecentSearchQueryDao
import com.rick.data.database_anime.model.AnimeRecentSearchQueryEntity
import com.rick.data.model_anime.AnimeRecentSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject

class AnimeRecentSearchRepositoryImpl @Inject constructor(
    private val recentSearchQueryDao: AnimeRecentSearchQueryDao
) : AnimeRecentSearchRepository {
    override fun getAnimeRecentSearchQueries(limit: Int): Flow<List<AnimeRecentSearchQuery>> =
        recentSearchQueryDao.getAnimeRecentSearchQueryEntities(limit).map { searchQueries ->
            searchQueries.map { it.asExternalModel() }
        }

    override suspend fun insertOrReplaceAnimeRecentSearch(searchQuery: String) {
        recentSearchQueryDao.insertOrReplaceAnimeRecentSearchQuery(
            AnimeRecentSearchQueryEntity(
                query = searchQuery,
                queriedDate = Clock.System.now(),
            ),
        )
    }

    override suspend fun clearAnimeRecentSearches() =
        recentSearchQueryDao.clearAnimeRecentSearchQueries()
}