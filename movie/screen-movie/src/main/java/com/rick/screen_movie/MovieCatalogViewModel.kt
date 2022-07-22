package com.rick.screen_movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieCatalogViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */

    // we don't need this
    val pagingDataFLow: Flow<PagingData<Result>>

    val state: StateFlow<UiState>
    val accept: (UiAction) -> Unit

    init {

        val actionStateFlow = MutableSharedFlow<UiAction>()
        val refresh = actionStateFlow
            .filterIsInstance<UiAction.Refresh>()
            .distinctUntilChanged()
        val navigate = actionStateFlow
            .filterIsInstance<UiAction.NavigateToDetails>()
            .distinctUntilChanged()


        pagingDataFLow = searchMovies().cachedIn(viewModelScope)

        // TODO
        state = combine(refresh, navigate, ::Pair).map { (r, n) ->
            UiState(
                isRefreshing = r.refresh,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = UiState()
            )

        accept = {action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchMovies(): Flow<PagingData<Result>> {
        return repository.getMovies()
    }

//    private fun jsonToJsonObject(result: Resource<MovieCatalog>): JSONObject {
//        return GsonParser(Gson()).toJsonObject(result.data!!, object : TypeToken<MovieCatalog>() {}.type)
//    }
}