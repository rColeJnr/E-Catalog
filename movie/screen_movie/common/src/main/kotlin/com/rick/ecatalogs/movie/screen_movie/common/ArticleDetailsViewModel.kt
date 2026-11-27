package com.rick.ecatalogs.movie.screen_movie.common

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.movie.data_movie.data.repository.article.UserArticleDataRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.article.UserArticlesRepository
import com.rick.ecatalogs.movie.data_movie.model.UserArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A movie review's details screen: the stored article (from the catalog, a search or the
 * favorites; deep link `movie_common_article_details/{articleId}`) and its favorite state.
 */
@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    articlesRepository: UserArticlesRepository,
    private val userDataRepository: UserArticleDataRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val articleId: String = checkNotNull(savedStateHandle[ARTICLE_ID_ARG])

    val state: StateFlow<ArticleDetailsUiState> = articlesRepository.observeArticleById(articleId)
        .map { article ->
            if (article == null) ArticleDetailsUiState.Unavailable else ArticleDetailsUiState.Success(article)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ArticleDetailsUiState.Loading)

    fun toggleFavorite() {
        val article = (state.value as? ArticleDetailsUiState.Success)?.article ?: return
        viewModelScope.launch {
            userDataRepository.setNyTimesArticleFavoriteId(article.id, !article.isFavorite)
        }
    }

    companion object {
        const val ARTICLE_ID_ARG = "articleId"
    }
}

sealed interface ArticleDetailsUiState {
    data object Loading : ArticleDetailsUiState

    /** Not stored (any more), e.g. a search result the list refresh removed. */
    data object Unavailable : ArticleDetailsUiState

    data class Success(val article: UserArticle) : ArticleDetailsUiState
}
