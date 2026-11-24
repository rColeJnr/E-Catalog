package com.rick.ecatalogs.book.screen_book.bestseller_favorites

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
import com.rick.ecatalogs.book.screen_book.bestseller_favorites.databinding.BookScreenBookBestsellerFavoritesFragmentBestsellerFavoriteBinding
import com.rick.ecatalogs.book.data_book.model.FavoriteUiEvents
import com.rick.ecatalogs.data.ui_components.book_favorite.BookFavScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BestsellerFavoritesFragment : Fragment() {

    private var _binding: BookScreenBookBestsellerFavoritesFragmentBestsellerFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BestsellerFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BookScreenBookBestsellerFavoritesFragmentBestsellerFavoriteBinding.inflate(
            inflater,
            container,
            false
        )

        binding.bookComposeView.setContent {
            EcsTheme {
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
                    onBestsellerClick = ::openBestseller,
                )
            }
        }
        return binding.root
    }

    /** The same details screen as the bestseller list (cover, stores, NYT review). */
    private fun openBestseller(isbn: String) {
        findNavController().navigateWithMotion(Uri.parse("com.rick.ecs://bestseller_details/$isbn"))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}