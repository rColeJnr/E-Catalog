package com.rick.screen_movie.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
): ViewModel() {

    private val imdbKey: String

    init {

        // Load api_keys
        System.loadLibrary("movie-keys")
        imdbKey = getIMDBKey()

        val actionStateFlow = MutableSharedFlow<UiAction>()

    }

    fun searchMovies(title: String) {
        viewModelScope.launch {
            repository.searchMovies(apiKey = imdbKey, title = title)
                .collect { result ->
                    when (result) {
                        is Resource.Error -> TODO()
                        is Resource.Loading -> TODO()
                        is Resource.Success -> TODO()
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


sealed class UiState {
    data class searchQuery(val query: String): UiState()
}

sealed class UiAction {
    data class SearchMovie(val title: String) : UiAction()
    data class SearchSeries(val title: String) : UiAction()
    data class NavigateToDetails(val movie: IMDBSearchResult): UiAction()
}