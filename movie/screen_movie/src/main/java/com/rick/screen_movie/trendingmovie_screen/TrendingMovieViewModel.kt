package com.rick.screen_movie.trendingmovie_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.data.movie_favorite.repository.trending_movie.CompositeTrendingMovieRepository
import com.rick.data.movie_favorite.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingMovieViewModel @Inject constructor(
    private val compositeMovieRepository: CompositeTrendingMovieRepository,
    private val userDataRepository: UserTrendingMovieDataRepository
) : ViewModel() {

    private val apiKey: String
    val pagingDataFlow: Flow<PagingData<UserTrendingMovie>>

    init {
        System.loadLibrary(LIB_NAME)
        apiKey = getTmdbKey()

        pagingDataFlow = getTrendingMovies().cachedIn(viewModelScope)
    }

    private fun getTrendingMovies(): Flow<PagingData<UserTrendingMovie>> =
        compositeMovieRepository.observeTrendingMovie(apiKey, viewModelScope)

    fun onEvent(event: TrendingMovieUiEvent) {
        when (event) {
            is TrendingMovieUiEvent.UpdateTrendingMovieFavorite -> updateTrendingMovieFavorite(
                event.id,
                event.isFavorite
            )
        }
    }

    private fun updateTrendingMovieFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setTrendingMovieFavoriteId(id, isFavorite)
        }
    }

}

sealed class TrendingMovieUiEvent {
    data class UpdateTrendingMovieFavorite(val id: Int, val isFavorite: Boolean) :
        TrendingMovieUiEvent()
}

private external fun getTmdbKey(): String
