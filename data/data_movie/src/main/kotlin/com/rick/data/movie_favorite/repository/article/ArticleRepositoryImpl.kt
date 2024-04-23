package com.rick.data.movie_favorite.repository.article

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.database_movie.dao.ArticleDao
import com.rick.data.database_movie.dao.ArticleRemoteKeysDao
import com.rick.data.database_movie.model.ArticleEntity
import com.rick.data.database_movie.model.asArticle
import com.rick.data.model_movie.article_models.Article
import com.rick.data.network_movie.ArticleNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ArticleRepositoryImpl @Inject constructor(
    private val articleDao: ArticleDao,
    private val keysDao: ArticleRemoteKeysDao,
    private val network: ArticleNetworkDataSource,
) : ArticleRepository {
    override fun getArticles(apiKey: String): Flow<PagingData<ArticleEntity>> {
        val pagingSourceFactory = { articleDao.getArticles() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                prefetchDistance = 0,
                initialLoadSize = 1
            ),
            remoteMediator = ArticleRemoteMediator(
                network = network,
                articleDao = articleDao,
                keysDao = keysDao,
                key = apiKey
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getArticleFavorites(favorites: Set<Long>): Flow<List<Article>> =
        articleDao.getArticlesWithFilters(favorites)
            .map { articleEntities -> articleEntities.map { it.asArticle() } }
}