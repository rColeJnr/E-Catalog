package com.rick.ecatalogs.movie.data_movie.data.repository.article

import com.rick.ecatalogs.movie.data_movie.data.repository.catalogPosition
import com.rick.ecatalogs.movie.data_movie.data.model.asArticleEntity
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleDao
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleRemoteKeys
import com.rick.ecatalogs.movie.data_movie.network.model.ArticleNetwork
import com.rick.ecatalogs.movie.data_movie.network.model.ArticleResponse

internal const val ARTICLE_FIRST_PAGE = 0
private const val MAX_PAGE = 100

/**
 * Stores one fetched page of articles. Shared by [ArticleRemoteMediator] and the background sync
 * so both refresh the list the same way.
 *
 * On [isRefresh] all remote keys are cleared before inserting.
 *
 * @return true when the end of pagination has been reached.
 */
internal suspend fun writeArticlePage(
    page: Int,
    response: ArticleResponse,
    isRefresh: Boolean,
    articleDao: ArticleDao,
    keysDao: ArticleRemoteKeysDao,
    transactionRunner: MovieTransactionRunner,
    keepIds: Set<String>,
): Boolean {
    val articles = response.response.docs
    val endOfPaginationReached = articles.isEmpty() || page >= MAX_PAGE

    transactionRunner {
        if (isRefresh) {
            // Written anew: rows leave the list, and those that aren't favorites are deleted
            articleDao.clearCatalog()
            articleDao.deleteOutsideCatalog(keepIds)
            keysDao.clearRemoteKeys()
        }

        val offset = response.response.meta.offset

        val prevKey = if (page == ARTICLE_FIRST_PAGE) null else (offset / 10) - 1
        val nextKey = if (endOfPaginationReached) null else {
            when (offset) {
                0 -> 1
                else -> (offset / 10) + 1
            }
        }
        val keys = articles.map {
            ArticleRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
        }
        keysDao.insertAll(keys)
        articleDao.upsertArticles(
            articles.mapIndexed { index, article ->
                article.asArticleEntity().copy(catalogPosition = catalogPosition(page, index))
            },
        )
    }
    return endOfPaginationReached
}
