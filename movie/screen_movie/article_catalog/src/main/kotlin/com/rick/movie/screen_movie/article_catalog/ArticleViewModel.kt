package com.rick.movie.screen_movie.article_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data.model_movie.UserArticle
import com.rick.movie.data_movie.data.repository.article.CompositeArticleRepository
import com.rick.movie.data_movie.data.repository.article.UserArticleDataRepository
import com.rick.movie.screen_movie.common.util.ARTICLE_LIB_NAME
import com.rick.movie.screen_movie.common.util.TIME_FORMAT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NYMovieViewModel @Inject constructor(
    private val userDataRepository: UserArticleDataRepository,
    private val compositeArticleRepository: CompositeArticleRepository
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */

    val pagingDataFLow: Flow<PagingData<UserArticle>>

    private val nyKey: String

    init {

        // Load api_keys
        System.loadLibrary(ARTICLE_LIB_NAME)
        nyKey = getNYKey()

        pagingDataFLow = fetchMovies().cachedIn(viewModelScope)
    }

    private fun fetchMovies(): Flow<PagingData<UserArticle>> =
        compositeArticleRepository.observeArticle(nyKey, viewModelScope)
    // Removed for consistency across application
//            .map { pagingData -> pagingData.map { NYMovieUiModel.MovieItem(it) } }
//            .map {
//                it.insertSeparators { before, after ->
//                    if (after == null) {
//                        // we're at the end of the list
//                        return@insertSeparators null
//                    }
//                    if (before == null) {
////                         we're at the beginning of the list
//                        return@insertSeparators NYMovieUiModel.SeparatorItem(
//                            "${getMonth(after.movie.pubDate.substring(0, 10)).month}  " +
//                                    "${getMonth(after.movie.pubDate.substring(0,10)).year}"
//                        )
//                        null
//                    }
//                    if (
//                        getMonth(after.movie.pubDate.substring(0,10))
//                            .month.equals(getMonth(before?.movie?.pubDate?.substring(0,10)).month)
//                    ) {
//                        null
//                    } else {
//                        NYMovieUiModel.SeparatorItem(
//                            "${getMonth(after.movie.pubDate.substring(0,10)).month}  " +
//                                    "${getMonth(after.movie.pubDate.substring(0,10)).year}"
//                        )
//                        null
//                    }
//                }
//            }

    fun onEvent(event: NYMovieUiEvent) {
        when (event) {
            is NYMovieUiEvent.UpdateArticleFavorite -> updateArticleFavorite(
                event.id,
                event.isFavorite
            )
        }
    }

    private fun updateArticleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setNyTimesArticleFavoriteId(id, isFavorite)
        }
    }
}

// This code is bad bcs i never cared to look into it.
private var previousDate: LocalDate? = null
private fun getMonth(date: String?): LocalDate {
    val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT)
    return if (date != null) {
        LocalDate.parse(date, formatter)
        LocalDate.now()
    } else run {
        LocalDate.now()
    }
}

private external fun getNYKey(): String

sealed class NYMovieUiEvent {
    data class UpdateArticleFavorite(val id: Long, val isFavorite: Boolean) : NYMovieUiEvent()
}
