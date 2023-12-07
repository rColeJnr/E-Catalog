package com.rick.screen_movie.nymovie_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.ny_times.article_models.NYMovie
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
class NYMovieViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */

    val pagingDataFLow: Flow<PagingData<NYMovieUiModel>>

    private val nyKey: String
    private var favorite: Favorite? = null

    init {

        // Load api_keys
        System.loadLibrary(LIB_NAME)
        nyKey = getNYKey()

        pagingDataFLow = fetchMovies(nyKey).cachedIn(viewModelScope)

    }

    private fun fetchMovies(key: String): Flow<PagingData<NYMovieUiModel>> =
        repository.getMovies(key)
            .map { pagingData -> pagingData.map { NYMovieUiModel.MovieItem(it) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }
                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators NYMovieUiModel.SeparatorItem(
                            "${getMonth(after.movie.pubDate).month}  " +
                                    "${getMonth(after.movie.pubDate).year}"
                        )
                    }
                    if (
                        getMonth(after.movie.pubDate)
                            .month.equals(getMonth(before.movie.pubDate).month)
                    ) {
                        null
                    } else {
                        NYMovieUiModel.SeparatorItem(
                            "${getMonth(after.movie.pubDate).month}  " +
                                    "${getMonth(after.movie.pubDate).year}"
                        )
                    }
                }
            }

    fun onEvent(event: NYMovieUiEvent) {
        when (event) {
            is NYMovieUiEvent.InsertFavorite -> insertFavorite(event.fav.toFavorite())
            is NYMovieUiEvent.RemoveFavorite -> removeFavorite()
            else -> {}
        }
    }

    private fun insertFavorite(fav: Favorite) {
        favorite = fav
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavorite(favorite!!)
        }
    }

    private fun removeFavorite() {
        favorite?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.removeFavorite(it)
            }
        }
        favorite = null
    }
}

// This code is bad bcs i never cared to look into it.
private var previousDate: LocalDate? = null
private fun getMonth(date: String?): LocalDate {
    val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT)
    return if (date != null) {
        LocalDate.parse(date, formatter)
    } else run {
        LocalDate.now()
    }
}

private external fun getNYKey(): String

sealed class NYMovieUiModel {
    data class MovieItem(val movie: NYMovie): NYMovieUiModel()
    data class SeparatorItem(val description: String): NYMovieUiModel()
}

sealed class NYMovieUiEvent {
    data class InsertFavorite(val fav: NYMovie): NYMovieUiEvent()
    object RemoveFavorite: NYMovieUiEvent()
}
