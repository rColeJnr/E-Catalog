package com.rick.data.movie_favorite.repository.trending_movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.database_movie.dao.TrendingMovieDao
import com.rick.data.database_movie.dao.TrendingMovieRemoteKeysDao
import com.rick.data.database_movie.model.TrendingMovieEntity
import com.rick.data.database_movie.model.asTrendingMovie
import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie
import com.rick.data.network_movie.TmdbNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


internal class TrendingMovieRepositoryImpl @Inject constructor(
    private val trendingMovieDao: TrendingMovieDao,
    private val keysDao: TrendingMovieRemoteKeysDao,
    private val network: TmdbNetworkDataSource,
) : TrendingMovieRepository {
    override fun getTrendingMovies(apiKey: String): Flow<PagingData<TrendingMovieEntity>> {
        val pagingSourceFactory = { trendingMovieDao.getTrendingMovie() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = TrendingMovieRemoteMediator(
                network = network,
                trendingMovieDao = trendingMovieDao,
                keysDao = keysDao,
                key = apiKey
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getTrendingMovieFavorites(favorites: Set<Int>): Flow<List<TrendingMovie>> =
        trendingMovieDao.getTrendingMovieWithFilters(favorites)
            .map { trendingMovieEntities -> trendingMovieEntities.map { it.asTrendingMovie() } }
}