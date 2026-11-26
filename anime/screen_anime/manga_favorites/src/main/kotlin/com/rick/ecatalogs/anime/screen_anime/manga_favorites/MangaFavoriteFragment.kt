package com.rick.ecatalogs.anime.screen_anime.manga_favorites

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
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.ecatalogs.anime.screen_anime.common.logScreenView
import com.rick.ecatalogs.anime.screen_anime.manga_favorites.databinding.AnimeScreenAnimeMangaFavoritesFragmentMangaFavoriteBinding
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.anime.data_anime.model.FavoriteUiEvents
import com.rick.ecatalogs.data.ui_components.anime_favorite.AnimeFavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MangaFavoriteFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaFavoritesFragmentMangaFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaFavoriteViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var textTranslator: TextTranslator

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
            // Lets each card offer "Show translation" under its summary
            CompositionLocalProvider(LocalTextTranslator provides textTranslator) {
                EcsTheme {
                    val mangaState by viewModel.feedMangaUiState.collectAsStateWithLifecycle()
                    val shouldDisplayMangaUndoFavorite by viewModel.shouldDisplayMangaUndoFavorite.collectAsStateWithLifecycle()
                    AnimeFavScreen(
                        state = mangaState,
                        onFavClick = { viewModel.onEvent(FavoriteUiEvents.RemoveMangaFavorite(it)) },
                        shouldDisplayUndoFavorite = shouldDisplayMangaUndoFavorite,
                        undoFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoMangaFavoriteRemoval) },
                        clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) },
                        onItemClick = ::openManga,
                    )
                }
            }
        }

        analyticsHelper.logScreenView("mangaFavorite")
        return binding.root
    }

    private fun openManga(id: Int) {
        findNavController().navigateWithMotion(Uri.parse("com.rick.ecs://manga_details_fragment/$id"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}