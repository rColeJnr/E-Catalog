package com.rick.book.screen_book.gutenberg_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.data_book.repository.gutenberg.CompositeGutenbergRepository
import com.rick.data.data_book.repository.gutenberg.GutenbergRecentSearchRepository
import com.rick.data.data_book.repository.gutenberg.UserGutenbergDataRepository
import com.rick.data.domain_book.GetGutenbergRecentSearchQueriesUseCase
import com.rick.data.model_book.gutenberg.GutenbergRecentSearchQuery
import com.rick.data.model_book.gutenberg.UserGutenberg
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

@HiltViewModel
class GutenbergSearchViewModel @Inject constructor(
    getGutenbergRecentSearchQueriesUseCase: GetGutenbergRecentSearchQueriesUseCase,
    private val gutenbergRecentSearchRepository: GutenbergRecentSearchRepository,
    private val compositeBookRepository: CompositeGutenbergRepository,
    private val userDataRepository: UserGutenbergDataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchState: StateFlow<GutenbergSearchUiState>
    val recentSearchState: StateFlow<GutenbergRecentSearchQueriesUiState>

    init {

        searchState = searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(GutenbergSearchUiState.EmptyQuery)
            } else {
                compositeBookRepository.searchGutenbergs(query)
                    // Not using .asResult() here, because it emits Loading state every
                    // time the user types a letter in the search box, which flickers the screen.
                    .map<List<UserGutenberg>, GutenbergSearchUiState> { gutenbergs ->
                        GutenbergSearchUiState.Success(
                            gutenbergs = gutenbergs
                        )
                    }
                    .catch { emit(GutenbergSearchUiState.Error) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GutenbergSearchUiState.EmptyQuery,
        )

        recentSearchState = getGutenbergRecentSearchQueriesUseCase()
            .map(GutenbergRecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GutenbergRecentSearchQueriesUiState.Loading,
            )
    }

    fun onEvent(event: GutenbergSearchUiEvent) {
        when (event) {
            is GutenbergSearchUiEvent.SearchQuery -> searchQuery(event.query)
            is GutenbergSearchUiEvent.UpdateFavorite -> updateFavorite(
                event.id,
                event.isFavorite
            )

            is GutenbergSearchUiEvent.OnSearchTriggered -> onSearchTriggered(event.query)
            GutenbergSearchUiEvent.ClearRecentSearches -> clearRecentSearches()
        }
    }

    private fun searchQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun updateFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            userDataRepository.setGutenbergFavoriteId(id, isFavorite)
        }
    }

    private fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            gutenbergRecentSearchRepository.insertOrReplaceGutenbergRecentSearch(query)
        }
    }

    private fun clearRecentSearches() {
        viewModelScope.launch {
            gutenbergRecentSearchRepository.clearGutenbergRecentSearches()
        }
    }

}

sealed interface GutenbergSearchUiState {
    data object EmptyQuery : GutenbergSearchUiState
    data class Success(val gutenbergs: List<UserGutenberg>) : GutenbergSearchUiState
    data object Loading : GutenbergSearchUiState
    data object Error : GutenbergSearchUiState
}

sealed interface GutenbergSearchUiEvent {
    data class SearchQuery(val query: String) : GutenbergSearchUiEvent

    data class UpdateFavorite(val id: Int, val isFavorite: Boolean) : GutenbergSearchUiEvent

    data class OnSearchTriggered(val query: String) : GutenbergSearchUiEvent

    data object ClearRecentSearches : GutenbergSearchUiEvent
}

sealed interface GutenbergRecentSearchQueriesUiState {
    data object Loading : GutenbergRecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<GutenbergRecentSearchQuery> = emptyList(),
    ) : GutenbergRecentSearchQueriesUiState
}


private const val SEARCH_QUERY = "gutenbergSearchQuery"
private const val TAG = "gutenbergSearchViewModel"
private const val SEARCH_QUERY_MIN_LENGTH = 2