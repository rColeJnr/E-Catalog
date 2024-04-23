package com.rick.screen_book.favorite_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.data.model_book.FavoriteUiEvents
import com.rick.screen_book.databinding.FragmentBookFavoriteBinding
import com.rick.ui_components.book_favorite.BookFavScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookFavoritesFragment : Fragment() {

    private var _binding: FragmentBookFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookFavoriteBinding.inflate(inflater, container, false)

        binding.bookComposeView.setContent {
            val bestSellerState by viewModel.feedBestsellerUiState.collectAsStateWithLifecycle()
            val gutenbergState by viewModel.feedGutenbergUiState.collectAsStateWithLifecycle()
            val showGutenberg by viewModel.showGutenberg.collectAsState()
            val showBestsellers by viewModel.showBestsellers.collectAsState()
            BookFavScreen(
                gutenbergState = gutenbergState,
                bestsellerState = bestSellerState,
                onGutenbergFavClick = {
                    viewModel.onEvent(
                        FavoriteUiEvents.RemoveGutenbergFavorite(
                            it.toInt()
                        )
                    )
                },
                onBestsellerFavClick = {
                    viewModel.onEvent(
                        FavoriteUiEvents.RemoveBestsellerFavorite(it)
                    )
                },
                showGutenberg = showGutenberg,
                showBestsellers = showBestsellers,
                shouldShowBestsellers = {
                    viewModel.onEvent(
                        FavoriteUiEvents.ShouldShowBestsellers(
                            it
                        )
                    )
                },
                shouldShowGutenberg = { viewModel.onEvent(FavoriteUiEvents.ShouldShowGutenberg(it)) },
                shouldDisplayGutenbergUndoFavorite = viewModel.shouldDisplayUndoGutenbergFavorite,
                shouldDisplayBestsellerUndoFavorite = viewModel.shouldDisplayUndoBestsellerFavorite,
                undoBestsellerFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoBestsellerFavoriteRemoval) },
                undoGutenbergFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoGutenbergFavoriteRemoval) },
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