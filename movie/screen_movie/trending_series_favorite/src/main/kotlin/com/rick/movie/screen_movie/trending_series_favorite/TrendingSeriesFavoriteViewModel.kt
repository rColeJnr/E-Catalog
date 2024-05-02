package com.rick.movie.screen_movie.trending_series_favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.model_movie.FavoriteUiEvents
import com.rick.data.model_movie.FavoriteUiState
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.movie.data_movie.data.repository.trending_series.UserTrendingSeriesDataRepository
import com.rick.movie.data_movie.data.repository.trending_series.UserTrendingSeriesRepository
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
class TrendingSeriesFavoriteViewModel @Inject constructor(
    private val userTrendingSeriesDataRepository: UserTrendingSeriesDataRepository,
    userTrendingSeriesRepository: UserTrendingSeriesRepository,
) : ViewModel() {

    val shouldDisplayUndoSeriesFavorite = MutableStateFlow(false)
    private var lastRemovedFavorite: Int? = null

    val feedTrendingSeriesUiState: StateFlow<FavoriteUiState> =
        userTrendingSeriesRepository.observeTrendingSeriesFavorite()
            .map<List<UserTrendingSeries>, FavoriteUiState>(FavoriteUiState::TrendingSeriesFavorites)
            .onStart { emit(FavoriteUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = FavoriteUiState.Loading
            )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.RemoveTrendingSeriesFavorite -> removeTrendingSeriesFavorite(event.seriesId)
            FavoriteUiEvents.UndoTrendingSeriesFavoriteRemoval -> undoTrendingSeriesFavoriteRemoval()
            FavoriteUiEvents.ClearUndoState -> clearUndoState()
            else -> {}
        }
    }

    private fun undoTrendingSeriesFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedFavorite?.let {
                userTrendingSeriesDataRepository.setTrendingSeriesFavoriteId(it, true)
            }
        }
//        clearUndoState()
    }

    private fun removeTrendingSeriesFavorite(seriesId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayUndoSeriesFavorite.value = true
            lastRemovedFavorite = seriesId
            userTrendingSeriesDataRepository.setTrendingSeriesFavoriteId(seriesId, false)
        }
    }

    private fun clearUndoState() {
        shouldDisplayUndoSeriesFavorite.value = false
        lastRemovedFavorite = null
    }
}

private const val SHOW_ARTICLES = "showArticles"
private const val SHOW_MOVIES = "showMovies"
private const val SHOW_SERIES = "showSeries"
