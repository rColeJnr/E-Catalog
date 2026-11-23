package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import com.rick.ecatalogs.book.data_book.data.model.asExternalModel
import com.rick.ecatalogs.book.data_book.database.dao.GutenbergRecentSearchQueryDao
import com.rick.ecatalogs.book.data_book.database.model.GutenbergRecentSearchQueryEntity
import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergRecentSearchQuery
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