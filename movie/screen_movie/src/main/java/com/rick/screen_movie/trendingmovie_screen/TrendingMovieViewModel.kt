package com.rick.screen_movie.trendingmovie_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.tmdb.trending_movie.TrendingMovie
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingMovieViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    private val apiKey: String
    val pagingDataFlow: Flow<PagingData<TrendingMovie>>
    private var favorite: Favorite? = null

    init {
        System.loadLibrary(LIB_NAME)
        apiKey = getTmdbKey()

        pagingDataFlow = getTrendingMovies().cachedIn(viewModelScope)
    }

    private fun getTrendingMovies(): Flow<PagingData<TrendingMovie>> =
        repository.getTrendingMovie(apiKey)

    fun onEvent(event: TrendingMovieUiEvent) {
        when (event) {
            is TrendingMovieUiEvent.InsertFavorite -> insertFavorite(event.favorite)
            is TrendingMovieUiEvent.RemoveFavorite -> removeFavorite()
        }
    }

    private fun insertFavorite(movie: TrendingMovie) {
        favorite = movie.toFavorite()
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavorite(favorite!!)
        }
    }

    private fun removeFavorite() {
        favorite?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.removeFavorite(it)
            }
        }
        favorite = null
    }

}

sealed class TrendingMovieUiEvent{
    data class InsertFavorite(val favorite: TrendingMovie): TrendingMovieUiEvent()
    object RemoveFavorite: TrendingMovieUiEvent()
}

private external fun getTmdbKey(): String
