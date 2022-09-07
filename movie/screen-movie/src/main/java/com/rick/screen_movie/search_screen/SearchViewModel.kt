package com.rick.screen_movie.search_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
) : ViewModel() {

    private val imdbKey: String

    private val _movieOrSeries: MutableLiveData<IMDBSearchResult> by
        lazy { MutableLiveData<IMDBSearchResult>() }
    val movieOrSeries: LiveData<IMDBSearchResult> get() = _movieOrSeries

    private val _searchList: MutableLiveData<List<IMDBSearchResult>> by
        lazy { MutableLiveData<List<IMDBSearchResult>>() }
    val searchList: LiveData<List<IMDBSearchResult>> get() = _searchList

    private val _searchLoading: MutableLiveData<Boolean> by
        lazy { MutableLiveData<Boolean>(false) }
    val searchLoading: LiveData<Boolean> get() = _searchLoading

    private val _searchError: MutableLiveData<String> by
        lazy { MutableLiveData<String>() }
    val searchError: LiveData<String> get() = _searchError

    val searchState: StateFlow<SearchUiState>
    val searchAction: (SearchUiAction) -> Unit

    init {

        // Load api_keys
        System.loadLibrary("movie-keys")
        imdbKey = getIMDBKey()

        val actionStateFlow = MutableSharedFlow<SearchUiAction>()
        val searchMovie =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchMovie>().distinctUntilChanged()
        val searchExactMovieOrSeries =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchExactMovieOrSeries>().distinctUntilChanged()
        val searchSeries =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchSeries>().distinctUntilChanged()

        viewModelScope.launch{ searchMovie.collectLatest { searchMovies(it.title) } }
        viewModelScope.launch{ searchExactMovieOrSeries.collectLatest { getMovieOrSeries(it.title) } }

        searchState = combine(
            searchMovie, searchSeries, ::Pair
        ).map { (movie, series) ->
            SearchUiState(
                movieQuery = movie.title, seriesQuery = series.title
            )
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 1000),
                initialValue = SearchUiState()
            )

        searchAction = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchMovies(title: String) {
        viewModelScope.launch {
            repository.searchMovies(apiKey = imdbKey, query = title).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(result.message)
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _searchList.postValue(result.data!!)
                    }
                }
            }
        }
    }

    private fun getMovieOrSeries(title: String) {
        viewModelScope.launch {
            repository.searchExactMatch(apiKey = imdbKey, query = title).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(result.message)
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _movieOrSeries.postValue(result.data!!.first())
                    }
                }
            }
        }
    }

    private fun searchSeries(title: String) {
        viewModelScope.launch {
            repository.searchSeries(apiKey = imdbKey, query = title).collect { result ->
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


data class SearchUiState(
    val movieQuery: String? = null,
    val seriesQuery: String? = null,
)

sealed class SearchUiAction {
    data class SearchMovie(val title: String) : SearchUiAction()
    data class SearchExactMovieOrSeries(val title: String) : SearchUiAction()
    data class SearchSeries(val title: String) : SearchUiAction()
    data class NavigateToDetails(val movie: IMDBSearchResult) : SearchUiAction()
}