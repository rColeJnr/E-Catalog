package com.rick.screen_movie.trendingseries_details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.data.movie_favorite.repository.trending_series.CompositeTrendingSeriesRepository
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates


//FLOW

@HiltViewModel
class TvDetailsViewModel @Inject constructor(
    private val repository: CompositeTrendingSeriesRepository
) : ViewModel() {

    private val tmdbKey: String
    private var id by Delegates.notNull<Int>()

    private val _searchLoading: MutableLiveData<TvDetailsUiState.Loading> by
    lazy { MutableLiveData<TvDetailsUiState.Loading>(TvDetailsUiState.Loading(false)) }
    val searchLoading: LiveData<TvDetailsUiState.Loading> get() = _searchLoading

    private val _searchError: MutableLiveData<TvDetailsUiState.Error> by
    lazy { MutableLiveData<TvDetailsUiState.Error>() }
    val searchError: LiveData<TvDetailsUiState.Error> get() = _searchError

    private val _series: MutableLiveData<TvDetailsUiState.Tv> by
    lazy { MutableLiveData<TvDetailsUiState.Tv>() }
    val series: LiveData<TvDetailsUiState.Tv> get() = _series

    init {

        // Load api_keys
        System.loadLibrary(LIB_NAME)
        tmdbKey = getTMDBKey()
    }

    fun onEvent(event: TvDetailsEvents) {
        when (event) {
            is TvDetailsEvents.GetTv -> getTv(event.id)
        }
    }

    private fun getTv(id: Int) {
        viewModelScope.launch {
            repository.observeTrendingSeries(tmdbKey, viewModelScope)
        }
    }

}

private external fun getTMDBKey(): String

sealed class TvDetailsUiState {
    data class Tv(val tv: UserTrendingSeries) : TvDetailsUiState()
    data class Loading(val loading: Boolean) : TvDetailsUiState()
    data class Error(val msg: String?) : TvDetailsUiState()
}

sealed class TvDetailsEvents {
    data class GetTv(val id: Int) : TvDetailsEvents()
}

