package com.rick.ecatalogs.movie.screen_movie.trending_movie_favorite

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
import com.rick.ecatalogs.movie.screen_movie.trending_movie_favorite.databinding.MovieScreenMovieTrendingMovieFavoriteFragmentTrendingMovieFavoriteBinding
import com.rick.ecatalogs.data.ui_components.movie_favorite.MovieFavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrenidngMovieFavoriteFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingMovieFavoriteFragmentTrendingMovieFavoriteBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingMovieFavoriteViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

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
            EcsTheme {
                val moviesState by viewModel.feedTrendingMovieUiState.collectAsStateWithLifecycle()
                val shouldDisplayUndoMovieFavorite by viewModel.shouldDisplayUndoMovieFavorite.collectAsState()
                MovieFavScreen(
                    state = moviesState,
                    onFavClick = {
                        viewModel.onEvent(
                            FavoriteUiEvents.RemoveTrendingMovieFavorite(it.toInt())
                        )
                    },
                    shouldDisplayUndoFavorite = shouldDisplayUndoMovieFavorite,
                    clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) },
                    onItemClick = ::openMovie,
                    undoFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoTrendingMovieFavoriteRemoval) },
                )
            }
        }

        analyticsHelper.logScreenView("trendingMovieFavorite")

        return binding.root
    }

    private fun openMovie(id: String) {
        findNavController().navigateWithMotion(
            Uri.parse("com.rick.ecs://trending_movie_details_fragment/$id")
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}