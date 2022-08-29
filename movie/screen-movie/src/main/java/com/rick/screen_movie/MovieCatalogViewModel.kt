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
import java.time.LocalDate
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
    val key: String

    init {

        // Load api_keys
        System.loadLibrary("app-keys")
        key = getKey()

        val actionStateFlow = MutableSharedFlow<UiAction>()
        val refresh = actionStateFlow
            .filterIsInstance<UiAction.Refresh>()
            .distinctUntilChanged()
        val navigate = actionStateFlow
            .filterIsInstance<UiAction.NavigateToDetails>()
            .distinctUntilChanged()

        pagingDataFLow = searchMovies(key).cachedIn(viewModelScope)

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

    private var lastDisplayedLocalDate: LocalDate? = null

    private fun searchMovies(key: String): Flow<PagingData<UiModel>> =
        repository.getMovies(key)
            .map { pagingData -> pagingData.map { UiModel.MovieItem(it) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }
                    if (before == null) {
                        // we're at the beginning of the list
                        lastDisplayedLocalDate = after.getMonth(after.movie.openingDate)
                        return@insertSeparators UiModel.SeparatorItem(
                            "${after.getMonth(after.movie.openingDate).month}  " +
                                    "${after.getMonth(after.movie.openingDate).year}"
                        )
                    }
                    if (
                        after.getMonth(after.movie.openingDate)
                            .month.equals(before.getMonth(before.movie.openingDate).month)
                    ) {
                        null
                    } else {
                        UiModel.SeparatorItem(
                            "${after.getMonth(after.movie.openingDate).month}  " +
                                    "${after.getMonth(after.movie.openingDate).year}"
                        )
                    }
                }
            }


    //    private fun jsonToJsonObject(result: Resource<MovieCatalog>): JSONObject {
//        return GsonParser(Gson()).toJsonObject(result.data!!, object : TypeToken<MovieCatalog>() {}.type)
//    }
// TODO Add compatibility with API 24,
// TODO add logic, stop returning date.now()
}

private var previousDate: LocalDate? = null
fun UiModel.MovieItem.getMonth(date: String?): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val localDate = if (date != null) {
        previousDate = LocalDate.parse(date, formatter)
        previousDate
    } else if (previousDate == null) {
        previousDate = LocalDate.now()
        previousDate
    } else {
        previousDate
    }
    return localDate!!
}

external fun getKey(): String