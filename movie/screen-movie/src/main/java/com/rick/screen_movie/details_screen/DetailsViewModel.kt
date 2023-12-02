package com.rick.screen_movie.details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.tmdb.movie.MovieResponse
import com.rick.data_movie.tmdb.tv.TvResponse
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


//FLOW

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    private val tmdbKey: String

    private val _searchLoading: MutableLiveData<DetailsUiState.Loading> by
    lazy { MutableLiveData<DetailsUiState.Loading>(DetailsUiState.Loading(false)) }
    val searchLoading: LiveData<DetailsUiState.Loading> get() = _searchLoading

    private val _searchError: MutableLiveData<DetailsUiState.Error> by
    lazy { MutableLiveData<DetailsUiState.Error>() }
    val searchError: LiveData<DetailsUiState.Error> get() = _searchError

    private val _movie: MutableLiveData<DetailsUiState.Movie> by
    lazy { MutableLiveData<DetailsUiState.Movie>() }
    val movie: LiveData<DetailsUiState.Movie> get() = _movie

    private val _series: MutableLiveData<DetailsUiState.Tv> by
    lazy { MutableLiveData<DetailsUiState.Tv>() }
    val series: LiveData<DetailsUiState.Tv> get() = _series

    init {

        // Load api_keys
        System.loadLibrary(LIB_NAME)
        tmdbKey = getTMDBKey()

    }

    fun onTriggerEvent(event: DetailsUiEvent) {
        when (event) {
            is DetailsUiEvent.GetTrendingMovie -> {
                getMovie(event.id)
            }
            is DetailsUiEvent.GetTrendingTv -> {
                getSeries(event.id)
            }
        }
    }

//    fun getMovieOrSeriesId(title: String) {
//        viewModelScope.launch {
//            repository.searchExactMatch(apiKey = imdbKey, query = title).collectLatest { result ->
//                when (result) {
//                    is Resource.Error -> {
//                        _searchError.postValue(result.message)
//                    }
//                    is Resource.Loading -> {
//                        _searchLoading.postValue(result.isLoading)
//                    }
//                    is Resource.Success -> {
//                        getMovieOrSeries(result.data!!.first().id)
//                    }
//                    else -> {}
//                }
//            }
//        }
//    }

    private fun getMovie(id: Int) {
        viewModelScope.launch {
            repository.getTmdbMovie(key = tmdbKey, id = id).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(DetailsUiState.Error(result.message))
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(DetailsUiState.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        _movie.postValue(DetailsUiState.Movie(result.data!!))
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getSeries(id: Int) {
        viewModelScope.launch {
            repository.getTmdbTv(key = tmdbKey, id = id).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(DetailsUiState.Error(result.message))
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(DetailsUiState.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        _series.postValue(DetailsUiState.Tv(result.data!!))
                    }
                    else -> {}
                }
            }
        }
    }

    //    private external fun getIMDBKey(): String
    private external fun getTMDBKey(): String
}

sealed class DetailsUiState {
    data class Tv(val tv: TvResponse) : DetailsUiState()
    data class Movie(val movie: MovieResponse) : DetailsUiState()
    data class Loading(val loading: Boolean): DetailsUiState()
    data class Error(val msg: String?): DetailsUiState()
}

sealed class DetailsUiEvent {
    data class GetTrendingMovie(val id: Int): DetailsUiEvent()
    data class GetTrendingTv(val id: Int): DetailsUiEvent()
}