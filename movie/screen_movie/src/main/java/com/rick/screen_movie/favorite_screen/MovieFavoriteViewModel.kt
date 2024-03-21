package com.rick.screen_movie.favorite_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.favorite.Favorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO FAVORITE
@HiltViewModel
class MovieFavoriteViewModel @Inject constructor(
    private val repo: MovieCatalogRepository
) : ViewModel() {

    private val _movies = MutableLiveData<List<Favorite>>()
    val movie: LiveData<List<Favorite>> get() = _movies

    private val _loadingMovies = MutableLiveData<Boolean>()
    val loadingMovies: LiveData<Boolean> get() = _loadingMovies

    private val _nyMovies = MutableLiveData<List<Favorite>>()
    val nyMovie: LiveData<List<Favorite>> get() = _nyMovies

    private val _loadingNyMovies = MutableLiveData<Boolean>()
    val loadingNyMovies: LiveData<Boolean> get() = _loadingNyMovies

    private val _series = MutableLiveData<List<Favorite>>()
    val series: LiveData<List<Favorite>> get() = _series

    private val _loadingSeries = MutableLiveData<Boolean>()
    val loadingSeries: LiveData<Boolean> get() = _loadingSeries

    var showArticles = MutableStateFlow(false)
        private set
    var showMovies = MutableStateFlow(false)
        private set
    var showSeries = MutableStateFlow(false)
        private set

    init {
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch {
            repo.getFavoriteMovies().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loadingMovies.value = result.isLoading
                    }

                    is Resource.Success -> {
                        _movies.value = result.data!!
                    }

                    else -> {}
                }
            }

            repo.getFavoriteSeries().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loadingSeries.value = result.isLoading
                    }

                    is Resource.Success -> {
                        _series.value = result.data!!
                    }

                    else -> {}
                }
            }

            repo.getFavoriteNyMovies().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loadingNyMovies.value = result.isLoading
                    }

                    is Resource.Success -> {
                        _nyMovies.value = result.data!!
                    }

                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: UiEvents) {
        when (event) {
            is UiEvents.ShouldInsertFavorite -> shouldInsertFavorite(event.fav)
            is UiEvents.DeleteFavorite -> deleteFavorite(event.fav)
            UiEvents.ShouldShowArticles -> shouldShowArticles()
            UiEvents.ShouldShowMovies -> shouldShowMovies()
            UiEvents.ShouldShowSeries -> shouldShowSeries()
        }
    }

    private fun shouldInsertFavorite(fav: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavorite(fav)
        }
    }

    private fun deleteFavorite(fav: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeFavorite(fav)
        }
    }

    private fun shouldShowArticles() {
        showArticles.value = !showArticles.value
    }

    private fun shouldShowMovies() {
        showMovies.value = !showMovies.value
    }

    private fun shouldShowSeries() {
        showSeries.value = !showSeries.value
    }

}

sealed class UiEvents {
    data class ShouldInsertFavorite(val fav: Favorite) : UiEvents()
    data class DeleteFavorite(val fav: Favorite) : UiEvents()
    object ShouldShowArticles: UiEvents()
    object ShouldShowMovies: UiEvents()
    object ShouldShowSeries: UiEvents()
}