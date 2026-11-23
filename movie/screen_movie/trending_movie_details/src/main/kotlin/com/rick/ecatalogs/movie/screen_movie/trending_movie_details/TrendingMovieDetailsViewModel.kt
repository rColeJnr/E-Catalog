package com.rick.ecatalogs.movie.screen_movie.trending_movie_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.movie.data_movie.domain.GetTrendingMovieByIdUseCase
import com.rick.ecatalogs.movie.data_movie.model.UserMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


//FLOW

@HiltViewModel
class TrendingMovieDetailsViewModel @Inject constructor(
    private val getTrendingMovieByIdUseCase: GetTrendingMovieByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val movieId = savedStateHandle.getStateFlow(key = MOVIE_ID, initialValue = -1)

    val uiState: StateFlow<MovieDetailsUiState>

    init {
        uiState = movieId.flatMapLatest { id ->
            if (id == -1) {
                flowOf(MovieDetailsUiState.Loading)
            } else {
                getTrendingMovieByIdUseCase(id = movieId.value)
                    .map<UserMovie, MovieDetailsUiState> {
                        MovieDetailsUiState.Success(it)
                    }
                    .catch { emit(MovieDetailsUiState.Error(it.localizedMessage)) }
            }
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = MovieDetailsUiState.Loading
        )
    }

    fun setMovieId(id: Int) {
        savedStateHandle[MOVIE_ID] = id
    }

}

private const val MOVIE_ID = "movieId"

sealed interface MovieDetailsUiState {
    data class Success(val movie: UserMovie) : MovieDetailsUiState

    data object Loading : MovieDetailsUiState
    data class Error(val msg: String?) : MovieDetailsUiState
}