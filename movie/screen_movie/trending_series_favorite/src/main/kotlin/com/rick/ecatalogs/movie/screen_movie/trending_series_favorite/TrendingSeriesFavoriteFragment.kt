package com.rick.ecatalogs.movie.screen_movie.trending_series_favorite

import com.rick.ecatalogs.data.ui_components.common.LocalTextTranslator
import androidx.compose.runtime.CompositionLocalProvider
import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import androidx.navigation.fragment.findNavController
import android.net.Uri
import android.os.Bundle
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.movie.data_movie.model.FavoriteUiEvents
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import com.rick.ecatalogs.movie.screen_movie.trending_series_favorite.databinding.MovieScreenMovieTrendingSeriesFavoriteFragmentTrendingSeriesFavoriteBinding
import com.rick.ecatalogs.data.ui_components.movie_favorite.MovieFavScreen
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

    @Inject
    lateinit var textTranslator: TextTranslator

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
            // Lets each card offer "Show translation" under its summary
            CompositionLocalProvider(LocalTextTranslator provides textTranslator) {
                EcsTheme {
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
                        onItemClick = ::openSeries,
                        undoFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoTrendingSeriesFavoriteRemoval) },
                    )
                }
            }
        }

        analyticsHelper.logScreenView("trendingSeriesFavorite")
        return binding.root
    }

    private fun openSeries(id: String) {
        findNavController().navigateWithMotion(
            Uri.parse("com.rick.ecs://trending_series_details_fragment/$id")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}