package com.rick.ecatalogs.book.screen_book.gutenberg_favorites

import com.rick.ecatalogs.book.data_book.model.FavoriteUiState
import com.rick.ecatalogs.book.screen_book.common.gutenbergReaderUri
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
import com.rick.ecatalogs.book.screen_book.common.logScreenView
import com.rick.ecatalogs.book.screen_book.gutenberg_favorites.databinding.BookScreenBookGutenbergFavoritesFragmentGutenbergFavoritesBinding
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.book.data_book.model.FavoriteUiEvents
import com.rick.ecatalogs.data.ui_components.book_favorite.BookFavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GutenbergFavoritesFragment : Fragment() {

    private var _binding: BookScreenBookGutenbergFavoritesFragmentGutenbergFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GutenbergFavoriteViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BookScreenBookGutenbergFavoritesFragmentGutenbergFavoritesBinding.inflate(
            inflater,
            container,
            false
        )

        binding.bookComposeView.setContent {
            EcsTheme {
                val gutenbergState by viewModel.feedGutenbergUiState.collectAsStateWithLifecycle()
                BookFavScreen(
                    bestsellerState = gutenbergState,
                    onBestsellerFavClick = {
                        viewModel.onEvent(
                            FavoriteUiEvents.RemoveGutenbergFavorite(it.toInt())
                        )
                    },
                    shouldDisplayBestsellerUndoFavorite = viewModel.shouldDisplayUndoGutenbergFavorite,
                    undoBestsellerFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoGutenbergFavoriteRemoval) },
                    clearUndoState = { viewModel.onEvent(FavoriteUiEvents.ClearUndoState) },
                    onGutenbergClick = ::openBook,
                )
            }
        }
        analyticsHelper.logScreenView("gutenbergFavorite")
        return binding.root
    }

    /** Opens the book in the reader, like the Gutenberg list does. */
    private fun openBook(id: Int) {
        val book = (viewModel.feedGutenbergUiState.value as? FavoriteUiState.GutenbergFavorites)
            ?.favorites?.firstOrNull { it.id == id } ?: return
        val link = book.formats.readableUrl ?: return
        findNavController().navigateWithMotion(gutenbergReaderUri(book.id, book.title, link))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}