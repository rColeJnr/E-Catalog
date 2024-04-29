package com.rick.book.screen_book.gutenberg_favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.book.screen_book.gutenberg_favorites.databinding.BookScreenBookGutenbergFavoritesFragmentGutenbergFavoritesBinding
import com.rick.data.model_book.FavoriteUiEvents
import com.rick.ui_components.book_favorite.BookFavScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GutenbergFavoritesFragment : Fragment() {

    private var _binding: BookScreenBookGutenbergFavoritesFragmentGutenbergFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GutenbergFavoriteViewModel by viewModels()

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
            )
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}