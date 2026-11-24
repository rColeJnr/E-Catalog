package com.rick.ecatalogs.movie.screen_movie.article_favorite

import java.nio.charset.StandardCharsets
import java.net.URLEncoder
import com.rick.ecatalogs.movie.data_movie.model.FavoriteUiState
import com.rick.ecatalogs.movie.screen_movie.common.ArticleDetailsDialogFragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = MovieScreenMovieArticleFavoriteFragmentArticleFavoriteBinding.inflate(
            inflater,
            container,
            false
        )

        binding.movieComposeView.setContent {
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

        analyticsHelper.logScreenView("articleFavorite")

        return binding.root
    }

    /** The same review dialog as the Movie Reviews list. */
    private fun openArticle(id: String) {
        val article = (viewModel.feedArticlesUiState.value as? FavoriteUiState.ArticlesFavorites)
            ?.favorites?.firstOrNull { it.id == id } ?: return
        ArticleDetailsDialogFragment(
            article,
            onDialogFavoriteClick = { _, articleId, isFavorite ->
                // Un-starring removes it here (with the usual undo); starring again restores it
                viewModel.onEvent(
                    if (isFavorite) FavoriteUiEvents.RemoveArticleFavorite(articleId)
                    else FavoriteUiEvents.UndoArticleFavoriteRemoval
                )
            },
            onWebUrlClick = { link ->
                val encoded = URLEncoder.encode(link, StandardCharsets.UTF_8.toString())
                findNavController().navigateWithMotion(
                    Uri.parse("com.rick.ecs://movie_common_webviewfragment/$encoded")
                )
            },
        ).show(requireActivity().supportFragmentManager, "article_details")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}