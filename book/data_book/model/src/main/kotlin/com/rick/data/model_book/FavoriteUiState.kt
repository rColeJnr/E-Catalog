package com.rick.data.model_book

import com.rick.data.model_book.bestseller.UserBestseller
import com.rick.data.model_book.gutenberg.UserGutenberg

sealed interface FavoriteUiState {
    data object Loading : FavoriteUiState
    data class GutenbergFavorites(val favorites: List<UserGutenberg>) : FavoriteUiState
    data class BestsellerFavorites(val favorites: List<UserBestseller>) : FavoriteUiState
}