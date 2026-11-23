package com.rick.ecatalogs.movie.screen_movie.trending_series_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.movie.data_movie.domain.GetTrendingSeriesByIdUseCase
import com.rick.ecatalogs.movie.data_movie.model.UserSeries
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
class SeriesDetailsViewModel @Inject constructor(
    private val getTrendingSeriesByIdUseCase: GetTrendingSeriesByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val seriesId = savedStateHandle.getStateFlow(key = SERIES_ID, initialValue = -1)

    val uiState: StateFlow<SeriesDetailsUiState>

    init {
        uiState = seriesId.flatMapLatest { id ->
            if (id == -1) {
                flowOf(SeriesDetailsUiState.Loading)
            } else {
                getTrendingSeriesByIdUseCase(id = seriesId.value)
                    .map<UserSeries, SeriesDetailsUiState> {
                        SeriesDetailsUiState.Success(it)
                    }
                    .catch {
                        emit(SeriesDetailsUiState.Error(it.localizedMessage))
                        it.printStackTrace()
                    }
            }
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = SeriesDetailsUiState.Loading
        )
    }

    fun setSeriesId(id: Int) {
        savedStateHandle[SERIES_ID] = id
    }

}

private const val SERIES_ID = "seriesId"

sealed interface SeriesDetailsUiState {
    data class Success(val series: UserSeries) : SeriesDetailsUiState

    data object Loading : SeriesDetailsUiState
    data class Error(val msg: String?) : SeriesDetailsUiState
}