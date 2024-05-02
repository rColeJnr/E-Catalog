package com.rick.anime.screen_anime.manga_favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.anime.anime_screen.common.logScreenView
import com.rick.anime.screen_anime.manga_favorites.databinding.AnimeScreenAnimeMangaFavoritesFragmentMangaFavoriteBinding
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_anime.FavoriteUiEvents
import com.rick.ui_components.anime_favorite.AnimeFavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MangaFavoriteFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaFavoritesFragmentMangaFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaFavoriteViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeMangaFavoritesFragmentMangaFavoriteBinding.inflate(
            inflater,
            container,
            false
        )

        binding.jikanComposeView.setContent {
            val mangaState by viewModel.feedMangaUiState.collectAsStateWithLifecycle()
            val shouldDisplayMangaUndoFavorite by viewModel.shouldDisplayMangaUndoFavorite.collectAsStateWithLifecycle()
            AnimeFavScreen(
                state = mangaState,
                onFavClick = { viewModel.onEvent(FavoriteUiEvents.RemoveMangaFavorite(it)) },
                shouldDisplayUndoFavorite = shouldDisplayMangaUndoFavorite,
                undoFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoMangaFavoriteRemoval) },
                clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) }
            )
        }

        analyticsHelper.logScreenView("mangaFavorite")
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}