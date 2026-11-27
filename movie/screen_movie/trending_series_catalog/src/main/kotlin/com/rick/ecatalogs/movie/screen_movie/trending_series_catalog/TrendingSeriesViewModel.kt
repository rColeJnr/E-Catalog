package com.rick.ecatalogs.movie.screen_movie.trending_series_catalog

import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbGenre
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.TmdbBrowseRepository
import androidx.paging.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.ecatalogs.movie.data_movie.model.UserTrendingSeries
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_series.CompositeTrendingSeriesRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_series.UserTrendingSeriesDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrendingSeriesViewModel @Inject constructor(
    private val compositeTrendingSeriesRepository: CompositeTrendingSeriesRepository,
    private val userDataRepository: UserTrendingSeriesDataRepository,
    private val browseRepository: TmdbBrowseRepository,
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

    /** null = trending; otherwise the TMDB genre whose most popular series are listed. */
    private val _selectedGenre = MutableStateFlow<Int?>(null)
    val selectedGenre: StateFlow<Int?> = _selectedGenre.asStateFlow()

    private val trending = compositeTrendingSeriesRepository
        .observeTrendingSeries(viewModelScope)
        .cachedIn(viewModelScope)

    val pagingDataFlow: Flow<PagingData<UserTrendingSeries>> = _selectedGenre
        .flatMapLatest { genreId ->
            if (genreId == null) {
                trending
            } else {
                browseRepository.discoverSeries(genreId)
                    .cachedIn(viewModelScope)
                    .combine(userDataRepository.userData) { series, userData ->
                        series.map { UserTrendingSeries(it, userData) }
                    }
            }
        }
        .cachedIn(viewModelScope)

    /** "Airing today". */
    val airingToday: StateFlow<List<TrendingSeries>> = browseRepository.airingTodaySeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentlyReleased: StateFlow<List<TrendingSeries>> = browseRepository.recentlyReleasedSeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val genres: StateFlow<List<TmdbGenre>> = flow { emit(browseRepository.seriesGenres()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectGenre(genreId: Int?) {
        _selectedGenre.value = genreId
    }

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

//sealed class TvSeriesUiState {
//    data class Series(val trendingTv: TrendingTv) : TvSeriesUiState()
//    data class Loading(val loading: Boolean) : TvSeriesUiState()
//    data class Error(val msg: String?) : TvSeriesUiState()
//}

sealed class TrendingSeriesUiEvent {
    data class UpdateTrendingSeriesFavorite(val id: Int, val isFavorite: Boolean) :
        TrendingSeriesUiEvent()

}