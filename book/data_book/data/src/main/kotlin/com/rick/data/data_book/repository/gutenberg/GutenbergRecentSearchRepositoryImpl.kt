package com.rick.data.data_book.repository.gutenberg

import com.rick.data.data_book.model.asExternalModel
import com.rick.data.database_book.dao.GutenbergRecentSearchQueryDao
import com.rick.data.database_book.model.GutenbergRecentSearchQueryEntity
import com.rick.data.model_book.gutenberg.GutenbergRecentSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject

class GutenbergRecentSearchRepositoryImpl @Inject constructor(
    private val recentSearchQueryDao: GutenbergRecentSearchQueryDao
) : GutenbergRecentSearchRepository {
    override fun getGutenbergRecentSearchQueries(limit: Int): Flow<List<GutenbergRecentSearchQuery>> =
        recentSearchQueryDao.getGutenbergRecentSearchQueryEntities(limit).map { searchQueries ->
            searchQueries.map { it.asExternalModel() }
        }

    override suspend fun insertOrReplaceGutenbergRecentSearch(searchQuery: String) {
        recentSearchQueryDao.insertOrReplaceGutenbergRecentSearchQuery(
            GutenbergRecentSearchQueryEntity(
                query = searchQuery,
                queriedDate = Clock.System.now(),
            ),
        )
    }

    override suspend fun clearGutenbergRecentSearches() =
        recentSearchQueryDao.clearGutenbergRecentSearchQueries()
}