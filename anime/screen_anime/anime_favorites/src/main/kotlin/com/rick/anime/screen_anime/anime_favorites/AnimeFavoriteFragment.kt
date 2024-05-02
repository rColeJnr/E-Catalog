package com.rick.anime.screen_anime.anime_favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.anime.anime_screen.common.logScreenView
import com.rick.anime.screen_anime.anime_favorites.databinding.AnimeScreenAnimeAnimeFavoritesFragmentAnimeFavoriteBinding
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_anime.FavoriteUiEvents
import com.rick.ui_components.anime_favorite.AnimeFavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AnimeFavoriteFragment : Fragment() {

    private var _binding: AnimeScreenAnimeAnimeFavoritesFragmentAnimeFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeFavoriteViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeAnimeFavoritesFragmentAnimeFavoriteBinding.inflate(
            inflater,
            container,
            false
        )

        binding.jikanComposeView.setContent {
            val animeState by viewModel.feedAnimeUiState.collectAsStateWithLifecycle()
            val shouldDisplayAnimeUndoFavorite by viewModel.shouldDisplayAnimeUndoFavorite.collectAsStateWithLifecycle()
            AnimeFavScreen(
                state = animeState,
                onFavClick = { viewModel.onEvent(FavoriteUiEvents.RemoveAnimeFavorite(it)) },
                shouldDisplayUndoFavorite = shouldDisplayAnimeUndoFavorite,
                undoFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoAnimeFavoriteRemoval) },
                clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) }
            )
        }
        analyticsHelper.logScreenView("AnimeFavorite")
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}