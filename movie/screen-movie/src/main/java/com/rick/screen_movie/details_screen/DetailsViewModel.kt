package com.rick.screen_movie.details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb.movie_model.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class DetailsViewModel(
    private val repository: MovieCatalogRepository
): ViewModel() {

    private val imdbKey: String

    private val _listImages: MutableLiveData<Image> = MutableLiveData()
    val listImages: LiveData<Image> get() = _listImages

    private val _movingPictures: MutableLiveData<IMDBMovie> = MutableLiveData()
    val movingPictures: LiveData<IMDBMovie> get() = _movingPictures

    init {

        // Load api_keys
        System.loadLibrary("movie-keys")
        imdbKey = getIMDBKey()
    }

    fun getMovieOrSeries(query: String) {
        viewModelScope.launch{
            repository.getMovieOrSeries("", query).collectLatest {
                when (it) {
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                    }
                }
            }
        }
    }

    private external fun getIMDBKey(): String
}