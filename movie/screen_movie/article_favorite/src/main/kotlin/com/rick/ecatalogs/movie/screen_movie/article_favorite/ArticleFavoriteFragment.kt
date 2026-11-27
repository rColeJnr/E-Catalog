package com.rick.ecatalogs.movie.screen_movie.article_favorite

import com.rick.ecatalogs.data.ui_components.common.LocalTextTranslator
import androidx.compose.runtime.CompositionLocalProvider
import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.movie.data_movie.model.FavoriteUiState
import com.rick.ecatalogs.movie.screen_movie.common.articleDetailsUri
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import androidx.navigation.fragment.findNavController
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
import com.rick.ecatalogs.movie.screen_movie.article_favorite.databinding.MovieScreenMovieArticleFavoriteFragmentArticleFavoriteBinding
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import com.rick.ecatalogs.data.ui_components.movie_favorite.MovieFavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArticleFavoriteFragment : Fragment() {

    private var _binding: MovieScreenMovieArticleFavoriteFragmentArticleFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArticleFavoriteViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var textTranslator: TextTranslator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = MovieScreenMovieArticleFavoriteFragmentArticleFavoriteBinding.inflate(
            inflater,
            container,
            false
        )

        binding.movieComposeView.setContent {
            // Lets each card offer "Show translation" under its summary
            CompositionLocalProvider(LocalTextTranslator provides textTranslator) {
                EcsTheme {
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
                        onItemClick = ::openArticle,
                        undoFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoArticleFavoriteRemoval) },
                    )
                }
            }
        }

        analyticsHelper.logScreenView("articleFavorite")

        return binding.root
    }

    /** The review's details screen, as from the Movie Reviews list. */
    private fun openArticle(id: String) {
        findNavController().navigateWithMotion(articleDetailsUri(id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}