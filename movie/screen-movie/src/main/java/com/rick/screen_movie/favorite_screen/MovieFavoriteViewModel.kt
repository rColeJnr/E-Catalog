package com.rick.screen_movie.favorite_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.favorite.Favorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieFavoriteViewModel @Inject constructor(private val repo: MovieCatalogRepository) :
    ViewModel() {

    private val _movies = MutableLiveData<List<Favorite>>()
    val movie: LiveData<List<Favorite>> get() = _movies

    private val _loadingMovies = MutableLiveData<Boolean>()
    val loadingMovies: LiveData<Boolean> get() = _loadingMovies

    private val _series = MutableLiveData<List<Favorite>>()
    val series: LiveData<List<Favorite>> get() = _series

    private val _loadingSeries = MutableLiveData<Boolean>()
    val loadingSeries: LiveData<Boolean> get() = _loadingSeries

    init {
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch {
            repo.getFavoriteMovies().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loadingMovies.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _movies.postValue(result.data ?: listOf())
                    }
                    else -> {}
                }
            }

            repo.getFavoriteSeries().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loadingSeries.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _series.postValue(result.data ?: listOf())
                    }
                    else -> {}
                }
            }
        }
    }

}