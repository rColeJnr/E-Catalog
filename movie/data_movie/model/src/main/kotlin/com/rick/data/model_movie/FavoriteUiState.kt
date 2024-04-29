package com.rick.data.model_movie

sealed interface FavoriteUiState {
    data object Loading : FavoriteUiState
    data class TrendingMoviesFavorites(val favorites: List<UserTrendingMovie>) : FavoriteUiState
    data class TrendingSeriesFavorites(val favorites: List<UserTrendingSeries>) : FavoriteUiState
    data class ArticlesFavorites(val favorites: List<UserArticle>) : FavoriteUiState
}
