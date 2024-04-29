package com.rick.movie.data_movie.data.repository.trending_series

import com.rick.data.database_movie.dao.TrendingSeriesRecentSearchQueryDao
import com.rick.data.database_movie.model.TrendingSeriesRecentSearchQueryEntity
import com.rick.data.model_movie.TmdbRecentSearchQuery
import com.rick.movie.data_movie.data.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject

class TrendingSeriesRecentSearchRepositoryImpl @Inject constructor(
    private val recentSearchQueryDao: TrendingSeriesRecentSearchQueryDao
) : TrendingSeriesRecentSearchRepository {
    override fun getTrendingSeriesRecentSearchQueries(limit: Int): Flow<List<TmdbRecentSearchQuery>> =
        recentSearchQueryDao.getTrendingSeriesRecentSearchQueryEntities(limit)
            .map { searchQueries ->
                searchQueries.map { it.asExternalModel() }
            }

    override suspend fun insertOrReplaceTrendingSeriesRecentSearch(searchQuery: String) {
        recentSearchQueryDao.insertOrReplaceTrendingSeriesRecentSearchQuery(
            TrendingSeriesRecentSearchQueryEntity(
                query = searchQuery,
                queriedDate = Clock.System.now(),
            ),
        )
    }

    override suspend fun clearTrendingSeriesRecentSearches() =
        recentSearchQueryDao.clearTrendingSeriesRecentSearchQueries()
}