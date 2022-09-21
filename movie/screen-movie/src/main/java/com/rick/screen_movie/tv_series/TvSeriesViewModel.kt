package com.rick.screen_movie.tv_series

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.data_movie.imdb.series_model.TvSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvSeriesViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    private val imdbKey: String

    init {

        // Load api_key
        System.loadLibrary("movie-keys")
        imdbKey = getIMDBKey()

    }

    private val _tvSeriesList: MutableLiveData<List<TvSeries>> by
    lazy { MutableLiveData<List<TvSeries>>() }
    val tvSeriesList: LiveData<List<TvSeries>> get() = _tvSeriesList

    private val _tvSeriesLoading: MutableLiveData<Boolean> by
    lazy { MutableLiveData<Boolean>(false) }
    val tvSeriesLoading: LiveData<Boolean> get() = _tvSeriesLoading

    private val _tvSeriesError: MutableLiveData<String> by
    lazy { MutableLiveData<String>() }
    val tvSeriesError: LiveData<String> get() = _tvSeriesError

    private fun getTvSeries() {
        viewModelScope.launch {
            repository.getTvSeries(imdbKey).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _tvSeriesList.postValue(result.data!!.tvSeries)
                    }
                }
            }
        }
    }

    private external fun getIMDBKey(): String
}