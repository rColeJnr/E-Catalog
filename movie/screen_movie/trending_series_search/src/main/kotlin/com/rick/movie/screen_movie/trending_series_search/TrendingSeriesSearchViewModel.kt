package com.rick.movie.screen_movie.trending_series_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.domain_movie.GetTrendingSeriesRecentSearchQueriesUseCase
import com.rick.data.model_movie.TmdbRecentSearchQuery
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.movie.data_movie.data.repository.trending_series.CompositeTrendingSeriesRepository
import com.rick.movie.data_movie.data.repository.trending_series.TrendingSeriesRecentSearchRepository
import com.rick.movie.data_movie.data.repository.trending_series.UserTrendingSeriesDataRepository
import com.rick.movie.screen_movie.common.util.SERIES_DETAILS_LIB_NAME
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
class TrendingSeriesSearchViewModel @Inject constructor(
    getTrendingSeriesRecentSearchQueriesUseCase: GetTrendingSeriesRecentSearchQueriesUseCase,
    private val trendingSeriesRecentSearchRepository: TrendingSeriesRecentSearchRepository,
    private val compositeTrendingSeriesRepository: CompositeTrendingSeriesRepository,
    private val userTrendingSeriesDataRepository: UserTrendingSeriesDataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val nyKey: String

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchState: StateFlow<TrendingSeriesSearchUiState>
    val recentSearchState: StateFlow<TrendingSeriesRecentSearchQueriesUiState>

    init {

        // Load api_keys
        System.loadLibrary(SERIES_DETAILS_LIB_NAME)
//        imdbKey = getIMDBKey()
        nyKey = getTmdbKey()

        searchState = searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(TrendingSeriesSearchUiState.EmptyQuery)
            } else {
                compositeTrendingSeriesRepository.observeSearchTrendingSeries(
                    query = query,
                    apiKey = nyKey
                )
                    // Not using .asResult() here, because it emits Loading state every
                    // time the user types a letter in the search box, which flickers the screen.

                    .map<List<UserTrendingSeries>, TrendingSeriesSearchUiState> { series ->
                        TrendingSeriesSearchUiState.Success(
                            series = series
                        )
                    }
                    .catch {
                        emit(TrendingSeriesSearchUiState.Error)
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrendingSeriesSearchUiState.EmptyQuery,
        )

        recentSearchState = getTrendingSeriesRecentSearchQueriesUseCase()
            .map(TrendingSeriesRecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TrendingSeriesRecentSearchQueriesUiState.Loading,
            )
    }

    fun onEvent(event: TrendingSeriesSearchUiEvent) {
        when (event) {
            is TrendingSeriesSearchUiEvent.SearchQuery -> searchQuery(event.query)
            is TrendingSeriesSearchUiEvent.UpdateFavorite -> updateFavorite(
                event.id,
                event.isFavorite
            )

            is TrendingSeriesSearchUiEvent.OnSearchTriggered -> onSearchTriggered(event.query)
            TrendingSeriesSearchUiEvent.ClearRecentSearches -> clearRecentSearches()

        }
    }

    private fun searchQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun updateFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            userTrendingSeriesDataRepository.setTrendingSeriesFavoriteId(id, isFavorite)
        }
    }

    private fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            trendingSeriesRecentSearchRepository.insertOrReplaceTrendingSeriesRecentSearch(
                searchQuery = query
            )
        }
    }

    private fun clearRecentSearches() {
        viewModelScope.launch {
            trendingSeriesRecentSearchRepository.clearTrendingSeriesRecentSearches()
        }
    }

}

private external fun getTmdbKey(): String

sealed interface TrendingSeriesSearchUiState {
    data object EmptyQuery : TrendingSeriesSearchUiState
    data class Success(val series: List<UserTrendingSeries>) : TrendingSeriesSearchUiState
    data object Loading : TrendingSeriesSearchUiState
    data object Error : TrendingSeriesSearchUiState
}

sealed interface TrendingSeriesSearchUiEvent {
    data class SearchQuery(val query: String) : TrendingSeriesSearchUiEvent

    data class UpdateFavorite(val id: Int, val isFavorite: Boolean) : TrendingSeriesSearchUiEvent

    data class OnSearchTriggered(val query: String) : TrendingSeriesSearchUiEvent

    data object ClearRecentSearches : TrendingSeriesSearchUiEvent
}

sealed interface TrendingSeriesRecentSearchQueriesUiState {
    data object Loading : TrendingSeriesRecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<TmdbRecentSearchQuery> = emptyList(),
    ) : TrendingSeriesRecentSearchQueriesUiState
}


private const val TAG = "trendingSeriesSearchViewModel"
private const val SEARCH_QUERY = "trendingSeriesSearchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 2