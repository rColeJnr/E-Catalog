package com.rick.movie.screen_movie.trending_movie_detatils

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.domain_movie.GetTrendingMovieByIdUseCase
import com.rick.data.model_movie.UserMovie
import com.rick.movie.screen_movie.common.util.MOVIE_DETAILS_LIB_NAME
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

    private val tmdbKey: String

    val movieId = savedStateHandle.getStateFlow(key = MOVIE_ID, initialValue = -1)

    val uiState: StateFlow<MovieDetailsUiState>

    init {

        // Load api_keys
        System.loadLibrary(MOVIE_DETAILS_LIB_NAME)
        tmdbKey = getTMDBKey()

        uiState = movieId.flatMapLatest { id ->
            if (id == -1) {
                flowOf(MovieDetailsUiState.Loading)
            } else {
                getTrendingMovieByIdUseCase(
                    id = movieId.value,
                    apiKey = tmdbKey
                )
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

private external fun getTMDBKey(): String
private const val MOVIE_ID = "movieId"

sealed interface MovieDetailsUiState {
    data class Success(val movie: UserMovie) : MovieDetailsUiState

    data object Loading : MovieDetailsUiState
    data class Error(val msg: String?) : MovieDetailsUiState
}