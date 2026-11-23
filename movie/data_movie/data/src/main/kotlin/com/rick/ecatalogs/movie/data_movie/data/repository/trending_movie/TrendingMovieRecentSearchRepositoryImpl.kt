package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieRecentSearchQueryDao
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.model.TmdbRecentSearchQuery
import com.rick.ecatalogs.movie.data_movie.data.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock.System
import javax.inject.Inject

class TrendingMovieRecentSearchRepositoryImpl @Inject constructor(
    private val recentSearchQueryDao: TrendingMovieRecentSearchQueryDao
) : TrendingMovieRecentSearchRepository {
    override fun getTrendingMovieRecentSearchQueries(limit: Int): Flow<List<TmdbRecentSearchQuery>> =
        recentSearchQueryDao.getTrendingMovieRecentSearchQueryEntities(limit).map { searchQueries ->
            searchQueries.map { it.asExternalModel() }
        }

    override suspend fun insertOrReplaceTrendingMovieRecentSearch(searchQuery: String) {
        recentSearchQueryDao.insertOrReplaceTrendingMovieRecentSearchQuery(
            TrendingMovieRecentSearchQueryEntity(
                query = searchQuery,
                queriedDate = System.now(),
            ),
        )
    }

    override suspend fun clearTrendingMovieRecentSearches() =
        recentSearchQueryDao.clearTrendingMovieRecentSearchQueries()
}