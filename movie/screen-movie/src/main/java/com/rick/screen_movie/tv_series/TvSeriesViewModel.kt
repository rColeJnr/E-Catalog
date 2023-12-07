package com.rick.screen_movie.tv_series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.tmdb.trending_tv.TrendingTv
import com.rick.screen_movie.util.LIB_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvSeriesViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
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

    val pagingDataFlow: Flow<PagingData<TvSeriesUiState.Series>>
    private lateinit var favorite: Favorite
    private val tmdbKey: String

    init {

        // Load api_key
        System.loadLibrary(LIB_NAME)
//        imdbKey = getIMDBKey()
        tmdbKey = getTMDBKey()
        pagingDataFlow = getTrendingTv(tmdbKey).cachedIn(viewModelScope)
//        getTvSeries()

    }

    private fun getTrendingTv(key: String): Flow<PagingData<TvSeriesUiState.Series>> =
        repository.getTrendingTv(key)
            .map { pagingData -> pagingData.map { TvSeriesUiState.Series(it) } }


//    private fun getTvSeries() {
//        viewModelScope.launch {
//            repository.getTrendingTv(tmdbKey).collectLatest { result ->
//                when (result) {
//                    is Resource.Error -> {
//                        _tvSeriesError.postValue(TvSeriesUiState.Error(result.message))
//                    }
//                    is Resource.Loading -> {
//                        _tvSeriesLoading.postValue(TvSeriesUiState.Loading(result.isLoading))
//                    }
//                    is Resource.Success -> {
//                        _tvSeriesList.postValue(TvSeriesUiState.Series(result.data!!))
//                    }
//                }
//            }
//        }
//    }

    fun onEvent(event: TvSeriesUiEvent) {
        when (event) {
            is TvSeriesUiEvent.InsertFavorite -> insertFavorite(event.fav)
            else -> {}
        }
    }

    private fun insertFavorite(favorite: TrendingTv) {
        this.favorite = favorite.toFavorite()
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavorite(this@TvSeriesViewModel.favorite)
        }
    }

}

private external fun getTMDBKey(): String

sealed class TvSeriesUiState {
    data class Series(val trendingTv: TrendingTv) : TvSeriesUiState()
    data class Loading(val loading: Boolean) : TvSeriesUiState()
    data class Error(val msg: String?) : TvSeriesUiState()
}

sealed class TvSeriesUiEvent {
    data class InsertFavorite(val fav: TrendingTv): TvSeriesUiEvent()
    object RemoveFavorite: TvSeriesUiEvent()
}