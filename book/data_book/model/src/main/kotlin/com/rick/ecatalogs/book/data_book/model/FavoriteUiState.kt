package com.rick.ecatalogs.book.data_book.model

import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import com.rick.ecatalogs.book.data_book.model.gutenberg.UserGutenberg

sealed interface FavoriteUiState {
    data object Loading : FavoriteUiState
    data class GutenbergFavorites(val favorites: List<UserGutenberg>) : FavoriteUiState
    data class BestsellerFavorites(val favorites: List<UserBestseller>) : FavoriteUiState
}