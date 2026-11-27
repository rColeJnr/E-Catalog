package com.rick.ecatalogs.movie.data_movie.data.repository.article

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.ecatalogs.movie.data_movie.database.model.asArticle
import com.rick.ecatalogs.movie.data_movie.model.UserArticle
import com.rick.ecatalogs.movie.data_movie.model.mapToUserArticle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * implements all repositories and provides all favorites
 * */
class CompositeArticleRepository @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val userDataRepository: UserArticleDataRepository
) : UserArticlesRepository {

    override fun observeArticle(
        viewModelScope: CoroutineScope
    ): Flow<PagingData<UserArticle>> =
        articleRepository.getArticles().cachedIn(viewModelScope)
            .combine(userDataRepository.userData) { article, userData ->
                article.map { it.asArticle().mapToUserArticle(userData) }
            }

    override fun observeArticleFavorite(): Flow<List<UserArticle>> =
        userDataRepository.userData.map { it.nyTimesArticlesFavoriteIds }.distinctUntilChanged()
            .flatMapLatest { favoriteIds ->
                when {
                    favoriteIds.isEmpty() -> flowOf(emptyList())
                    else -> articleRepository.getArticleFavorites(favoriteIds)
                        .combine(userDataRepository.userData) { article, userData ->
                            article.mapToUserArticle(userData)
                        }
                }
            }

    // getArticleFavorites reads the stored rows with the given ids, favorite or not
    override fun observeArticleById(id: String): Flow<UserArticle?> =
        articleRepository.getArticleFavorites(setOf(id))
            .combine(userDataRepository.userData) { articles, userData ->
                articles.firstOrNull()?.mapToUserArticle(userData)
            }

    override fun observeSearchArticle(query: String): Flow<List<UserArticle>> =
        articleRepository.searchArticle(query = query)
            .combine(userDataRepository.userData) { article, userData ->
                article.map { it.mapToUserArticle(userData) }
            }
}
