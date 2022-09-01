package com.rick.screen_movie.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
): ViewModel() {

    private val imdbKey: String
    var searchList: List<IMDBSearchResult> = listOf()
    val searchState: StateFlow<SearchUiState>
    val searchAction: (SearchUiAction) -> Unit

    init {

        // Load api_keys
        System.loadLibrary("movie-keys")
        imdbKey = getIMDBKey()

        val actionStateFlow = MutableSharedFlow<SearchUiAction>()
        val searchMovie = actionStateFlow
            .filterIsInstance<SearchUiAction.SearchMovie>()
            .distinctUntilChanged()
        val searchSeries = actionStateFlow
            .filterIsInstance<SearchUiAction.SearchSeries>()
            .distinctUntilChanged()

        searchMovies("anal")

        searchState = combine(
            searchMovie,
            searchSeries,
            ::Pair
        ).map { (movie, series) ->
            SearchUiState(
                movieQuery = movie.title,
                seriesQuery = series.title
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 1000),
                initialValue = SearchUiState()
            )

        searchAction = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    fun searchMovies(title: String) {
        viewModelScope.launch {
            repository.searchMovies(apiKey = imdbKey, title = title)
                .collect { result ->
                    when (result) {
                        is Resource.Error -> {}
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            searchList = result.data!!
                        }
                    }
                }
        }
    }

    fun searchSeries(title: String) {
        viewModelScope.launch {
            repository.searchSeries(apiKey = imdbKey, title = title)
                .collect { result ->
                    when (result) {
                        is Resource.Error -> TODO()
                        is Resource.Loading -> TODO()
                        is Resource.Success -> TODO()
                    }
                }
        }
    }

    private external fun getIMDBKey(): String
}


data class SearchUiState (
    val movieQuery : String? = null,
    val seriesQuery : String? = null,
)

sealed class SearchUiAction {
    data class SearchMovie(val title: String) : SearchUiAction()
    data class SearchSeries(val title: String) : SearchUiAction()
    data class NavigateToDetails(val movie: IMDBSearchResult): SearchUiAction()
}