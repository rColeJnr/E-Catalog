package com.rick.movie.screen_movie.article_favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.model_movie.FavoriteUiEvents
import com.rick.data.model_movie.FavoriteUiState
import com.rick.data.model_movie.UserArticle
import com.rick.movie.data_movie.data.repository.article.UserArticleDataRepository
import com.rick.movie.data_movie.data.repository.article.UserArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleFavoriteViewModel @Inject constructor(
    private val userArticleDataRepository: UserArticleDataRepository,
    userArticlesRepository: UserArticlesRepository,
) : ViewModel() {

    val shouldDisplayUndoArticleFavorite = MutableStateFlow(false)
    private var lastRemovedArticleFavorite: String? = null

    val feedArticlesUiState: StateFlow<FavoriteUiState> =
        userArticlesRepository.observeArticleFavorite()
            .map<List<UserArticle>, FavoriteUiState>(FavoriteUiState::ArticlesFavorites)
            .onStart { emit(FavoriteUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = FavoriteUiState.Loading
            )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.RemoveArticleFavorite -> removeArticleFavorite(event.articleId)
            FavoriteUiEvents.UndoArticleFavoriteRemoval -> undoArticleFavoriteRemoval()
            FavoriteUiEvents.ClearUndoState -> clearUndoState()
            else -> {}
        }
    }

    private fun undoArticleFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedArticleFavorite?.let {
                userArticleDataRepository.setNyTimesArticleFavoriteId(it, true)
            }
        }
//        clearUndoState()
    }

    private fun removeArticleFavorite(articleId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayUndoArticleFavorite.value = true
            lastRemovedArticleFavorite = articleId
            userArticleDataRepository.setNyTimesArticleFavoriteId(articleId, false)
        }
    }

    private fun clearUndoState() {
        shouldDisplayUndoArticleFavorite.value = false
        lastRemovedArticleFavorite = null
    }
}

private const val SHOW_ARTICLES = "showArticles"
private const val SHOW_MOVIES = "showMovies"
private const val SHOW_SERIES = "showSeries"
