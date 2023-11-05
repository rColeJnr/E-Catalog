package com.rick.screen_movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.favorite.Favorite
import com.rick.screen_movie.util.LIB_NAME
import com.rick.screen_movie.util.TIME_FORMAT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    private val nyKey: String

    init {

        // Load api_keys
        System.loadLibrary(LIB_NAME)
        nyKey = getNYKey()

        pagingDataFLow = fetchMovies(nyKey).cachedIn(viewModelScope)

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

    fun onEvent(event: UiAction) {
        when (event) {
            is UiAction.InsertFavorite -> insertFavorite(event.fav)
            else -> {}
        }
    }

    private fun insertFavorite(fav: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavorite(fav)
        }
    }
}

private var previousDate: LocalDate? = null
private fun getMonth(date: String?): LocalDate {
    val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT)
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