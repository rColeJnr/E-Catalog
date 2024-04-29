package com.rick.book.screen_book.bestseller_favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.book.screen_book.bestseller_favorites.databinding.BookScreenBookBestsellerFavoriteFragmentBestsellerFavoriteBinding
import com.rick.data.model_book.FavoriteUiEvents
import com.rick.ui_components.book_favorite.BookFavScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BestsellerFavoritesFragment : Fragment() {

    private var _binding: BookScreenBookBestsellerFavoriteFragmentBestsellerFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BestsellerFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BookScreenBookBestsellerFavoriteFragmentBestsellerFavoriteBinding.inflate(
            inflater,
            container,
            false
        )

        binding.bookComposeView.setContent {
            val bestSellerState by viewModel.feedBestsellerUiState.collectAsStateWithLifecycle()
            BookFavScreen(
                bestsellerState = bestSellerState,
                onBestsellerFavClick = {
                    viewModel.onEvent(
                        FavoriteUiEvents.RemoveBestsellerFavorite(it)
                    )
                },
                shouldDisplayBestsellerUndoFavorite = viewModel.shouldDisplayUndoBestsellerFavorite,
                undoBestsellerFavoriteRemoval = { viewModel.onEvent(FavoriteUiEvents.UndoBestsellerFavoriteRemoval) },
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