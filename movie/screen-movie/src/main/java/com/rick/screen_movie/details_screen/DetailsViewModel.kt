package com.rick.screen_movie.details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb_am_not_paying.movie_model.IMDBMovie
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    private val imdbKey: String

    private val _searchLoading: MutableLiveData<Boolean> by
    lazy { MutableLiveData<Boolean>(false) }
    val searchLoading: LiveData<Boolean> get() = _searchLoading

    private val _searchError: MutableLiveData<String> by
    lazy { MutableLiveData<String>() }
    val searchError: LiveData<String> get() = _searchError

    private val _movingPictures: MutableLiveData<IMDBMovie> by
    lazy { MutableLiveData<IMDBMovie>() }
    val movingPictures: LiveData<IMDBMovie> get() = _movingPictures

    init {

        // Load api_keys
        System.loadLibrary(LIB_NAME)
        imdbKey = getIMDBKey()

    }

    fun getMovieOrSeriesId(title: String) {
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
                        getMovieOrSeries(result.data!!.first().id)
                    }
                    else -> {}
                }
            }
        }
    }

    fun getMovieOrSeries(id: String) {
        viewModelScope.launch {
            repository.getMovieOrSeries(imdbKey, id).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(result.message)
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _movingPictures.postValue(result.data!!)
                    }
                    else -> {}
                }
            }
        }
    }

    private external fun getIMDBKey(): String
}