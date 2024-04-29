package com.rick.movie.screen_movie.trending_series_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.movie.data_movie.data.repository.trending_series.CompositeTrendingSeriesRepository
import com.rick.movie.data_movie.data.repository.trending_series.UserTrendingSeriesDataRepository
import com.rick.movie.screen_movie.common.util.SERIES_LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingSeriesViewModel @Inject constructor(
    private val compositeTrendingSeriesRepository: CompositeTrendingSeriesRepository,
    private val userDataRepository: UserTrendingSeriesDataRepository
) : ViewModel() {

//    private val _tvSeriesList: MutableLiveData<TvSeriesUiState.Series> by
//    lazy { MutableLiveData<TvSeriesUiState.Series>() }
//    val tvSeriesList: LiveData<TvSeriesUiState.Series> get() = _tvSeriesList
//
//    private val _tvSeriesLoading: MutableLiveData<TvSeriesUiState.Loading> by
//    lazy { MutableLiveData<TvSeriesUiState.Loading>() }
//    val tvSeriesLoading: LiveData<TvSeriesUiState.Loading> get() = _tvSeriesLoading
//
//    private val _tvSeriesError: MutableLiveData<TvSeriesUiState.Error> by
//    lazy { MutableLiveData<TvSeriesUiState.Error>() }
//    val tvSeriesError: LiveData<TvSeriesUiState.Error> get() = _tvSeriesError

//    private val imdbKey: String

    val pagingDataFlow: Flow<PagingData<UserTrendingSeries>>

    private val tmdbKey: String

    init {

        // Load api_key
        System.loadLibrary(SERIES_LIB_NAME)
//        imdbKey = getIMDBKey()
        tmdbKey = getTMDBKey()
        pagingDataFlow = getTrendingSeries().cachedIn(viewModelScope)
//        getTvSeries()

    }

    private fun getTrendingSeries(): Flow<PagingData<UserTrendingSeries>> =
        compositeTrendingSeriesRepository.observeTrendingSeries(tmdbKey, viewModelScope)

    fun onEvent(event: TrendingSeriesUiEvent) {
        when (event) {
            is TrendingSeriesUiEvent.UpdateTrendingSeriesFavorite -> updateTrendingSeriesFavorite(
                event.id, event.isFavorite
            )
        }
    }

    private fun updateTrendingSeriesFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setTrendingSeriesFavoriteId(id, isFavorite)
        }
    }

}

private external fun getTMDBKey(): String

//sealed class TvSeriesUiState {
//    data class Series(val trendingTv: TrendingTv) : TvSeriesUiState()
//    data class Loading(val loading: Boolean) : TvSeriesUiState()
//    data class Error(val msg: String?) : TvSeriesUiState()
//}

sealed class TrendingSeriesUiEvent {
    data class UpdateTrendingSeriesFavorite(val id: Int, val isFavorite: Boolean) :
        TrendingSeriesUiEvent()

}