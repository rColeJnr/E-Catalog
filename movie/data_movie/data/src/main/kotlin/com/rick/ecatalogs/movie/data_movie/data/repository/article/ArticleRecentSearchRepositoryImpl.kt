package com.rick.ecatalogs.movie.data_movie.data.repository.article

import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleRecentSearchQueryDao
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.model.ArticleRecentSearchQuery
import com.rick.ecatalogs.movie.data_movie.data.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject

class ArticleRecentSearchRepositoryImpl @Inject constructor(
    private val recentSearchQueryDao: ArticleRecentSearchQueryDao
) : ArticleRecentSearchRepository {
    override fun getArticleRecentSearchQueries(limit: Int): Flow<List<ArticleRecentSearchQuery>> =
        recentSearchQueryDao.getArticleRecentSearchQueryEntities(limit).map { searchQueries ->
            searchQueries.map { it.asExternalModel() }
        }

    override suspend fun insertOrReplaceArticleRecentSearch(searchQuery: String) {
        recentSearchQueryDao.insertOrReplaceArticleRecentSearchQuery(
            ArticleRecentSearchQueryEntity(
                query = searchQuery,
                queriedDate = Clock.System.now(),
            ),
        )
    }

    override suspend fun clearArticleRecentSearches() =
        recentSearchQueryDao.clearArticleRecentSearchQueries()
}