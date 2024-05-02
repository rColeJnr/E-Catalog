package com.rick.movie.screen_movie.trending_series_favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_movie.FavoriteUiEvents
import com.rick.movie.screen_movie.common.logScreenView
import com.rick.movie.screen_movie.trending_series_favorite.databinding.MovieScreenMovieTrendingSeriesFavoriteFragmentTrendingSeriesFavoriteBinding
import com.rick.ui_components.movie_favorite.MovieFavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrendingSeriesFavoriteFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingSeriesFavoriteFragmentTrendingSeriesFavoriteBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingSeriesFavoriteViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding =
            MovieScreenMovieTrendingSeriesFavoriteFragmentTrendingSeriesFavoriteBinding.inflate(
                inflater,
                container,
                false
            )

        binding.movieComposeView.setContent {
            val seriesState by viewModel.feedTrendingSeriesUiState.collectAsStateWithLifecycle()
            val shouldDisplayUndoSeriesFavorite by viewModel.shouldDisplayUndoSeriesFavorite.collectAsState()
            MovieFavScreen(
                state = seriesState,
                onFavClick = {
                    viewModel.onEvent(
                        FavoriteUiEvents.RemoveTrendingSeriesFavorite(it.toInt())
                    )
                },
                shouldDisplayUndoFavorite = shouldDisplayUndoSeriesFavorite,
                clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) },
                undoFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoTrendingSeriesFavoriteRemoval) },
            )
        }

        analyticsHelper.logScreenView("trendingSeriesFavorite")
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}