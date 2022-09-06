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

    val pagingDataFLow: Flow<PagingData<UiModel>>

    val state: StateFlow<UiState>
    val accept: (UiAction) -> Unit
    private val nyKey: String
    private val imdbKey: String

    init {

        // Load api_keys
        System.loadLibrary("movie-keys")
        nyKey = getNYKey()
        imdbKey = getIMDBKey()

        val actionStateFlow = MutableSharedFlow<UiAction>()
//        val refresh = actionStateFlow
//            .filterIsInstance<UiAction.Refresh>()
//            .distinctUntilChanged()
        val navigate = actionStateFlow
            .filterIsInstance<UiAction.NavigateToDetails>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.NavigateToDetails(movie = null)) }

        pagingDataFLow = fetchMovies(nyKey).cachedIn(viewModelScope)

        state = navigate.map { UiState(navigatedAway = it.movie != null) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun fetchMovies(key: String): Flow<PagingData<UiModel>> =
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
                        return@insertSeparators UiModel.SeparatorItem(
                            "${getMonth(after.movie.openingDate).month}  " +
                                    "${getMonth(after.movie.openingDate).year}"
                        )
                    }
                    if (
                        getMonth(after.movie.openingDate)
                            .month.equals(getMonth(before.movie.openingDate).month)
                    ) {
                        null
                    } else {
                        UiModel.SeparatorItem(
                            "${getMonth(after.movie.openingDate).month}  " +
                                    "${getMonth(after.movie.openingDate).year}"
                        )
                    }
                }
            }

    private fun getMovieId(query: String): String {
        viewModelScope.launch{
        }
        return ""
    }
}

private var previousDate: LocalDate? = null
private fun getMonth(date: String?): LocalDate {
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

private external fun getNYKey(): String
private external fun getIMDBKey(): String