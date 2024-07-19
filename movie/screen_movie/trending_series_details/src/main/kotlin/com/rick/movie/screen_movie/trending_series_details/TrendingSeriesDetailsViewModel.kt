package com.rick.movie.screen_movie.trending_series_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.domain_movie.GetTrendingSeriesByIdUseCase
import com.rick.data.model_movie.UserSeries
import com.rick.movie.screen_movie.common.util.SERIES_DETAILS_LIB_NAME
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

    private val tmdbKey: String

    private val seriesId = savedStateHandle.getStateFlow(key = SERIES_ID, initialValue = -1)

    val uiState: StateFlow<SeriesDetailsUiState>

    init {

        // Load api_keys
        System.loadLibrary(SERIES_DETAILS_LIB_NAME)
        tmdbKey = getTMDBKey()

        uiState = seriesId.flatMapLatest { id ->
            if (id == -1) {
                flowOf(SeriesDetailsUiState.Loading)
            } else {
                getTrendingSeriesByIdUseCase(
                    id = seriesId.value,
                    apiKey = tmdbKey
                )
                    .map<UserSeries, SeriesDetailsUiState> {
                        SeriesDetailsUiState.Success(it)
                    }
                    .catch { emit(SeriesDetailsUiState.Error(it.localizedMessage)) }
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

private external fun getTMDBKey(): String
private const val SERIES_ID = "seriesId"

sealed interface SeriesDetailsUiState {
    data class Success(val series: UserSeries) : SeriesDetailsUiState

    data object Loading : SeriesDetailsUiState
    data class Error(val msg: String?) : SeriesDetailsUiState
}