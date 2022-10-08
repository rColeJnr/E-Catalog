package com.rick.screen_movie.tv_series

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb.series_model.TvSeries
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvSeriesViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    private val _tvSeriesList: MutableLiveData<TvSeriesUiState.Series> by
    lazy { MutableLiveData<TvSeriesUiState.Series>() }
    val tvSeriesList: LiveData<TvSeriesUiState.Series> get() = _tvSeriesList

    private val _tvSeriesLoading: MutableLiveData<TvSeriesUiState.Loading> by
    lazy { MutableLiveData<TvSeriesUiState.Loading>() }
    val tvSeriesLoading: LiveData<TvSeriesUiState.Loading> get() = _tvSeriesLoading

    private val _tvSeriesError: MutableLiveData<TvSeriesUiState.Error> by
    lazy { MutableLiveData<TvSeriesUiState.Error>() }
    val tvSeriesError: LiveData<TvSeriesUiState.Error> get() = _tvSeriesError

    private val imdbKey: String

    init {

        // Load api_key
        System.loadLibrary(LIB_NAME)
        imdbKey = getIMDBKey()

        getTvSeries()

    }

    private fun getTvSeries() {
        viewModelScope.launch {
            repository.getTvSeries(imdbKey).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _tvSeriesError.postValue(TvSeriesUiState.Error(result.message))
                    }
                    is Resource.Loading -> {
                        _tvSeriesLoading.postValue(TvSeriesUiState.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        _tvSeriesList.postValue(TvSeriesUiState.Series(result.data!!))
                    }
                }
            }
        }
    }

    private external fun getIMDBKey(): String
}

sealed class TvSeriesUiState {
    data class Series(val series: List<TvSeries>) : TvSeriesUiState()
    data class Loading(val loading: Boolean) : TvSeriesUiState()
    data class Error(val msg: String?) : TvSeriesUiState()
}