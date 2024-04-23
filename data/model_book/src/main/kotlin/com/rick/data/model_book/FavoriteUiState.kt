package com.rick.data.model_book

sealed interface FavoriteUiState {
    data object Loading : FavoriteUiState
    data class GutenbergFavorites(val favorites: List<UserGutenberg>) : FavoriteUiState
    data class BestsellerFavorites(val favorites: List<UserBestseller>) : FavoriteUiState
}