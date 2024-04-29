package com.rick.movie.data_movie.data.repository.article

import android.util.Log
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
import com.rick.data.network_movie.model.ArticleNetwork
import com.rick.movie.data_movie.data.model.asArticleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
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
        articleDao.getArticlesWithFilters(
            filterById = true,
            filterArticleIds = favorites
        )
            .map { articleEntities -> articleEntities.map { it.asArticle() } }

    override fun searchArticle(query: String, apiKey: String): Flow<List<Article>> = channelFlow {
        try {
            Log.e(TAG, "DUDES GET'S HERE")
            val response = network.searchMovieArticles(
                query = query,
                apiKey = apiKey
            ).response.docs
            articleDao.upsertArticles(response.map(ArticleNetwork::asArticleEntity))

            // appending '%' so we can allow other characters to be before and after the query string
            val dbQuery = "%${query.replace(' ', '%')}%"
            articleDao.getArticlesWithFilters(
                filterByQuery = true,
                query = dbQuery
            )
                .collectLatest { articleEntities -> send(articleEntities.map(ArticleEntity::asArticle)) }

        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage ?: "IOException")
        } catch (e: HttpException) {
            Log.e(TAG, e.localizedMessage ?: "HTTPException")
        }
    }
}

private const val TAG = "ArticleRepositoryImpl"