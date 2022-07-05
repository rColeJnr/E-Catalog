package com.rick.screen_movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MovieCatalogViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */
//    val state: StateFlow<UiState>

    val pagingDataFLow: Flow<PagingData<Result>>

//    val accept: () -> Unit

    init {
//        val actionsStateFlow = MutableSharedFlow()

        pagingDataFLow = searchMovies().cachedIn(viewModelScope)

//        state = moviesScrolled.map { scroll ->
//            UiState(
//            )
//        }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
//                initialValue = UiState()
//            )
//
//        accept = {
//            viewModelScope.launch { actionsStateFlow.emit() }
//        }
    }

    private fun searchMovies(): Flow<PagingData<Result>> {
        return repository.getMovies()
    }

//    private fun jsonToJsonObject(result: Resource<MovieCatalog>): JSONObject {
//        return GsonParser(Gson()).toJsonObject(result.data!!, object : TypeToken<MovieCatalog>() {}.type)
//    }

//    fun loadMoreData(){
//        _isLoading.postValue(true)
//        paginationNumber += 10
//        fetchMovieCatalog(paginationNumber)
//    }
//
//    fun refreshData() {
//        _isRefreshing.postValue(true)
//        movieMutableList = mutableListOf()
//        fetchMovieCatalog(15)
//    }

    override fun onCleared() {
        //TODO(save last position i guess.)
        super.onCleared()
    }

//    companion object {
//        private val ITEMS_PER_PAGE = 20
//    }
}