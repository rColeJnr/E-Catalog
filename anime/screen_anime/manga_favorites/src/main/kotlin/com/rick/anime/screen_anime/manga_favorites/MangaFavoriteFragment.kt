package com.rick.anime.screen_anime.manga_favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.anime.screen_anime.manga_favorites.databinding.AnimeScreenAnimeMangaFavoritesFragmentMangaFavoriteBinding
import com.rick.data.model_anime.FavoriteUiEvents
import com.rick.ui_components.anime_favorite.AnimeFavScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MangaFavoriteFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaFavoritesFragmentMangaFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaFavoriteViewModel by viewModels()

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
            val animeState by viewModel.feedAnimeUiState.collectAsStateWithLifecycle()
            val mangaState by viewModel.feedMangaUiState.collectAsStateWithLifecycle()
            val showAnime by viewModel.showAnime.collectAsStateWithLifecycle()
            val showManga by viewModel.showManga.collectAsStateWithLifecycle()
            val shouldDisplayAnimeUndoFavorite by viewModel.shouldDisplayAnimeUndoFavorite.collectAsStateWithLifecycle()
            val shouldDisplayMangaUndoFavorite by viewModel.shouldDisplayMangaUndoFavorite.collectAsStateWithLifecycle()
            AnimeFavScreen(
                animeState = animeState,
                mangaState = mangaState,
                onAnimeFavClick = { viewModel.onEvent(FavoriteUiEvents.RemoveAnimeFavorite(it)) },
                onMangaFavClick = { viewModel.onEvent(FavoriteUiEvents.RemoveMangaFavorite(it)) },
                showAnime = showAnime,
                shouldShowAnime = { viewModel.onEvent(FavoriteUiEvents.ShouldShowAnime(it)) },
                showManga = showManga,
                shouldShowManga = { viewModel.onEvent(FavoriteUiEvents.ShouldShowManga(it)) },
                shouldDisplayAnimeUndoFavorite = shouldDisplayAnimeUndoFavorite,
                shouldDisplayMangaUndoFavorite = shouldDisplayMangaUndoFavorite,
                undoAnimeFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoAnimeFavoriteRemoval) },
                undoMangaFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoMangaFavoriteRemoval) },
                clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) }
            )
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}