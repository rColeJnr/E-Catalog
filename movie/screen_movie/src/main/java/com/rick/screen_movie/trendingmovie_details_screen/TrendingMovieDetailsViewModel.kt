package com.rick.screen_movie.trendingmovie_details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates


//FLOW

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(

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
            //TODO()
        }
    }
}

private external fun getTMDBKey(): String

sealed class MovieDetailsUiState {
    data class Movie(val movie: com.rick.data.model_movie.tmdb.movie.MovieResponse) :
        MovieDetailsUiState()

    data class Loading(val loading: Boolean) : MovieDetailsUiState()
    data class Error(val msg: String?) : MovieDetailsUiState()
}

sealed class MovieDetailsEvents {
    data class GetMovie(val id: Int) : MovieDetailsEvents()
}

