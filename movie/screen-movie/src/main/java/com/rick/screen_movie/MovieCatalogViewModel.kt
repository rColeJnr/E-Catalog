package com.rick.screen_movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.rick.data_movie.MovieCatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MovieCatalogViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */

    // we don't need this
    val pagingDataFLow: Flow<PagingData<UiModel>>

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

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchMovies(): Flow<PagingData<UiModel>> =
        repository.getMovies()
            .map { pagingData -> pagingData.map { UiModel.MovieItem(it) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }
                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators UiModel.SeparatorItem("${after.getMonth(after.movie.openingDate)}")

                    }
                    if (after.getMonth(after.movie.openingDate)
                            .isAfter(before.getMonth(before.movie.openingDate))
                    ) {
                        UiModel.SeparatorItem("${after.getMonth(after.movie.openingDate)}")
                    } else {
                        // no separator
                        null
                    }
                }
            }


//    private fun jsonToJsonObject(result: Resource<MovieCatalog>): JSONObject {
//        return GsonParser(Gson()).toJsonObject(result.data!!, object : TypeToken<MovieCatalog>() {}.type)
//    }
}

fun UiModel.MovieItem.getMonth(date: String?): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("L")
    date?.let {
        return LocalDateTime.parse(date, formatter)
    }
    return LocalDateTime.parse(LocalDateTime.now().format(formatter))
}