package com.rick.movie.screen_movie.trending_movie_favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.model_movie.FavoriteUiEvents
import com.rick.data.model_movie.FavoriteUiState
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.movie.data_movie.data.repository.trending_movie.UserTrendingMovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingMovieFavoriteViewModel @Inject constructor(
    private val userTrendingMovieDataRepository: UserTrendingMovieDataRepository,
    userTrendingMovieRepository: UserTrendingMovieRepository,
) : ViewModel() {

    val shouldDisplayUndoMovieFavorite = MutableStateFlow(false)
    private var lastRemovedFavorite: Int? = null

    val feedTrendingMovieUiState: StateFlow<FavoriteUiState> =
        userTrendingMovieRepository.observeTrendingMovieFavorite()
            .map<List<UserTrendingMovie>, FavoriteUiState>(FavoriteUiState::TrendingMoviesFavorites)
            .onStart { emit(FavoriteUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = FavoriteUiState.Loading
            )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.RemoveTrendingMovieFavorite -> removeTrendingMovieFavorite(event.movieId)
            FavoriteUiEvents.UndoTrendingMovieFavoriteRemoval -> undoTrendingMovieFavoriteRemoval()
            FavoriteUiEvents.ClearUndoState -> clearUndoState()
            else -> {}
        }
    }

    private fun undoTrendingMovieFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedFavorite?.let {
                userTrendingMovieDataRepository.setTrendingMovieFavoriteId(it, true)
            }
        }
//        clearUndoState()
    }

    private fun removeTrendingMovieFavorite(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayUndoMovieFavorite.value = true
            lastRemovedFavorite = movieId
            userTrendingMovieDataRepository.setTrendingMovieFavoriteId(movieId, false)
        }
    }

    private fun clearUndoState() {
        shouldDisplayUndoMovieFavorite.value = false
        lastRemovedFavorite = null
    }
}

private const val SHOW_ARTICLES = "showArticles"
private const val SHOW_MOVIES = "showMovies"
private const val SHOW_SERIES = "showSeries"
