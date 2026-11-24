package com.rick.ecatalogs.movie.data_movie.data.repository.article

import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.data.datastore.CachedList
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.movie.data_movie.data.model.asArticleEntity
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleDao
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleEntity
import com.rick.ecatalogs.movie.data_movie.database.model.asArticle
import com.rick.ecatalogs.movie.data_movie.model.article_models.Article
import com.rick.ecatalogs.movie.data_movie.network.ArticleNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.ArticleNetwork
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
    private val transactionRunner: MovieTransactionRunner,
    private val preferences: PreferencesDataSource,
    private val listCache: ListCache,
) : ArticleRepository {
    override fun getArticles(): Flow<PagingData<ArticleEntity>> {
        val pagingSourceFactory = { articleDao.getArticles() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                prefetchDistance = 10,
                initialLoadSize = 30
            ),
            remoteMediator = ArticleRemoteMediator(
                network = network,
                articleDao = articleDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                listCache = listCache,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getArticleFavorites(favorites: Set<String>): Flow<List<Article>> =
        articleDao.getArticlesWithFilters(
            filterById = true,
            filterArticleIds = favorites
        )
            .map { articleEntities -> articleEntities.map { it.asArticle() } }

    override suspend fun syncFirstPage(): List<Article> {
        val response = network.fetchMovieArticles(page = ARTICLE_FIRST_PAGE)
        val articles = response.response.docs
        val fetchedIds = articles.map(ArticleNetwork::id).toSet()
        if (fetchedIds.isEmpty()) return emptyList()

        if (fetchedIds != keysDao.firstPageIds().toSet()) {
            writeArticlePage(
                page = ARTICLE_FIRST_PAGE,
                response = response,
                isRefresh = true,
                articleDao = articleDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
            )
        }

        // The first page was just checked against the server: the stored list is current
        listCache.markRefreshed(CachedList.ARTICLES)

        val previouslySeen = preferences.notifiedArticleIds()
        preferences.setNotifiedArticleIds(fetchedIds)
        if (previouslySeen.isEmpty()) return emptyList()
        return articles
            .filter { it.id !in previouslySeen }
            .map { it.asArticleEntity().asArticle() }
    }

    override fun searchArticle(query: String): Flow<List<Article>> = channelFlow {
        try {
            val response = network.searchMovieArticles(
                query = query,
            ).response.docs

            articleDao.clearArticles(response.map(ArticleNetwork::id))

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