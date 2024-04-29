package com.rick.movie.screen_movie.article_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.domain_movie.GetArticleRecentSearchQueriesUseCase
import com.rick.data.model_movie.ArticleRecentSearchQuery
import com.rick.data.model_movie.UserArticle
import com.rick.movie.data_movie.data.repository.article.ArticleRecentSearchRepository
import com.rick.movie.data_movie.data.repository.article.CompositeArticleRepository
import com.rick.movie.data_movie.data.repository.article.UserArticleDataRepository
import com.rick.movie.screen_movie.common.util.ARTICLE_LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// WHY ISNT MY CODEBASE CONSISTENT THROUGHOUT, AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHH
// I'm in public so i can't just blast.
// TODO
@HiltViewModel
class ArticleSearchViewModel @Inject constructor(
    getArticleRecentSearchQueriesUseCase: GetArticleRecentSearchQueriesUseCase,
    private val articleRecentSearchRepository: ArticleRecentSearchRepository,
    private val compositeArticleRepository: CompositeArticleRepository,
    private val userArticleDataRepository: UserArticleDataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val nyKey: String

    private val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchState: StateFlow<ArticleSearchUiState>
    val recentSearchState: StateFlow<ArticleRecentSearchQueriesUiState>

    init {

        // Load api_keys
        System.loadLibrary(ARTICLE_LIB_NAME)
//        imdbKey = getIMDBKey()
        nyKey = getNyKey()

        searchState = searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(ArticleSearchUiState.EmptyQuery)
            } else {
                compositeArticleRepository.observeSearchArticle(query, apiKey = nyKey)
                    // Not using .asResult() here, because it emits Loading state every
                    // time the user types a letter in the search box, which flickers the screen.
                    .map<List<UserArticle>, ArticleSearchUiState> { articles ->
                        ArticleSearchUiState.Success(
                            articles = articles
                        )
                    }
                    .catch { emit(ArticleSearchUiState.Error(it.localizedMessage)) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArticleSearchUiState.EmptyQuery,
        )

        recentSearchState = getArticleRecentSearchQueriesUseCase()
            .map(ArticleRecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ArticleRecentSearchQueriesUiState.Loading,
            )
    }

    fun onEvent(event: ArticleSearchUiEvent) {
        when (event) {
            is ArticleSearchUiEvent.SearchQuery -> searchQuery(event.query)
            is ArticleSearchUiEvent.UpdateFavorite -> updateFavorite(
                event.id,
                event.isFavorite
            )

            is ArticleSearchUiEvent.OnSearchTriggered -> onSearchTriggered(event.query)
            ArticleSearchUiEvent.ClearRecentSearches -> clearRecentSearches()

        }
    }

    private fun searchQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun updateFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            userArticleDataRepository.setNyTimesArticleFavoriteId(id, isFavorite)
        }
    }

    private fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            articleRecentSearchRepository.insertOrReplaceArticleRecentSearch(searchQuery = query)
        }
    }

    private fun clearRecentSearches() {
        viewModelScope.launch {
            articleRecentSearchRepository.clearArticleRecentSearches()
        }
    }

}

private external fun getNyKey(): String

sealed interface ArticleSearchUiState {
    data object EmptyQuery : ArticleSearchUiState
    data class Success(val articles: List<UserArticle>) : ArticleSearchUiState
    data object Loading : ArticleSearchUiState
    data class Error(val msg: String) : ArticleSearchUiState
}

sealed interface ArticleSearchUiEvent {
    data class SearchQuery(val query: String) : ArticleSearchUiEvent

    data class UpdateFavorite(val id: Long, val isFavorite: Boolean) : ArticleSearchUiEvent

    data class OnSearchTriggered(val query: String) : ArticleSearchUiEvent

    data object ClearRecentSearches : ArticleSearchUiEvent
}

sealed interface ArticleRecentSearchQueriesUiState {
    data object Loading : ArticleRecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<ArticleRecentSearchQuery> = emptyList(),
    ) : ArticleRecentSearchQueriesUiState
}


private const val SEARCH_QUERY = "articleSearchQuery"
private const val TAG = "articleSearchViewModel"
private const val SEARCH_QUERY_MIN_LENGTH = 2