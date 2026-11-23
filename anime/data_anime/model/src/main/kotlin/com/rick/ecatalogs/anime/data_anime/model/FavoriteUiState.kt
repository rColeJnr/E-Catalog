package com.rick.ecatalogs.anime.data_anime.model

sealed interface FavoriteUiState {
    data object Loading : FavoriteUiState
    data class AnimeFavorites(val favorites: List<UserAnime>) : FavoriteUiState
    data class MangaFavorites(val favorites: List<UserManga>) : FavoriteUiState
}