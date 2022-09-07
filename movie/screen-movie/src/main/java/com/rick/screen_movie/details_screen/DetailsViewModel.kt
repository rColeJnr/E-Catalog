package com.rick.screen_movie.details_screen

import androidx.lifecycle.*
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

    private var movieOrSeriesId: MutableLiveData<String> = MutableLiveData()

    private val _movingPictures: MutableLiveData<IMDBMovie> = MutableLiveData()
    val movingPictures: LiveData<IMDBMovie> get() = _movingPictures

    init {

        // Load api_keys
        System.loadLibrary("movie-keys")
        imdbKey = getIMDBKey()

        // Simplify, you over thought this
        // if i were this code i wouldn't even run
        viewModelScope.launch{
            movieOrSeriesId.asFlow().collectLatest {
                getMovieOrSeries()
            }
        }

    }

    fun setMovieOrSeriesId(id: String) {
        movieOrSeriesId.value = id
    }

    private fun getMovieOrSeries() {
        viewModelScope.launch{
            repository.getMovieOrSeries(imdbKey, movieOrSeriesId.value.toString()).collectLatest {
                when (it) {
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        _movingPictures.postValue(it.data!!)
                    }
                }
            }
        }
    }

    private external fun getIMDBKey(): String
}