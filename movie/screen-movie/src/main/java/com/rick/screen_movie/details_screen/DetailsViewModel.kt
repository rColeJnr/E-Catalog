package com.rick.screen_movie.details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb.movie_model.IMDBMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
): ViewModel() {

    private val imdbKey: String

    private val _movingPicturesId : MutableLiveData<String> = MutableLiveData()
    val movingPicturesId : LiveData<String> get() = _movingPicturesId

    private val _movingPictures: MutableLiveData<IMDBMovie> = MutableLiveData()
    val movingPictures: LiveData<IMDBMovie> get() = _movingPictures

    init {

        // Load api_keys
        System.loadLibrary("movie-keys")
        imdbKey = getIMDBKey()


    }

    fun getMovieOrSeries(id: String) {
        viewModelScope.launch{
            repository.getMovieOrSeries(imdbKey, id).collectLatest {
                when (it) {
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        _movingPictures.value = it.data!!
                    }
                }
            }
        }
    }

    private external fun getIMDBKey(): String
}