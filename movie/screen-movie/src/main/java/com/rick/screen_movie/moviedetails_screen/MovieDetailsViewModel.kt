package com.rick.screen_movie.moviedetails_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.tmdb.movie.MovieResponse
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates


//FLOW

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    private val tmdbKey: String
    private var id by Delegates.notNull<Int>()

    private val _searchLoading: MutableLiveData<MovieDetailsUiState.Loading> by
    lazy { MutableLiveData<MovieDetailsUiState.Loading>(MovieDetailsUiState.Loading(false)) }
    val searchLoading: LiveData<MovieDetailsUiState.Loading> get() = _searchLoading

    private val _searchError: MutableLiveData<MovieDetailsUiState.Error> by
    lazy { MutableLiveData<MovieDetailsUiState.Error>() }
    val searchError: LiveData<MovieDetailsUiState.Error> get() = _searchError

    private val _movie: MutableLiveData<MovieDetailsUiState.Movie> by
    lazy { MutableLiveData<MovieDetailsUiState.Movie>() }
    val movie: LiveData<MovieDetailsUiState.Movie> get() = _movie

    init {

        // Load api_keys
        System.loadLibrary(LIB_NAME)
        tmdbKey = getTMDBKey()

    }

    fun onEvent(event: MovieDetailsEvents) {
        when (event) {
            is MovieDetailsEvents.GetMovie -> getMovie(event.id)
        }
    }

    private fun getMovie(id: Int) {
        viewModelScope.launch {
            repository.getTmdbMovie(key = tmdbKey, id = id).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(MovieDetailsUiState.Error(result.message))
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(MovieDetailsUiState.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        _movie.postValue(MovieDetailsUiState.Movie(result.data!!))
                    }
                    else -> {}
                }
            }
        }
    }
}

private external fun getTMDBKey(): String

sealed class MovieDetailsUiState {
    data class Movie(val movie: MovieResponse) : MovieDetailsUiState()
    data class Loading(val loading: Boolean): MovieDetailsUiState()
    data class Error(val msg: String?): MovieDetailsUiState()
}

sealed class MovieDetailsEvents {
    data class GetMovie(val id: Int): MovieDetailsEvents()
}

