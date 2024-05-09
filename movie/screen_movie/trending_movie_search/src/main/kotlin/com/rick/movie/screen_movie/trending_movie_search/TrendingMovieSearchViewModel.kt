package com.rick.movie.screen_movie.trending_movie_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.domain_movie.GetTrendingMovieRecentSearchQueriesUseCase
import com.rick.data.model_movie.TmdbRecentSearchQuery
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.movie.data_movie.data.repository.trending_movie.CompositeTrendingMovieRepository
import com.rick.movie.data_movie.data.repository.trending_movie.TrendingMovieRecentSearchRepository
import com.rick.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.movie.screen_movie.common.util.MOVIE_DETAILS_LIB_NAME
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
@HiltViewModel
class TrendingMovieSearchViewModel @Inject constructor(
    getTrendingMovieRecentSearchQueriesUseCase: GetTrendingMovieRecentSearchQueriesUseCase,
    private val trendingMovieRecentSearchRepository: TrendingMovieRecentSearchRepository,
    private val compositeTrendingMovieRepository: CompositeTrendingMovieRepository,
    private val userTrendingMovieDataRepository: UserTrendingMovieDataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tmdbKey: String

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchState: StateFlow<TrendingMovieSearchUiState>
    val recentSearchState: StateFlow<TrendingMovieRecentSearchQueriesUiState>

    init {

        // Load api_keys
        System.loadLibrary(MOVIE_DETAILS_LIB_NAME)
//        imdbKey = getIMDBKey()
        tmdbKey = getTmdbKey()

        searchState = searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(TrendingMovieSearchUiState.EmptyQuery)
            } else {
                compositeTrendingMovieRepository.observeSearchTrendingMovie(query, apiKey = tmdbKey)
                    // Not using .asResult() here, because it emits Loading state every
                    // time the user types a letter in the search box, which flickers the screen.
                    .map<List<UserTrendingMovie>, TrendingMovieSearchUiState> { data ->
                        TrendingMovieSearchUiState.Success(
                            movies = data
                        )
                    }
                    .catch {
                        emit(TrendingMovieSearchUiState.Error(it.localizedMessage))
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrendingMovieSearchUiState.EmptyQuery,
        )

        recentSearchState = getTrendingMovieRecentSearchQueriesUseCase()
            .map(TrendingMovieRecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TrendingMovieRecentSearchQueriesUiState.Loading,
            )

    }


    fun onEvent(event: TrendingMovieSearchUiEvent) {
        when (event) {
            is TrendingMovieSearchUiEvent.SearchQuery -> searchQuery(event.query)
            is TrendingMovieSearchUiEvent.UpdateFavorite -> updateFavorite(
                event.id,
                event.isFavorite
            )

            is TrendingMovieSearchUiEvent.OnSearchTriggered -> onSearchTriggered(event.query)
            TrendingMovieSearchUiEvent.ClearRecentSearches -> clearRecentSearches()
        }
    }

    private fun searchQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun updateFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            userTrendingMovieDataRepository.setTrendingMovieFavoriteId(id, isFavorite)
        }
    }

    private fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            trendingMovieRecentSearchRepository.insertOrReplaceTrendingMovieRecentSearch(searchQuery = query)
        }
    }

    private fun clearRecentSearches() {
        viewModelScope.launch {
            trendingMovieRecentSearchRepository.clearTrendingMovieRecentSearches()
        }
    }

}

private external fun getTmdbKey(): String

sealed interface TrendingMovieSearchUiState {
    data object EmptyQuery : TrendingMovieSearchUiState
    data class Success(val movies: List<UserTrendingMovie>) : TrendingMovieSearchUiState
    data object Loading : TrendingMovieSearchUiState
    data class Error(val msg: String) : TrendingMovieSearchUiState
}

sealed interface TrendingMovieSearchUiEvent {
    data class SearchQuery(val query: String) : TrendingMovieSearchUiEvent
    data class UpdateFavorite(val id: Int, val isFavorite: Boolean) :
        TrendingMovieSearchUiEvent

    data class OnSearchTriggered(val query: String) : TrendingMovieSearchUiEvent

    data object ClearRecentSearches : TrendingMovieSearchUiEvent

}

sealed interface TrendingMovieRecentSearchQueriesUiState {
    data object Loading : TrendingMovieRecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<TmdbRecentSearchQuery> = emptyList(),
    ) : TrendingMovieRecentSearchQueriesUiState
}

private const val TAG = "TrendingMovieSearchViewModel"
private const val SEARCH_QUERY = "trendingMovieSearchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 2