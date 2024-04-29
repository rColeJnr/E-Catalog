package com.rick.movie.screen_movie.trending_movie_favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.data.model_movie.FavoriteUiEvents
import com.rick.movie.screen_movie.trending_movie_favorite.databinding.MovieScreenMovieTrendingMovieFavoriteFragmentTrendingMovieFavoriteBinding
import com.rick.ui_components.movie_favorite.MovieFavScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrenidngMovieFavoriteFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingMovieFavoriteFragmentTrendingMovieFavoriteBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingMovieFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding =
            MovieScreenMovieTrendingMovieFavoriteFragmentTrendingMovieFavoriteBinding.inflate(
                inflater,
                container,
                false
            )

        binding.movieComposeView.setContent {
            val articlesState by viewModel.feedArticlesUiState.collectAsStateWithLifecycle()
            val moviesState by viewModel.feedTrendingMovieUiState.collectAsStateWithLifecycle()
            val seriesState by viewModel.feedTrendingSeriesUiState.collectAsStateWithLifecycle()
            val showArticles by viewModel.showArticles.collectAsState()
            val showMovies by viewModel.showMovies.collectAsState()
            val showSeries by viewModel.showSeries.collectAsState()
            val shouldDisplayUndoArticleFavorite by viewModel.shouldDisplayUndoArticleFavorite.collectAsState()
            val shouldDisplayUndoMovieFavorite by viewModel.shouldDisplayUndoMovieFavorite.collectAsState()
            val shouldDisplayUndoSeriesFavorite by viewModel.shouldDisplayUndoSeriesFavorite.collectAsState()
            MovieFavScreen(
                articlesState = articlesState,
                moviesState = moviesState,
                seriesState = seriesState,
                showArticles = showArticles,
                showMovies = showMovies,
                showSeries = showSeries,
                shouldShowArticles = { viewModel.onEvent(FavoriteUiEvents.ShouldShowArticles(it)) },
                shouldShowMovies = { viewModel.onEvent(FavoriteUiEvents.ShouldShowMovies(it)) },
                shouldShowSeries = { viewModel.onEvent(FavoriteUiEvents.ShouldShowSeries(it)) },
                onArticleFavClick = {
                    viewModel.onEvent(
                        FavoriteUiEvents.RemoveArticleFavorite(it.toLong())
                    )
                },
                onMovieFavClick = {
                    viewModel.onEvent(
                        FavoriteUiEvents.RemoveTrendingMovieFavorite(it.toInt())
                    )
                },
                onSeriesFavClick = {
                    viewModel.onEvent(
                        FavoriteUiEvents.RemoveTrendingSeriesFavorite(it.toInt())
                    )
                },
                shouldDisplayArticleUndoFavorite = shouldDisplayUndoArticleFavorite,
                shouldDisplayMovieUndoFavorite = shouldDisplayUndoMovieFavorite,
                shouldDisplaySeriesUndoFavorite = shouldDisplayUndoSeriesFavorite,
                clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) },
                undoArticleFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoArticleFavoriteRemoval) },
                undoMovieFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoTrendingMovieFavoriteRemoval) },
                undoSeriesFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoTrendingSeriesFavoriteRemoval) }
            )
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}