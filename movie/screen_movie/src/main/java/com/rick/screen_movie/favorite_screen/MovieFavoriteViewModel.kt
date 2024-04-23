package com.rick.screen_movie.favorite_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.model_movie.FavoriteUiEvents
import com.rick.data.model_movie.FavoriteUiState
import com.rick.data.model_movie.UserArticle
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.data.movie_favorite.repository.article.UserArticleDataRepository
import com.rick.data.movie_favorite.repository.article.UserArticlesRepository
import com.rick.data.movie_favorite.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.data.movie_favorite.repository.trending_movie.UserTrendingMovieRepository
import com.rick.data.movie_favorite.repository.trending_series.UserTrendingSeriesDataRepository
import com.rick.data.movie_favorite.repository.trending_series.UserTrendingSeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieFavoriteViewModel @Inject constructor(
    private val userTrendingMovieDataRepository: UserTrendingMovieDataRepository,
    private val userTrendingSeriesDataRepository: UserTrendingSeriesDataRepository,
    private val userArticleDataRepository: UserArticleDataRepository,
    userArticlesRepository: UserArticlesRepository,
    userTrendingMovieRepository: UserTrendingMovieRepository,
    userTrendingSeriesRepository: UserTrendingSeriesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val showArticles = savedStateHandle.getStateFlow(SHOW_ARTICLES, false)

    val showMovies = savedStateHandle.getStateFlow(SHOW_MOVIES, false)

    val showSeries = savedStateHandle.getStateFlow(SHOW_SERIES, false)

    val shouldDisplayUndoArticleFavorite = MutableStateFlow(false)
    val shouldDisplayUndoMovieFavorite = MutableStateFlow(false)
    val shouldDisplayUndoSeriesFavorite = MutableStateFlow(false)
    private var lastRemovedFavorite: Int? = null
    private var lastRemovedArticleFavorite: Long? = null

    val feedTrendingMovieUiState: StateFlow<FavoriteUiState> =
        userTrendingMovieRepository.observeTrendingMovieFavorite()
            .map<List<UserTrendingMovie>, FavoriteUiState>(FavoriteUiState::TrendingMoviesFavorites)
            .onStart { emit(FavoriteUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = FavoriteUiState.Loading
            )

    val feedTrendingSeriesUiState: StateFlow<FavoriteUiState> =
        userTrendingSeriesRepository.observeTrendingSeriesFavorite()
            .map<List<UserTrendingSeries>, FavoriteUiState>(FavoriteUiState::TrendingSeriesFavorites)
            .onStart { emit(FavoriteUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = FavoriteUiState.Loading
            )

    val feedArticlesUiState: StateFlow<FavoriteUiState> =
        userArticlesRepository.observeArticleFavorite()
            .map<List<UserArticle>, FavoriteUiState>(FavoriteUiState::ArticlesFavorites)
            .onStart { emit(FavoriteUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = FavoriteUiState.Loading
            )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.ShouldShowArticles -> shouldShowArticles(event.show)
            is FavoriteUiEvents.ShouldShowMovies -> shouldShowMovies(event.show)
            is FavoriteUiEvents.ShouldShowSeries -> shouldShowSeries(event.show)
            is FavoriteUiEvents.RemoveArticleFavorite -> removeArticleFavorite(event.articleId)
            is FavoriteUiEvents.RemoveTrendingSeriesFavorite -> removeTrendingSeriesFavorite(event.seriesId)
            is FavoriteUiEvents.RemoveTrendingMovieFavorite -> removeTrendingMovieFavorite(event.movieId)
            FavoriteUiEvents.UndoArticleFavoriteRemoval -> undoArticleFavoriteRemoval()
            FavoriteUiEvents.UndoTrendingSeriesFavoriteRemoval -> undoTrendingSeriesFavoriteRemoval()
            FavoriteUiEvents.UndoTrendingMovieFavoriteRemoval -> undoTrendingMovieFavoriteRemoval()
            FavoriteUiEvents.ClearUndoState -> clearUndoState()
        }
    }

    private fun undoTrendingMovieFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedFavorite?.let {
                userTrendingMovieDataRepository.setTrendingMovieFavoriteId(it, true)
            }
        }
//        clearUndoState()
    }

    private fun undoTrendingSeriesFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedFavorite?.let {
                userTrendingSeriesDataRepository.setTrendingSeriesFavoriteId(it, true)
            }
        }
//        clearUndoState()
    }

    private fun undoArticleFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedArticleFavorite?.let {
                userArticleDataRepository.setNyTimesArticleFavoriteId(it, true)
            }
        }
//        clearUndoState()
    }

    private fun removeTrendingMovieFavorite(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayUndoMovieFavorite.value = true
            lastRemovedFavorite = movieId
            userTrendingMovieDataRepository.setTrendingMovieFavoriteId(movieId, false)
        }
    }

    private fun removeTrendingSeriesFavorite(seriesId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayUndoSeriesFavorite.value = true
            lastRemovedFavorite = seriesId
            userTrendingSeriesDataRepository.setTrendingSeriesFavoriteId(seriesId, false)
        }
    }

    private fun removeArticleFavorite(articleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayUndoArticleFavorite.value = true
            lastRemovedArticleFavorite = articleId
            userArticleDataRepository.setNyTimesArticleFavoriteId(articleId, false)
        }
    }

    private fun shouldShowArticles(show: Boolean) {
        savedStateHandle[SHOW_ARTICLES] = show
    }

    private fun shouldShowMovies(show: Boolean) {
        savedStateHandle[SHOW_MOVIES] = show
    }

    private fun shouldShowSeries(show: Boolean) {
        savedStateHandle[SHOW_SERIES] = show
    }

    private fun clearUndoState() {
        shouldDisplayUndoArticleFavorite.value = false
        shouldDisplayUndoMovieFavorite.value = false
        shouldDisplayUndoSeriesFavorite.value = false
        lastRemovedFavorite = null
        lastRemovedArticleFavorite = null
    }
}

private const val SHOW_ARTICLES = "showArticles"
private const val SHOW_MOVIES = "showMovies"
private const val SHOW_SERIES = "showSeries"
