package com.rick.ecatalogs.movie.data_movie.data.repository.article

import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleEntity
import com.rick.ecatalogs.movie.data_movie.model.article_models.Article
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [Article] & [ArticleEntity]
 * */
interface ArticleRepository {
    fun getArticles(): Flow<PagingData<ArticleEntity>>
    fun getArticleFavorites(favorites: Set<String>): Flow<List<Article>>
    fun searchArticle(query: String): Flow<List<Article>>

    /**
     * Fetches the first page of articles. If its ids differ from the stored first page,
     * refreshes the list the same way a pull-to-refresh does.
     *
     * @return true if the content changed.
     * @throws java.io.IOException or retrofit2.HttpException on network failure.
     */
    suspend fun syncFirstPage(): Boolean
}
