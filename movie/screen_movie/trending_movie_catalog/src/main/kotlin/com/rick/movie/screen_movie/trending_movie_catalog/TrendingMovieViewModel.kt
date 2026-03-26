package com.rick.movie.screen_movie.trending_movie_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.movie.data_movie.data.repository.trending_movie.CompositeTrendingMovieRepository
import com.rick.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.movie.screen_movie.common.util.MOVIE_LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingMovieViewModel @Inject constructor(
    private val compositeMovieRepository: CompositeTrendingMovieRepository,
    private val userDataRepository: UserTrendingMovieDataRepository
) : ViewModel() {

    private val apiKey: String

    private val _uiState: MutableStateFlow<TrendingMovieUiState> =
        MutableStateFlow(TrendingMovieUiState.Loading)
    val uiState: StateFlow<TrendingMovieUiState> = _uiState.asStateFlow()


    init {
        System.loadLibrary(MOVIE_LIB_NAME)
        apiKey = getTmdbKey()

        getTrendingMovies()
    }

    private fun getTrendingMovies() {
        // We catch the flow from the repository and cache it in the viewModelScope
        val pagingFlow = compositeMovieRepository
            .observeTrendingMovie(apiKey, viewModelScope)
            .cachedIn(viewModelScope)

        // Update the StateFlow to Success with the paging data flow
        _uiState.value = TrendingMovieUiState.Success(movies = pagingFlow)
    }

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

sealed interface TrendingMovieUiState {
    data object Loading : TrendingMovieUiState
    data object Error : TrendingMovieUiState
    data class Success(val movies: Flow<PagingData<UserTrendingMovie>>) : TrendingMovieUiState
}
sealed class TrendingMovieUiEvent {
    data class UpdateTrendingMovieFavorite(val id: Int, val isFavorite: Boolean) :
        TrendingMovieUiEvent()
}

private external fun getTmdbKey(): String
