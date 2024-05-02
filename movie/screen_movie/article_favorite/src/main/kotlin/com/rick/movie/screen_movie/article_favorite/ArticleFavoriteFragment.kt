package com.rick.movie.screen_movie.article_favorite

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
import com.rick.movie.screen_movie.article_favorite.databinding.MovieScreenMovieArticleFavoriteFragmentArticleFavoriteBinding
import com.rick.movie.screen_movie.common.logScreenView
import com.rick.ui_components.movie_favorite.MovieFavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArticleFavoriteFragment : Fragment() {

    private var _binding: MovieScreenMovieArticleFavoriteFragmentArticleFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArticleFavoriteViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = MovieScreenMovieArticleFavoriteFragmentArticleFavoriteBinding.inflate(
            inflater,
            container,
            false
        )

        binding.movieComposeView.setContent {
            val articlesState by viewModel.feedArticlesUiState.collectAsStateWithLifecycle()
            val shouldDisplayUndoArticleFavorite by viewModel.shouldDisplayUndoArticleFavorite.collectAsState()
            MovieFavScreen(
                state = articlesState,
                onFavClick = {
                    viewModel.onEvent(
                        FavoriteUiEvents.RemoveArticleFavorite(it)
                    )
                },
                shouldDisplayUndoFavorite = shouldDisplayUndoArticleFavorite,
                clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) },
                undoFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoArticleFavoriteRemoval) },
            )
        }

        analyticsHelper.logScreenView("articleFavorite")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}